package dias.heimy.service.impl;

import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.domain.entity.Transaction;
import dias.heimy.domain.entity.User;
import dias.heimy.domain.entity.UserBalance;
import dias.heimy.domain.enums.TransactionType;
import dias.heimy.domain.exception.CannotDeleteIncomeException;
import dias.heimy.domain.exception.InsufficientBalanceException;
import dias.heimy.domain.exception.TransactionNotFoundException;
import dias.heimy.domain.exception.TransferTransactionNotModifiableException;
import dias.heimy.domain.exception.UnauthorizedAccessException;
import dias.heimy.domain.exception.UserNotFoundException;
import dias.heimy.dto.mapper.TransactionMapper;
import dias.heimy.dto.request.TransactionRequest;
import dias.heimy.dto.response.TransactionResponse;
import dias.heimy.repository.TransactionRepository;
import dias.heimy.repository.UserBalanceRepository;
import dias.heimy.repository.UserRepository;
import dias.heimy.service.TransactionService;
import dias.heimy.service.UserBalanceService;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final UserBalanceRepository userBalanceRepository;
    private final TransactionMapper transactionMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserBalanceService userBalanceService;

    @Override
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request, String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        User user = findUserByEmail(email);

        UserBalance userBalance = userBalanceService.getUserBalanceWithLockOrCreateDefault(user.getId());

        if (request.type() == TransactionType.EXPENSE) {
            BigDecimal balanceAfterExpense = userBalance.getAccountBalance().subtract(request.amount());
            if (balanceAfterExpense.compareTo(BigDecimal.ZERO) < 0) {
                log.warn(
                        "Saldo insuficiente para criar despesa. Disponível: R$ {}, Solicitado: R$ {}",
                        userBalance.getAccountBalance(),
                        request.amount());
                throw new InsufficientBalanceException();
            }
        }

        BigDecimal totalIncome = userBalance.getTotalIncome();
        BigDecimal totalExpense = userBalance.getTotalExpense();

        if (request.type() == TransactionType.INCOME) {
            totalIncome = totalIncome.add(request.amount());
        } else if (request.type() == TransactionType.EXPENSE) {
            totalExpense = totalExpense.add(request.amount());
        }

        BigDecimal newAccountBalance = totalIncome.subtract(totalExpense).subtract(userBalance.getSavingsBalance());
        userBalance.updateBalance(totalIncome, totalExpense, newAccountBalance, userBalance.getSavingsBalance());

        userBalanceRepository.save(userBalance);

        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setUser(user);
        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info(
                "Transação criada com sucesso: {} - {} - R$ {}",
                savedTransaction.getId(),
                savedTransaction.getType(),
                savedTransaction.getAmount());

        return transactionMapper.toResponse(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(UUID id, String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        User user = findUserByEmail(email);

        Transaction transaction =
                transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));

        validateTransactionOwnership(transaction, user);

        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> listTransactions(Pageable pageable, String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        User user = findUserByEmail(email);

        Page<Transaction> transactions = transactionRepository.findByUser(user, pageable);
        log.info("Listando {} transações para usuário: {}", transactions.getTotalElements(), email);

        return transactions.map(transactionMapper::toResponse);
    }

    @Override
    @Transactional
    public TransactionResponse updateTransaction(UUID id, TransactionRequest request, String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        User user = findUserByEmail(email);

        Transaction transaction =
                transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));

        validateTransactionOwnership(transaction, user);

        if (transaction.getType() == TransactionType.TRANSFER) {
            log.warn(
                    "Tentativa de atualizar transação TRANSFER manualmente: {} - Usuário: {}",
                    transaction.getId(),
                    email);
            throw new TransferTransactionNotModifiableException();
        }

        BigDecimal oldAmount = transaction.getAmount();

        transactionMapper.updateEntityFromRequest(request, transaction);
        Transaction updatedTransaction = transactionRepository.save(transaction);

        userBalanceService.updateBalanceAfterTransactionUpdate(
                user.getId(), updatedTransaction.getType(), oldAmount, updatedTransaction.getAmount());

        log.info("Transação atualizada com sucesso: {}", id);
        return transactionMapper.toResponse(updatedTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(UUID id, String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        User user = findUserByEmail(email);

        Transaction transaction =
                transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));

        validateTransactionOwnership(transaction, user);

        if (transaction.getType() == TransactionType.TRANSFER) {
            log.warn(
                    "Tentativa de deletar transação TRANSFER manualmente: {} - Usuário: {}",
                    transaction.getId(),
                    email);
            throw new TransferTransactionNotModifiableException();
        }

        BigDecimal amount = transaction.getAmount();
        TransactionType type = transaction.getType();

        if (type == TransactionType.INCOME) {
            UserBalance userBalance = userBalanceService.getUserBalanceWithLockOrCreateDefault(user.getId());

            BigDecimal newTotalIncome = userBalance.getTotalIncome().subtract(amount);
            BigDecimal newAccountBalance =
                    newTotalIncome.subtract(userBalance.getTotalExpense()).subtract(userBalance.getSavingsBalance());

            if (newAccountBalance.compareTo(BigDecimal.ZERO) < 0) {
                log.warn(
                        "Tentativa de deletar transação INCOME que resultaria em saldo negativo: {} - Saldo resultante: R$ {} - Usuário: {}",
                        transaction.getId(),
                        newAccountBalance,
                        email);
                throw new CannotDeleteIncomeException();
            }
        }

        transactionRepository.delete(transaction);

        userBalanceService.revertBalanceAfterTransactionDelete(user.getId(), type, amount);

        log.info("Transação deletada com sucesso: {}", id);
    }

    private String extractEmailFromToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return jwtTokenProvider.extractEmailFromToken(token);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    private void validateTransactionOwnership(Transaction transaction, User user) {
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("esta transação");
        }
    }
}

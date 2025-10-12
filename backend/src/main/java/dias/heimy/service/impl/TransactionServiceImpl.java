package dias.heimy.service.impl;

import static dias.heimy.domain.enums.ErrorCode.TRANSACTION_NOT_FOUND;
import static dias.heimy.domain.enums.ErrorCode.UNAUTHORIZED_ACCESS;
import static dias.heimy.domain.enums.ErrorCode.USER_NOT_FOUND;

import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.domain.entity.Transaction;
import dias.heimy.domain.entity.User;
import dias.heimy.domain.enums.TransactionType;
import dias.heimy.domain.exception.DomainException;
import dias.heimy.dto.mapper.TransactionMapper;
import dias.heimy.dto.request.TransactionRequest;
import dias.heimy.dto.response.MonthlyBalanceResponse;
import dias.heimy.dto.response.TransactionResponse;
import dias.heimy.repository.TransactionRepository;
import dias.heimy.repository.UserRepository;
import dias.heimy.service.TransactionService;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    private static final String TRANSACTION_NOT_FOUND_MESSAGE = "Transação não encontrada: ";

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request, String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        User user = findUserByEmail(email);

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

        Transaction transaction = transactionRepository
                .findById(id)
                .orElseThrow(() -> new DomainException(TRANSACTION_NOT_FOUND, TRANSACTION_NOT_FOUND_MESSAGE + id));

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

        Transaction transaction = transactionRepository
                .findById(id)
                .orElseThrow(() -> new DomainException(TRANSACTION_NOT_FOUND, TRANSACTION_NOT_FOUND_MESSAGE + id));

        validateTransactionOwnership(transaction, user);

        transactionMapper.updateEntityFromRequest(request, transaction);
        Transaction updatedTransaction = transactionRepository.save(transaction);

        log.info("Transação atualizada com sucesso: {}", id);
        return transactionMapper.toResponse(updatedTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(UUID id, String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        User user = findUserByEmail(email);

        Transaction transaction = transactionRepository
                .findById(id)
                .orElseThrow(() -> new DomainException(TRANSACTION_NOT_FOUND, TRANSACTION_NOT_FOUND_MESSAGE + id));

        validateTransactionOwnership(transaction, user);

        transactionRepository.delete(transaction);
        log.info("Transação deletada com sucesso: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public MonthlyBalanceResponse getMonthlyBalance(int year, int month, String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        User user = findUserByEmail(email);

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        BigDecimal totalIncome =
                transactionRepository.sumByUserAndTypeAndDateBetween(user, TransactionType.INCOME, startDate, endDate);

        BigDecimal totalExpense =
                transactionRepository.sumByUserAndTypeAndDateBetween(user, TransactionType.EXPENSE, startDate, endDate);

        log.info("Saldo calculado para {}/{}: Receitas=R$ {}, Despesas=R$ {}", year, month, totalIncome, totalExpense);

        return MonthlyBalanceResponse.of(year, month, totalIncome, totalExpense);
    }

    private String extractEmailFromToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return jwtTokenProvider.extractEmailFromToken(token);
    }

    private User findUserByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new DomainException(USER_NOT_FOUND, "Usuário não encontrado: " + email));
    }

    private void validateTransactionOwnership(Transaction transaction, User user) {
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new DomainException(UNAUTHORIZED_ACCESS, "Você não tem permissão para acessar esta transação");
        }
    }
}

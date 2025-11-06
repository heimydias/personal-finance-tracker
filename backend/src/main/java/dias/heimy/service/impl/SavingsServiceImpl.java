package dias.heimy.service.impl;

import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.domain.entity.Savings;
import dias.heimy.domain.entity.Transaction;
import dias.heimy.domain.entity.User;
import dias.heimy.domain.enums.TransactionType;
import dias.heimy.domain.enums.TransferType;
import dias.heimy.domain.exception.InsufficientBalanceException;
import dias.heimy.domain.exception.SavingsNotFoundException;
import dias.heimy.domain.exception.UnauthorizedAccessException;
import dias.heimy.domain.exception.UserNotFoundException;
import dias.heimy.dto.mapper.SavingsMapper;
import dias.heimy.dto.request.SavingsRequest;
import dias.heimy.dto.response.ConsolidatedYieldResponse;
import dias.heimy.dto.response.MonthlyYieldResponse;
import dias.heimy.dto.response.SavingsResponse;
import dias.heimy.repository.SavingsRepository;
import dias.heimy.repository.TransactionRepository;
import dias.heimy.repository.UserRepository;
import dias.heimy.service.SavingsService;
import dias.heimy.service.UserBalanceService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
public class SavingsServiceImpl implements SavingsService {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private final SavingsRepository savingsRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final SavingsMapper savingsMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserBalanceService userBalanceService;

    @Override
    @Transactional
    public SavingsResponse createSavings(SavingsRequest request, String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        User user = findUserByEmail(email);

        BigDecimal accountBalanceAtDate =
                userBalanceService.getAccountBalanceUntilDate(user, request.transactionDate());
        if (accountBalanceAtDate.compareTo(request.amount()) < 0) {
            log.warn(
                    "Saldo insuficiente em {} para criar poupança. Disponível: R$ {}, Solicitado: R$ {}",
                    request.transactionDate(),
                    accountBalanceAtDate,
                    request.amount());
            throw new InsufficientBalanceException();
        }

        Transaction depositTransaction = createTransferTransaction(
                user,
                request.amount(),
                "Depósito em Poupança: " + request.name(),
                "POUPANÇA - " + request.type().getDescription(),
                request.transactionDate(),
                TransferType.DEPOSIT);

        Savings savings = savingsMapper.toEntity(request, user);
        savings.setDepositTransaction(depositTransaction);
        Savings savedSavings = savingsRepository.save(savings);

        userBalanceService.moveToSavings(user.getId(), savedSavings.getAmount());

        log.info(
                "Poupança criada com sucesso: {} - {} - R$ {} - Taxa: {}%",
                savedSavings.getId(), savedSavings.getName(), savedSavings.getAmount(), savedSavings.getInterestRate());

        return savingsMapper.toResponse(savedSavings);
    }

    @Override
    @Transactional(readOnly = true)
    public SavingsResponse getSavingsById(UUID id, String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        User user = findUserByEmail(email);

        Savings savings = savingsRepository.findById(id).orElseThrow(() -> new SavingsNotFoundException(id));

        validateSavingsOwnership(savings, user);

        return savingsMapper.toResponse(savings);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SavingsResponse> listSavings(Pageable pageable, String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        User user = findUserByEmail(email);

        Page<Savings> savings = savingsRepository.findByUserId(user.getId(), pageable);
        log.info("Listando {} poupanças para usuário: {}", savings.getTotalElements(), email);

        return savings.map(savingsMapper::toResponse);
    }

    @Override
    @Transactional
    public SavingsResponse updateSavings(UUID id, SavingsRequest request, String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        User user = findUserByEmail(email);

        Savings savings = savingsRepository.findById(id).orElseThrow(() -> new SavingsNotFoundException(id));

        validateSavingsOwnership(savings, user);

        BigDecimal oldAmount = savings.getAmount();
        savingsMapper.updateEntity(savings, request);
        Savings updatedSavings = savingsRepository.save(savings);

        userBalanceService.updateSavingsBalance(user.getId(), oldAmount, updatedSavings.getAmount());

        log.info("Poupança atualizada com sucesso: {}", id);
        return savingsMapper.toResponse(updatedSavings);
    }

    @Override
    @Transactional
    public void deleteSavings(UUID id, String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        User user = findUserByEmail(email);

        Savings savings = savingsRepository.findById(id).orElseThrow(() -> new SavingsNotFoundException(id));

        validateSavingsOwnership(savings, user);

        BigDecimal amount = savings.getAmount();

        createTransferTransaction(
                user,
                amount,
                "Resgate de Poupança: " + savings.getName(),
                "POUPANÇA - " + savings.getType().getDescription(),
                LocalDate.now(),
                TransferType.WITHDRAWAL);

        savingsRepository.delete(savings);

        userBalanceService.moveFromSavings(user.getId(), amount);

        log.info("Poupança deletada com sucesso: {} - Valor de R$ {} devolvido à conta", id, amount);
    }

    @Override
    @Transactional(readOnly = true)
    public MonthlyYieldResponse calculateMonthlyYield(UUID id, String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        User user = findUserByEmail(email);

        Savings savings = savingsRepository.findById(id).orElseThrow(() -> new SavingsNotFoundException(id));

        validateSavingsOwnership(savings, user);

        BigDecimal monthlyYield =
                savings.getAmount().multiply(savings.getInterestRate()).divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);

        BigDecimal totalAfterYield = savings.getAmount().add(monthlyYield);

        log.info(
                "Rendimento calculado para poupança {}: R$ {} -> R$ {} (rendimento: R$ {})",
                savings.getName(),
                savings.getAmount(),
                totalAfterYield,
                monthlyYield);

        return new MonthlyYieldResponse(
                savings.getId(),
                savings.getName(),
                savings.getAmount(),
                savings.getInterestRate(),
                monthlyYield,
                totalAfterYield);
    }

    @Override
    @Transactional(readOnly = true)
    public ConsolidatedYieldResponse calculateConsolidatedYield(String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        User user = findUserByEmail(email);

        List<Savings> allSavings =
                savingsRepository.findByUserId(user.getId(), Pageable.unpaged()).getContent();

        if (allSavings.isEmpty()) {
            log.info("Usuário {} não possui poupanças", email);
            return ConsolidatedYieldResponse.of(
                    0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, List.of());
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalMonthlyYield = BigDecimal.ZERO;
        BigDecimal weightedRateSum = BigDecimal.ZERO;
        List<MonthlyYieldResponse> savingsYields = new ArrayList<>();

        for (Savings savings : allSavings) {
            BigDecimal monthlyYield = savings.getAmount()
                    .multiply(savings.getInterestRate())
                    .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);

            BigDecimal totalAfterYield = savings.getAmount().add(monthlyYield);

            savingsYields.add(new MonthlyYieldResponse(
                    savings.getId(),
                    savings.getName(),
                    savings.getAmount(),
                    savings.getInterestRate(),
                    monthlyYield,
                    totalAfterYield));

            totalAmount = totalAmount.add(savings.getAmount());
            totalMonthlyYield = totalMonthlyYield.add(monthlyYield);
            weightedRateSum = weightedRateSum.add(savings.getInterestRate().multiply(savings.getAmount()));
        }

        BigDecimal averageInterestRate = totalAmount.compareTo(BigDecimal.ZERO) > 0
                ? weightedRateSum.divide(totalAmount, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal totalAfterYield = totalAmount.add(totalMonthlyYield);

        log.info(
                "Rendimento consolidado calculado para usuário {}: {} poupanças, Total: R$ {}, Taxa média: {}%, Rendimento: R$ {}",
                email, allSavings.size(), totalAmount, averageInterestRate, totalMonthlyYield);

        return ConsolidatedYieldResponse.of(
                allSavings.size(), totalAmount, averageInterestRate, totalMonthlyYield, totalAfterYield, savingsYields);
    }

    private String extractEmailFromToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return jwtTokenProvider.extractEmailFromToken(token);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    private void validateSavingsOwnership(Savings savings, User user) {
        if (!savings.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("esta poupança");
        }
    }

    private Transaction createTransferTransaction(
            User user,
            BigDecimal amount,
            String description,
            String category,
            LocalDate transactionDate,
            TransferType transferType) {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.TRANSFER);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setTransactionDate(transactionDate);
        transaction.setDescription(description);
        transaction.setTransferType(transferType);
        transaction.setUser(user);
        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info(
                "Transação de transferência criada: R$ {} - {} - Data: {} - Tipo: {}",
                amount,
                description,
                transactionDate,
                transferType);
        return savedTransaction;
    }
}

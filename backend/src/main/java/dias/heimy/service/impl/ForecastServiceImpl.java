package dias.heimy.service.impl;

import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.domain.entity.Savings;
import dias.heimy.domain.entity.User;
import dias.heimy.domain.entity.UserBalance;
import dias.heimy.domain.enums.TransactionType;
import dias.heimy.domain.exception.UserNotFoundException;
import dias.heimy.dto.response.ForecastResponse;
import dias.heimy.dto.response.MonthlyHistoryResponse;
import dias.heimy.repository.SavingsRepository;
import dias.heimy.repository.TransactionRepository;
import dias.heimy.repository.UserBalanceRepository;
import dias.heimy.repository.UserRepository;
import dias.heimy.service.ForecastService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForecastServiceImpl implements ForecastService {

    private final TransactionRepository transactionRepository;
    private final SavingsRepository savingsRepository;
    private final UserRepository userRepository;
    private final UserBalanceRepository userBalanceRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional(readOnly = true)
    public ForecastResponse calculateForecast(int monthsToAnalyze, String authorizationHeader) {
        int validMonths = Math.max(1, Math.min(12, monthsToAnalyze));

        String email = extractEmailFromToken(authorizationHeader);
        User user = findUserByEmail(email);

        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusMonths(1);
        int targetYear = targetDate.getYear();
        int targetMonth = targetDate.getMonthValue();

        List<MonthlyHistoryResponse> history = calculateMonthlyHistory(user, validMonths, today);

        int actualMonthsAnalyzed = history.size();

        BigDecimal averageIncome = BigDecimal.ZERO;
        BigDecimal averageExpense = BigDecimal.ZERO;
        BigDecimal averageSavingsDeposit = BigDecimal.ZERO;

        if (actualMonthsAnalyzed > 0) {
            BigDecimal totalIncome = BigDecimal.ZERO;
            BigDecimal totalExpense = BigDecimal.ZERO;
            BigDecimal totalSavingsDeposit = BigDecimal.ZERO;

            for (MonthlyHistoryResponse month : history) {
                totalIncome = totalIncome.add(month.income());
                totalExpense = totalExpense.add(month.expense());
                totalSavingsDeposit = totalSavingsDeposit.add(month.savingsDeposit());
            }

            BigDecimal monthCount = BigDecimal.valueOf(actualMonthsAnalyzed);
            averageIncome = totalIncome.divide(monthCount, 2, RoundingMode.HALF_UP);
            averageExpense = totalExpense.divide(monthCount, 2, RoundingMode.HALF_UP);
            averageSavingsDeposit = totalSavingsDeposit.divide(monthCount, 2, RoundingMode.HALF_UP);
        }

        BigDecimal projectedIncome = averageIncome;
        BigDecimal projectedExpense = averageExpense;

        BigDecimal projectedSavingsYield = calculateProjectedSavingsYield(user);

        UserBalance currentBalance = getUserBalanceOrDefault(user);
        BigDecimal currentAccountBalance = currentBalance.getAccountBalance();
        BigDecimal currentSavingsBalance = currentBalance.getSavingsBalance();
        BigDecimal currentTotalBalance = currentBalance.getTotalBalance();

        BigDecimal projectedAccountBalance =
                currentAccountBalance.add(projectedIncome).subtract(projectedExpense);

        BigDecimal projectedSavingsBalance = currentSavingsBalance.add(projectedSavingsYield);

        BigDecimal projectedTotalBalance = projectedAccountBalance.add(projectedSavingsBalance);

        log.info(
                "Previsão calculada para {}/{} - Média Receitas: R$ {}, Média Despesas: R$ {}, "
                        + "Rendimento Poupança: R$ {}, Saldo Projetado: R$ {}",
                targetYear,
                targetMonth,
                averageIncome,
                averageExpense,
                projectedSavingsYield,
                projectedTotalBalance);

        return new ForecastResponse(
                targetYear,
                targetMonth,
                actualMonthsAnalyzed,
                averageIncome,
                averageExpense,
                averageSavingsDeposit,
                projectedIncome,
                projectedExpense,
                projectedSavingsYield,
                projectedAccountBalance,
                projectedSavingsBalance,
                projectedTotalBalance,
                currentAccountBalance,
                currentSavingsBalance,
                currentTotalBalance,
                history);
    }

    private List<MonthlyHistoryResponse> calculateMonthlyHistory(User user, int monthsToAnalyze, LocalDate today) {
        List<MonthlyHistoryResponse> history = new ArrayList<>();

        LocalDate previousMonth = today.minusMonths(1);

        for (int i = 0; i < monthsToAnalyze; i++) {
            LocalDate targetMonth = previousMonth.minusMonths(i);
            int year = targetMonth.getYear();
            int month = targetMonth.getMonthValue();

            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

            BigDecimal income = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.INCOME, startDate, endDate);
            BigDecimal expense = transactionRepository.sumByUserAndTypeAndDateBetween(
                    user, TransactionType.EXPENSE, startDate, endDate);
            BigDecimal savingsDeposit =
                    transactionRepository.sumSavingsDepositsByUserAndDateBetween(user, startDate, endDate);

            if (income == null) income = BigDecimal.ZERO;
            if (expense == null) expense = BigDecimal.ZERO;
            if (savingsDeposit == null) savingsDeposit = BigDecimal.ZERO;

            if (income.compareTo(BigDecimal.ZERO) > 0
                    || expense.compareTo(BigDecimal.ZERO) > 0
                    || savingsDeposit.compareTo(BigDecimal.ZERO) > 0) {
                history.add(MonthlyHistoryResponse.of(year, month, income, expense, savingsDeposit));
            }
        }

        return history;
    }

    private BigDecimal calculateProjectedSavingsYield(User user) {
        List<Savings> allSavings =
                savingsRepository.findByUserId(user.getId(), Pageable.unpaged()).getContent();

        BigDecimal totalYield = BigDecimal.ZERO;

        for (Savings savings : allSavings) {
            if (savings.getAmount() != null
                    && savings.getInterestRate() != null
                    && savings.getInterestRate().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal monthlyYield = savings.getAmount()
                        .multiply(savings.getInterestRate())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                totalYield = totalYield.add(monthlyYield);
            }
        }

        return totalYield;
    }

    private UserBalance getUserBalanceOrDefault(User user) {
        return userBalanceRepository.findByUserId(user.getId()).orElseGet(() -> {
            UserBalance defaultBalance = new UserBalance();
            defaultBalance.setUser(user);
            defaultBalance.setTotalIncome(BigDecimal.ZERO);
            defaultBalance.setTotalExpense(BigDecimal.ZERO);
            defaultBalance.setAccountBalance(BigDecimal.ZERO);
            defaultBalance.setSavingsBalance(BigDecimal.ZERO);
            defaultBalance.setTotalBalance(BigDecimal.ZERO);
            return defaultBalance;
        });
    }

    private String extractEmailFromToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return jwtTokenProvider.extractEmailFromToken(token);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }
}

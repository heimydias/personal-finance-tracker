package dias.heimy.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.domain.entity.Savings;
import dias.heimy.domain.entity.User;
import dias.heimy.domain.entity.UserBalance;
import dias.heimy.domain.enums.SavingsType;
import dias.heimy.domain.enums.TransactionType;
import dias.heimy.domain.enums.UserRole;
import dias.heimy.domain.exception.UserNotFoundException;
import dias.heimy.repository.SavingsRepository;
import dias.heimy.repository.TransactionRepository;
import dias.heimy.repository.UserBalanceRepository;
import dias.heimy.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for ForecastServiceImpl")
class ForecastServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SavingsRepository savingsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private ForecastServiceImpl forecastService;

    @Test
    @DisplayName("Should calculate forecast with 3 months history successfully")
    void shouldCalculateForecast_WithThreeMonthsHistory() {
        var user = createTestUser();
        var userBalance = createTestUserBalance(user);
        var savings = createTestSavings(user);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.of(userBalance));
        when(savingsRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(savings)));

        mockTransactionSums(user, new BigDecimal("3000.00"), new BigDecimal("1500.00"), new BigDecimal("500.00"));

        var result = forecastService.calculateForecast(3, "Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.monthsAnalyzed()).isLessThanOrEqualTo(3);
        assertThat(result.averageIncome()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        assertThat(result.averageExpense()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        assertThat(result.projectedSavingsYield()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        assertThat(result.currentAccountBalance()).isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(result.currentSavingsBalance()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    @DisplayName("Should calculate forecast with 6 months history")
    void shouldCalculateForecast_WithSixMonthsHistory() {
        var user = createTestUser();
        var userBalance = createTestUserBalance(user);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.of(userBalance));
        when(savingsRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockTransactionSums(user, new BigDecimal("4000.00"), new BigDecimal("2000.00"), new BigDecimal("300.00"));

        var result = forecastService.calculateForecast(6, "Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.monthsAnalyzed()).isLessThanOrEqualTo(6);
    }

    @Test
    @DisplayName("Should calculate forecast without history (new user)")
    void shouldCalculateForecast_WithoutHistory() {
        var user = createTestUser();
        var userBalance = createTestUserBalance(user);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.of(userBalance));
        when(savingsRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                        eq(user), eq(TransactionType.INCOME), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                        eq(user), eq(TransactionType.EXPENSE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumSavingsDepositsByUserAndDateBetween(
                        eq(user), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);

        var result = forecastService.calculateForecast(3, "Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.monthsAnalyzed()).isZero();
        assertThat(result.averageIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.averageExpense()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.projectedIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.projectedExpense()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.history()).isEmpty();
    }

    @Test
    @DisplayName("Should calculate forecast with only income")
    void shouldCalculateForecast_WithOnlyIncome() {
        var user = createTestUser();
        var userBalance = createTestUserBalance(user);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.of(userBalance));
        when(savingsRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                        eq(user), eq(TransactionType.INCOME), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("5000.00"));
        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                        eq(user), eq(TransactionType.EXPENSE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumSavingsDepositsByUserAndDateBetween(
                        eq(user), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);

        var result = forecastService.calculateForecast(3, "Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.averageIncome()).isGreaterThan(BigDecimal.ZERO);
        assertThat(result.averageExpense()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should calculate forecast with only expense")
    void shouldCalculateForecast_WithOnlyExpense() {
        var user = createTestUser();
        var userBalance = createTestUserBalance(user);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.of(userBalance));
        when(savingsRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                        eq(user), eq(TransactionType.INCOME), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                        eq(user), eq(TransactionType.EXPENSE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("2000.00"));
        when(transactionRepository.sumSavingsDepositsByUserAndDateBetween(
                        eq(user), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);

        var result = forecastService.calculateForecast(3, "Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.averageIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.averageExpense()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should calculate forecast with only savings deposit")
    void shouldCalculateForecast_WithOnlySavingsDeposit() {
        var user = createTestUser();
        var userBalance = createTestUserBalance(user);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.of(userBalance));
        when(savingsRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                        eq(user), eq(TransactionType.INCOME), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                        eq(user), eq(TransactionType.EXPENSE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumSavingsDepositsByUserAndDateBetween(
                        eq(user), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("1000.00"));

        var result = forecastService.calculateForecast(3, "Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.averageIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.averageExpense()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.averageSavingsDeposit()).isGreaterThan(BigDecimal.ZERO);
        assertThat(result.history()).isNotEmpty();
    }

    @Test
    @DisplayName("Should calculate forecast including savings yield")
    void shouldCalculateForecast_IncludingSavingsYield() {
        var user = createTestUser();
        var userBalance = createTestUserBalance(user);
        var savings1 = createTestSavings(user, "Poupança 1", new BigDecimal("10000.00"), new BigDecimal("1.00"));
        var savings2 = createTestSavings(user, "Poupança 2", new BigDecimal("5000.00"), new BigDecimal("0.50"));

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.of(userBalance));
        when(savingsRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(savings1, savings2)));

        mockTransactionSums(user, new BigDecimal("3000.00"), new BigDecimal("1500.00"), BigDecimal.ZERO);

        var result = forecastService.calculateForecast(3, "Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.projectedSavingsYield()).isEqualByComparingTo(new BigDecimal("125.00"));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowException_WhenUserNotFound() {
        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> forecastService.calculateForecast(3, "Bearer token"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("Should create default balance when user has no balance")
    void shouldCreateDefaultBalance_WhenNoBalanceExists() {
        var user = createTestUser();

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.empty());
        when(savingsRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                        eq(user), any(TransactionType.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumSavingsDepositsByUserAndDateBetween(
                        eq(user), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);

        var result = forecastService.calculateForecast(3, "Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.currentAccountBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.currentSavingsBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.currentTotalBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should handle months parameter below minimum")
    void shouldHandleMonthsParameter_BelowMinimum() {
        var user = createTestUser();
        var userBalance = createTestUserBalance(user);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.of(userBalance));
        when(savingsRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockTransactionSums(user, new BigDecimal("3000.00"), new BigDecimal("1500.00"), BigDecimal.ZERO);

        var result = forecastService.calculateForecast(0, "Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.monthsAnalyzed()).isLessThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Should handle months parameter above maximum")
    void shouldHandleMonthsParameter_AboveMaximum() {
        var user = createTestUser();
        var userBalance = createTestUserBalance(user);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.of(userBalance));
        when(savingsRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockTransactionSums(user, new BigDecimal("3000.00"), new BigDecimal("1500.00"), BigDecimal.ZERO);

        var result = forecastService.calculateForecast(24, "Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.monthsAnalyzed()).isLessThanOrEqualTo(12);
    }

    @Test
    @DisplayName("Should calculate target year and month correctly")
    void shouldCalculateTargetYearAndMonth_Correctly() {
        var user = createTestUser();
        var userBalance = createTestUserBalance(user);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.of(userBalance));
        when(savingsRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        mockTransactionSums(user, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        var result = forecastService.calculateForecast(3, "Bearer token");

        LocalDate nextMonth = LocalDate.now().plusMonths(1);
        assertThat(result.targetYear()).isEqualTo(nextMonth.getYear());
        assertThat(result.targetMonth()).isEqualTo(nextMonth.getMonthValue());
    }

    @Test
    @DisplayName("Should handle null values from transaction repository")
    void shouldHandleNullValues_FromTransactionRepository() {
        var user = createTestUser();
        var userBalance = createTestUserBalance(user);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.of(userBalance));
        when(savingsRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                        eq(user), eq(TransactionType.INCOME), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(null);
        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                        eq(user), eq(TransactionType.EXPENSE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(null);
        when(transactionRepository.sumSavingsDepositsByUserAndDateBetween(
                        eq(user), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(null);

        var result = forecastService.calculateForecast(3, "Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.history()).isEmpty();
    }

    @Test
    @DisplayName("Should handle savings with zero interest rate")
    void shouldHandleSavings_WithZeroInterestRate() {
        var user = createTestUser();
        var userBalance = createTestUserBalance(user);
        var savings = createTestSavings(user, "Poupança Zero", new BigDecimal("10000.00"), BigDecimal.ZERO);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.of(userBalance));
        when(savingsRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(savings)));

        mockTransactionSums(user, new BigDecimal("3000.00"), new BigDecimal("1500.00"), BigDecimal.ZERO);

        var result = forecastService.calculateForecast(3, "Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.projectedSavingsYield()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should handle savings with null amount")
    void shouldHandleSavings_WithNullAmount() {
        var user = createTestUser();
        var userBalance = createTestUserBalance(user);
        var savings = createTestSavings(user, "Poupança Null Amount", null, new BigDecimal("1.00"));

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.of(userBalance));
        when(savingsRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(savings)));

        mockTransactionSums(user, new BigDecimal("3000.00"), new BigDecimal("1500.00"), BigDecimal.ZERO);

        var result = forecastService.calculateForecast(3, "Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.projectedSavingsYield()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should handle savings with null interest rate")
    void shouldHandleSavings_WithNullInterestRate() {
        var user = createTestUser();
        var userBalance = createTestUserBalance(user);
        var savings = createTestSavings(user, "Poupança Null Rate", new BigDecimal("10000.00"), null);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.of(userBalance));
        when(savingsRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(savings)));

        mockTransactionSums(user, new BigDecimal("3000.00"), new BigDecimal("1500.00"), BigDecimal.ZERO);

        var result = forecastService.calculateForecast(3, "Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.projectedSavingsYield()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should calculate projected balances correctly")
    void shouldCalculateProjectedBalances_Correctly() {
        var user = createTestUser();
        var userBalance = createTestUserBalance(user);
        var savings = createTestSavings(user, "Poupança", new BigDecimal("500.00"), new BigDecimal("1.00"));

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.of(userBalance));
        when(savingsRepository.findByUserId(eq(user.getId()), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(savings)));

        mockTransactionSums(user, new BigDecimal("3000.00"), new BigDecimal("1500.00"), BigDecimal.ZERO);

        var result = forecastService.calculateForecast(3, "Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.projectedSavingsYield()).isEqualByComparingTo(new BigDecimal("5.00"));

        BigDecimal expectedProjectedAccount =
                result.currentAccountBalance().add(result.projectedIncome()).subtract(result.projectedExpense());
        assertThat(result.projectedAccountBalance()).isEqualByComparingTo(expectedProjectedAccount);

        BigDecimal expectedProjectedSavings = result.currentSavingsBalance().add(result.projectedSavingsYield());
        assertThat(result.projectedSavingsBalance()).isEqualByComparingTo(expectedProjectedSavings);

        BigDecimal expectedProjectedTotal = result.projectedAccountBalance().add(result.projectedSavingsBalance());
        assertThat(result.projectedTotalBalance()).isEqualByComparingTo(expectedProjectedTotal);
    }

    private void mockTransactionSums(User user, BigDecimal income, BigDecimal expense, BigDecimal savingsDeposit) {
        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                        eq(user), eq(TransactionType.INCOME), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(income);
        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                        eq(user), eq(TransactionType.EXPENSE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(expense);
        when(transactionRepository.sumSavingsDepositsByUserAndDateBetween(
                        eq(user), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(savingsDeposit);
    }

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encoded_password");
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setCreatedBy("SYSTEM");
        user.setLastModifiedBy("SYSTEM");
        return user;
    }

    private UserBalance createTestUserBalance(User user) {
        UserBalance userBalance = new UserBalance();
        userBalance.setId(UUID.randomUUID());
        userBalance.setUser(user);
        userBalance.setTotalIncome(new BigDecimal("5000.00"));
        userBalance.setTotalExpense(new BigDecimal("2000.00"));
        userBalance.setAccountBalance(new BigDecimal("3000.00"));
        userBalance.setSavingsBalance(new BigDecimal("500.00"));
        userBalance.setTotalBalance(new BigDecimal("3500.00"));
        userBalance.setCreatedAt(LocalDateTime.now());
        userBalance.setUpdatedAt(LocalDateTime.now());
        return userBalance;
    }

    private Savings createTestSavings(User user) {
        return createTestSavings(user, "Reserva de Emergência", new BigDecimal("500.00"), new BigDecimal("1.00"));
    }

    private Savings createTestSavings(User user, String name, BigDecimal amount, BigDecimal interestRate) {
        Savings savings = new Savings();
        savings.setId(UUID.randomUUID());
        savings.setUser(user);
        savings.setName(name);
        savings.setType(SavingsType.EMERGENCY_FUND);
        savings.setAmount(amount);
        savings.setInterestRate(interestRate);
        savings.setDescription("Savings for testing");
        savings.setTransactionDate(LocalDate.now());
        savings.setCreatedAt(LocalDateTime.now());
        savings.setUpdatedAt(LocalDateTime.now());
        return savings;
    }
}

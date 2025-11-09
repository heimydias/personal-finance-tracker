package dias.heimy.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.domain.entity.User;
import dias.heimy.domain.entity.UserBalance;
import dias.heimy.domain.enums.TransactionType;
import dias.heimy.domain.enums.UserRole;
import dias.heimy.domain.exception.UserNotFoundException;
import dias.heimy.repository.TransactionRepository;
import dias.heimy.repository.UserBalanceRepository;
import dias.heimy.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for UserBalanceServiceImpl")
class UserBalanceServiceImplTest {

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserBalanceServiceImpl userBalanceService;

    @Test
    @DisplayName("Should get current balance successfully")
    void shouldGetCurrentBalance_WhenValidUser() {

        var user = createTestUser();
        var userBalance = createTestUserBalance(user);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.of(userBalance));

        var result = userBalanceService.getCurrentBalance("Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.accountBalance()).isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(result.savingsBalance()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(result.totalBalance()).isEqualByComparingTo(new BigDecimal("3500.00"));
    }

    @Test
    @DisplayName("Should create default balance when user has no balance")
    void shouldCreateDefaultBalance_WhenNoBalanceExists() {

        var user = createTestUser();
        var defaultBalance = createDefaultBalance(user);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userBalanceRepository.findByUserId(user.getId())).thenReturn(Optional.of(defaultBalance));

        var result = userBalanceService.getCurrentBalance("Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.totalIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.totalExpense()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should get monthly balance successfully")
    void shouldGetMonthlyBalance_WhenValidYearAndMonth() {

        var user = createTestUser();
        var year = 2024;
        var month = 10;
        var startDate = LocalDate.of(year, month, 1);
        var endDate = LocalDate.of(year, month, 31);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                        user, TransactionType.INCOME, startDate, endDate))
                .thenReturn(new BigDecimal("3000.00"));
        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                        user, TransactionType.EXPENSE, startDate, endDate))
                .thenReturn(new BigDecimal("1500.00"));
        when(transactionRepository.sumSavingsDepositsByUserAndDateBetween(user, startDate, endDate))
                .thenReturn(new BigDecimal("500.00"));
        when(transactionRepository.sumSavingsWithdrawalsByUserAndDateBetween(user, startDate, endDate))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumByUserAndTypeAndDateBefore(
                        eq(user), eq(TransactionType.INCOME), any(LocalDate.class)))
                .thenReturn(new BigDecimal("3000.00"));
        when(transactionRepository.sumByUserAndTypeAndDateBefore(
                        eq(user), eq(TransactionType.EXPENSE), any(LocalDate.class)))
                .thenReturn(new BigDecimal("1500.00"));
        when(transactionRepository.sumSavingsDepositsByUserAndDateBefore(eq(user), any(LocalDate.class)))
                .thenReturn(new BigDecimal("500.00"));
        when(transactionRepository.sumSavingsWithdrawalsByUserAndDateBefore(eq(user), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);

        var result = userBalanceService.getMonthlyBalance(year, month, "Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.year()).isEqualTo(year);
        assertThat(result.month()).isEqualTo(month);
        assertThat(result.totalIncome()).isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(result.totalExpense()).isEqualByComparingTo(new BigDecimal("1500.00"));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowException_WhenUserNotFound() {

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userBalanceService.getCurrentBalance("Bearer token"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("Should move to savings successfully")
    void shouldMoveToSavings() {

        var userId = UUID.randomUUID();
        var amount = new BigDecimal("500.00");
        var userBalance = new UserBalance();
        userBalance.setAccountBalance(new BigDecimal("1000.00"));
        userBalance.setSavingsBalance(new BigDecimal("500.00"));
        userBalance.setTotalBalance(new BigDecimal("1500.00"));

        when(userBalanceRepository.findByUserIdWithLock(userId)).thenReturn(Optional.of(userBalance));
        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(userBalance);

        userBalanceService.moveToSavings(userId, amount);

        verify(userBalanceRepository).save(userBalance);
        assertThat(userBalance.getAccountBalance()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(userBalance.getSavingsBalance()).isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    @Test
    @DisplayName("Should move from savings successfully")
    void shouldMoveFromSavings() {

        var userId = UUID.randomUUID();
        var amount = new BigDecimal("300.00");
        var userBalance = new UserBalance();
        userBalance.setAccountBalance(new BigDecimal("500.00"));
        userBalance.setSavingsBalance(new BigDecimal("1000.00"));
        userBalance.setTotalBalance(new BigDecimal("1500.00"));

        when(userBalanceRepository.findByUserIdWithLock(userId)).thenReturn(Optional.of(userBalance));
        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(userBalance);

        userBalanceService.moveFromSavings(userId, amount);

        verify(userBalanceRepository).save(userBalance);
        assertThat(userBalance.getAccountBalance()).isEqualByComparingTo(new BigDecimal("800.00"));
        assertThat(userBalance.getSavingsBalance()).isEqualByComparingTo(new BigDecimal("700.00"));
    }

    @Test
    @DisplayName("Should get account balance until date successfully")
    void shouldGetAccountBalanceUntilDate() {

        var user = createTestUser();
        var date = LocalDate.of(2024, 10, 15);

        when(transactionRepository.sumByUserAndTypeAndDateBefore(
                        eq(user), eq(TransactionType.INCOME), any(LocalDate.class)))
                .thenReturn(new BigDecimal("5000.00"));
        when(transactionRepository.sumByUserAndTypeAndDateBefore(
                        eq(user), eq(TransactionType.EXPENSE), any(LocalDate.class)))
                .thenReturn(new BigDecimal("2000.00"));
        when(transactionRepository.sumSavingsDepositsByUserAndDateBefore(eq(user), any(LocalDate.class)))
                .thenReturn(new BigDecimal("500.00"));
        when(transactionRepository.sumSavingsWithdrawalsByUserAndDateBefore(eq(user), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);

        var result = userBalanceService.getAccountBalanceUntilDate(user, date);

        assertThat(result).isEqualByComparingTo(new BigDecimal("2500.00"));
    }

    @Test
    @DisplayName("Should update balance after transaction successfully")
    void shouldUpdateBalanceAfterTransaction() {

        var userId = UUID.randomUUID();
        var amount = new BigDecimal("1000.00");
        var userBalance = new UserBalance();
        userBalance.setTotalIncome(new BigDecimal("5000.00"));
        userBalance.setTotalExpense(new BigDecimal("2000.00"));
        userBalance.setAccountBalance(new BigDecimal("3000.00"));
        userBalance.setSavingsBalance(BigDecimal.ZERO);

        when(userBalanceRepository.findByUserIdWithLock(userId)).thenReturn(Optional.of(userBalance));
        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(userBalance);

        userBalanceService.updateBalanceAfterTransaction(userId, TransactionType.INCOME, amount);

        verify(userBalanceRepository).save(userBalance);
        assertThat(userBalance.getTotalIncome()).isEqualByComparingTo(new BigDecimal("6000.00"));
    }

    @Test
    @DisplayName("Should revert balance after transaction delete successfully")
    void shouldRevertBalanceAfterTransactionDelete() {

        var userId = UUID.randomUUID();
        var amount = new BigDecimal("500.00");
        var userBalance = new UserBalance();
        userBalance.setTotalIncome(new BigDecimal("5000.00"));
        userBalance.setTotalExpense(new BigDecimal("2500.00"));
        userBalance.setAccountBalance(new BigDecimal("2500.00"));
        userBalance.setSavingsBalance(BigDecimal.ZERO);

        when(userBalanceRepository.findByUserIdWithLock(userId)).thenReturn(Optional.of(userBalance));
        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(userBalance);

        userBalanceService.revertBalanceAfterTransactionDelete(userId, TransactionType.EXPENSE, amount);

        verify(userBalanceRepository).save(userBalance);
        assertThat(userBalance.getTotalExpense()).isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    @Test
    @DisplayName("Should update balance after transaction update successfully")
    void shouldUpdateBalanceAfterTransactionUpdate() {

        var userId = UUID.randomUUID();
        var oldAmount = new BigDecimal("1000.00");
        var newAmount = new BigDecimal("1500.00");
        var userBalance = new UserBalance();
        userBalance.setTotalIncome(new BigDecimal("5000.00"));
        userBalance.setTotalExpense(new BigDecimal("2000.00"));
        userBalance.setAccountBalance(new BigDecimal("3000.00"));
        userBalance.setSavingsBalance(BigDecimal.ZERO);

        when(userBalanceRepository.findByUserIdWithLock(userId)).thenReturn(Optional.of(userBalance));
        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(userBalance);

        userBalanceService.updateBalanceAfterTransactionUpdate(userId, TransactionType.INCOME, oldAmount, newAmount);

        verify(userBalanceRepository).save(userBalance);
        assertThat(userBalance.getTotalIncome()).isEqualByComparingTo(new BigDecimal("5500.00"));
    }

    @Test
    @DisplayName("Should update savings balance successfully")
    void shouldUpdateSavingsBalance() {

        var userId = UUID.randomUUID();
        var oldAmount = new BigDecimal("500.00");
        var newAmount = new BigDecimal("800.00");
        var userBalance = new UserBalance();
        userBalance.setTotalIncome(new BigDecimal("5000.00"));
        userBalance.setTotalExpense(new BigDecimal("2000.00"));
        userBalance.setAccountBalance(new BigDecimal("2500.00"));
        userBalance.setSavingsBalance(new BigDecimal("500.00"));

        when(userBalanceRepository.findByUserIdWithLock(userId)).thenReturn(Optional.of(userBalance));
        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(userBalance);

        userBalanceService.updateSavingsBalance(userId, oldAmount, newAmount);

        verify(userBalanceRepository).save(userBalance);
        assertThat(userBalance.getSavingsBalance()).isEqualByComparingTo(new BigDecimal("800.00"));
        assertThat(userBalance.getAccountBalance()).isEqualByComparingTo(new BigDecimal("2200.00"));
    }

    @Test
    @DisplayName("Should handle null values in monthly balance calculation")
    void shouldHandleNullValues_InMonthlyBalanceCalculation() {

        var user = createTestUser();
        var year = 2024;
        var month = 10;
        var startDate = LocalDate.of(year, month, 1);
        var endDate = LocalDate.of(year, month, 31);

        when(jwtTokenProvider.extractEmailFromToken("token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                        user, TransactionType.INCOME, startDate, endDate))
                .thenReturn(null);
        when(transactionRepository.sumByUserAndTypeAndDateBetween(
                        user, TransactionType.EXPENSE, startDate, endDate))
                .thenReturn(null);
        when(transactionRepository.sumSavingsDepositsByUserAndDateBetween(user, startDate, endDate))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumSavingsWithdrawalsByUserAndDateBetween(user, startDate, endDate))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumByUserAndTypeAndDateBefore(
                        eq(user), eq(TransactionType.INCOME), any(LocalDate.class)))
                .thenReturn(null);
        when(transactionRepository.sumByUserAndTypeAndDateBefore(
                        eq(user), eq(TransactionType.EXPENSE), any(LocalDate.class)))
                .thenReturn(null);
        when(transactionRepository.sumSavingsDepositsByUserAndDateBefore(eq(user), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumSavingsWithdrawalsByUserAndDateBefore(eq(user), any(LocalDate.class)))
                .thenReturn(BigDecimal.ZERO);

        var result = userBalanceService.getMonthlyBalance(year, month, "Bearer token");

        assertThat(result).isNotNull();
        assertThat(result.totalIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.totalExpense()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should handle null values in account balance calculation")
    void shouldHandleNullValues_InAccountBalanceCalculation() {

        var user = createTestUser();
        var date = LocalDate.of(2024, 10, 15);

        when(transactionRepository.sumByUserAndTypeAndDateBefore(
                        eq(user), eq(TransactionType.INCOME), any(LocalDate.class)))
                .thenReturn(null);
        when(transactionRepository.sumByUserAndTypeAndDateBefore(
                        eq(user), eq(TransactionType.EXPENSE), any(LocalDate.class)))
                .thenReturn(null);
        when(transactionRepository.sumSavingsDepositsByUserAndDateBefore(eq(user), any(LocalDate.class)))
                .thenReturn(null);
        when(transactionRepository.sumSavingsWithdrawalsByUserAndDateBefore(eq(user), any(LocalDate.class)))
                .thenReturn(null);

        var result = userBalanceService.getAccountBalanceUntilDate(user, date);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should handle TRANSFER type in update balance after transaction")
    void shouldHandleTransferType_InUpdateBalanceAfterTransaction() {

        var userId = UUID.randomUUID();
        var amount = new BigDecimal("500.00");
        var userBalance = new UserBalance();
        userBalance.setTotalIncome(new BigDecimal("5000.00"));
        userBalance.setTotalExpense(new BigDecimal("2000.00"));
        userBalance.setAccountBalance(new BigDecimal("3000.00"));
        userBalance.setSavingsBalance(BigDecimal.ZERO);

        when(userBalanceRepository.findByUserIdWithLock(userId)).thenReturn(Optional.of(userBalance));
        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(userBalance);

        userBalanceService.updateBalanceAfterTransaction(userId, TransactionType.TRANSFER, amount);

        verify(userBalanceRepository).save(userBalance);
        assertThat(userBalance.getTotalIncome()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(userBalance.getTotalExpense()).isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    @Test
    @DisplayName("Should update balance after EXPENSE transaction")
    void shouldUpdateBalanceAfterExpenseTransaction() {

        var userId = UUID.randomUUID();
        var amount = new BigDecimal("300.00");
        var userBalance = new UserBalance();
        userBalance.setTotalIncome(new BigDecimal("5000.00"));
        userBalance.setTotalExpense(new BigDecimal("2000.00"));
        userBalance.setAccountBalance(new BigDecimal("3000.00"));
        userBalance.setSavingsBalance(BigDecimal.ZERO);

        when(userBalanceRepository.findByUserIdWithLock(userId)).thenReturn(Optional.of(userBalance));
        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(userBalance);

        userBalanceService.updateBalanceAfterTransaction(userId, TransactionType.EXPENSE, amount);

        verify(userBalanceRepository).save(userBalance);
        assertThat(userBalance.getTotalExpense()).isEqualByComparingTo(new BigDecimal("2300.00"));
    }

    @Test
    @DisplayName("Should revert balance after INCOME transaction delete")
    void shouldRevertBalanceAfterIncomeTransactionDelete() {

        var userId = UUID.randomUUID();
        var amount = new BigDecimal("1000.00");
        var userBalance = new UserBalance();
        userBalance.setTotalIncome(new BigDecimal("5000.00"));
        userBalance.setTotalExpense(new BigDecimal("2000.00"));
        userBalance.setAccountBalance(new BigDecimal("3000.00"));
        userBalance.setSavingsBalance(BigDecimal.ZERO);

        when(userBalanceRepository.findByUserIdWithLock(userId)).thenReturn(Optional.of(userBalance));
        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(userBalance);

        userBalanceService.revertBalanceAfterTransactionDelete(userId, TransactionType.INCOME, amount);

        verify(userBalanceRepository).save(userBalance);
        assertThat(userBalance.getTotalIncome()).isEqualByComparingTo(new BigDecimal("4000.00"));
    }

    @Test
    @DisplayName("Should update balance after EXPENSE transaction update")
    void shouldUpdateBalanceAfterExpenseTransactionUpdate() {

        var userId = UUID.randomUUID();
        var oldAmount = new BigDecimal("500.00");
        var newAmount = new BigDecimal("700.00");
        var userBalance = new UserBalance();
        userBalance.setTotalIncome(new BigDecimal("5000.00"));
        userBalance.setTotalExpense(new BigDecimal("2000.00"));
        userBalance.setAccountBalance(new BigDecimal("3000.00"));
        userBalance.setSavingsBalance(BigDecimal.ZERO);

        when(userBalanceRepository.findByUserIdWithLock(userId)).thenReturn(Optional.of(userBalance));
        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(userBalance);

        userBalanceService.updateBalanceAfterTransactionUpdate(userId, TransactionType.EXPENSE, oldAmount, newAmount);

        verify(userBalanceRepository).save(userBalance);
        assertThat(userBalance.getTotalExpense()).isEqualByComparingTo(new BigDecimal("2200.00"));
    }

    @Test
    @DisplayName("Should handle TRANSFER type in revert balance")
    void shouldHandleTransferType_InRevertBalance() {

        var userId = UUID.randomUUID();
        var amount = new BigDecimal("500.00");
        var userBalance = new UserBalance();
        userBalance.setTotalIncome(new BigDecimal("5000.00"));
        userBalance.setTotalExpense(new BigDecimal("2000.00"));
        userBalance.setAccountBalance(new BigDecimal("3000.00"));
        userBalance.setSavingsBalance(BigDecimal.ZERO);

        when(userBalanceRepository.findByUserIdWithLock(userId)).thenReturn(Optional.of(userBalance));
        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(userBalance);

        userBalanceService.revertBalanceAfterTransactionDelete(userId, TransactionType.TRANSFER, amount);

        verify(userBalanceRepository).save(userBalance);
        assertThat(userBalance.getTotalIncome()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(userBalance.getTotalExpense()).isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    @Test
    @DisplayName("Should handle TRANSFER type in update balance after transaction update")
    void shouldHandleTransferType_InUpdateBalanceAfterTransactionUpdate() {

        var userId = UUID.randomUUID();
        var oldAmount = new BigDecimal("500.00");
        var newAmount = new BigDecimal("800.00");
        var userBalance = new UserBalance();
        userBalance.setTotalIncome(new BigDecimal("5000.00"));
        userBalance.setTotalExpense(new BigDecimal("2000.00"));
        userBalance.setAccountBalance(new BigDecimal("3000.00"));
        userBalance.setSavingsBalance(BigDecimal.ZERO);

        when(userBalanceRepository.findByUserIdWithLock(userId)).thenReturn(Optional.of(userBalance));
        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(userBalance);

        userBalanceService.updateBalanceAfterTransactionUpdate(userId, TransactionType.TRANSFER, oldAmount, newAmount);

        verify(userBalanceRepository).save(userBalance);
        assertThat(userBalance.getTotalIncome()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(userBalance.getTotalExpense()).isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    @Test
    @DisplayName("Should create default balance when user balance not found with lock")
    void shouldCreateDefaultBalance_WhenUserBalanceNotFoundWithLock() {

        var userId = UUID.randomUUID();
        var user = createTestUser();
        user.setId(userId);
        var newBalance = createDefaultBalance(user);

        when(userBalanceRepository.findByUserIdWithLock(userId)).thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userBalanceRepository.save(any(UserBalance.class))).thenReturn(newBalance);

        var result = userBalanceService.getUserBalanceWithLockOrCreateDefault(userId);

        assertThat(result).isNotNull();
        assertThat(result.getTotalIncome()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getTotalExpense()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getAccountBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.getSavingsBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(userRepository).findById(userId);
        verify(userBalanceRepository).save(any(UserBalance.class));
    }

    @Test
    @DisplayName("Should return existing balance when found with lock")
    void shouldReturnExistingBalance_WhenFoundWithLock() {

        var userId = UUID.randomUUID();
        var existingBalance = new UserBalance();
        existingBalance.setId(UUID.randomUUID());
        existingBalance.setTotalIncome(new BigDecimal("1000.00"));
        existingBalance.setTotalExpense(new BigDecimal("500.00"));
        existingBalance.setAccountBalance(new BigDecimal("500.00"));
        existingBalance.setSavingsBalance(BigDecimal.ZERO);

        when(userBalanceRepository.findByUserIdWithLock(userId)).thenReturn(Optional.of(existingBalance));

        var result = userBalanceService.getUserBalanceWithLockOrCreateDefault(userId);

        assertThat(result).isEqualTo(existingBalance);
        assertThat(result.getTotalIncome()).isEqualByComparingTo(new BigDecimal("1000.00"));
        verify(userBalanceRepository).findByUserIdWithLock(userId);
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

    private UserBalance createDefaultBalance(User user) {
        UserBalance userBalance = new UserBalance();
        userBalance.setId(UUID.randomUUID());
        userBalance.setUser(user);
        userBalance.setTotalIncome(BigDecimal.ZERO);
        userBalance.setTotalExpense(BigDecimal.ZERO);
        userBalance.setAccountBalance(BigDecimal.ZERO);
        userBalance.setSavingsBalance(BigDecimal.ZERO);
        userBalance.setTotalBalance(BigDecimal.ZERO);
        userBalance.setCreatedAt(LocalDateTime.now());
        userBalance.setUpdatedAt(LocalDateTime.now());
        return userBalance;
    }
}

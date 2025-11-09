package dias.heimy.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dias.heimy.domain.entity.User;
import dias.heimy.domain.entity.UserBalance;
import dias.heimy.domain.enums.UserRole;
import dias.heimy.repository.UserBalanceRepository;
import dias.heimy.repository.UserRepository;
import java.math.BigDecimal;
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
@DisplayName("Tests for UserBalanceLockServiceImpl")
class UserBalanceLockServiceImplTest {

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserBalanceLockServiceImpl userBalanceLockService;

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

        var result = userBalanceLockService.getUserBalanceWithLockOrCreateDefault(userId);

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

        var result = userBalanceLockService.getUserBalanceWithLockOrCreateDefault(userId);

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

package dias.heimy.service;

import dias.heimy.domain.entity.User;
import dias.heimy.domain.entity.UserBalance;
import dias.heimy.domain.enums.TransactionType;
import dias.heimy.dto.response.MonthlyBalanceResponse;
import dias.heimy.dto.response.UserBalanceResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface UserBalanceService {
    UserBalanceResponse getCurrentBalance(String authorizationHeader);

    MonthlyBalanceResponse getMonthlyBalance(int year, int month, String authorizationHeader);

    BigDecimal getAccountBalanceUntilDate(User user, LocalDate date);

    UserBalance getUserBalanceOrCreateDefault(User user);

    UserBalance getUserBalanceWithLockOrCreateDefault(UUID userId);

    void updateBalanceAfterTransaction(UUID userId, TransactionType type, BigDecimal amount);

    void revertBalanceAfterTransactionDelete(UUID userId, TransactionType type, BigDecimal amount);

    void updateBalanceAfterTransactionUpdate(
            UUID userId, TransactionType type, BigDecimal oldAmount, BigDecimal newAmount);

    void moveToSavings(UUID userId, BigDecimal amount);

    void updateSavingsBalance(UUID userId, BigDecimal oldAmount, BigDecimal newAmount);

    void moveFromSavings(UUID userId, BigDecimal amount);
}

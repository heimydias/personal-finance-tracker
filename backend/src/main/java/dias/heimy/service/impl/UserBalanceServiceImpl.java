package dias.heimy.service.impl;

import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.domain.entity.User;
import dias.heimy.domain.entity.UserBalance;
import dias.heimy.domain.enums.TransactionType;
import dias.heimy.domain.exception.UserNotFoundException;
import dias.heimy.dto.response.MonthlyBalanceResponse;
import dias.heimy.dto.response.UserBalanceResponse;
import dias.heimy.repository.TransactionRepository;
import dias.heimy.repository.UserBalanceRepository;
import dias.heimy.repository.UserRepository;
import dias.heimy.service.UserBalanceService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBalanceServiceImpl implements UserBalanceService {

    private final UserBalanceRepository userBalanceRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional(readOnly = true)
    public UserBalanceResponse getCurrentBalance(String authorizationHeader) {
        String email = extractEmailFromToken(authorizationHeader);
        User user = findUserByEmail(email);

        UserBalance userBalance = getUserBalanceOrCreateDefault(user);

        log.info(
                "Saldo consultado para usuário {}: Conta=R$ {}, Poupança=R$ {}, Total=R$ {}",
                email,
                userBalance.getAccountBalance(),
                userBalance.getSavingsBalance(),
                userBalance.getTotalBalance());

        return new UserBalanceResponse(
                userBalance.getId(),
                user.getId(),
                userBalance.getTotalIncome(),
                userBalance.getTotalExpense(),
                userBalance.getAccountBalance(),
                userBalance.getSavingsBalance(),
                userBalance.getTotalBalance(),
                userBalance.getCreatedAt(),
                userBalance.getUpdatedAt());
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

        BigDecimal savingsDepositsMonth =
                transactionRepository.sumSavingsDepositsByUserAndDateBetween(user, startDate, endDate);
        BigDecimal savingsWithdrawalsMonth =
                transactionRepository.sumSavingsWithdrawalsByUserAndDateBetween(user, startDate, endDate);

        if (totalIncome == null) {
            totalIncome = BigDecimal.ZERO;
        }
        if (totalExpense == null) {
            totalExpense = BigDecimal.ZERO;
        }

        BigDecimal netSavingsDepositsMonth = savingsDepositsMonth.subtract(savingsWithdrawalsMonth);

        BigDecimal totalIncomeUntilMonth =
                transactionRepository.sumByUserAndTypeAndDateBefore(user, TransactionType.INCOME, endDate.plusDays(1));
        BigDecimal totalExpenseUntilMonth =
                transactionRepository.sumByUserAndTypeAndDateBefore(user, TransactionType.EXPENSE, endDate.plusDays(1));

        BigDecimal savingsDepositsUntilMonth =
                transactionRepository.sumSavingsDepositsByUserAndDateBefore(user, endDate.plusDays(1));
        BigDecimal savingsWithdrawalsUntilMonth =
                transactionRepository.sumSavingsWithdrawalsByUserAndDateBefore(user, endDate.plusDays(1));

        if (totalIncomeUntilMonth == null) {
            totalIncomeUntilMonth = BigDecimal.ZERO;
        }
        if (totalExpenseUntilMonth == null) {
            totalExpenseUntilMonth = BigDecimal.ZERO;
        }

        BigDecimal savingsBalanceUntilMonth = savingsDepositsUntilMonth.subtract(savingsWithdrawalsUntilMonth);

        BigDecimal totalBalanceUntilMonth = totalIncomeUntilMonth.subtract(totalExpenseUntilMonth);

        BigDecimal accountBalanceUntilMonth = totalBalanceUntilMonth.subtract(savingsBalanceUntilMonth);

        log.info(
                "Saldo mensal calculado para {}/{} - Receitas do Mês: R$ {}, Despesas do Mês: R$ {}, "
                        + "Depósitos em Poupança: R$ {}, Resgates de Poupança: R$ {}, Depósito Líquido: R$ {}, "
                        + "Saldo do Período: R$ {}, Saldo em Conta até {}: R$ {}, Poupança até {}: R$ {}, Saldo Total até {}: R$ {}",
                year,
                month,
                totalIncome,
                totalExpense,
                savingsDepositsMonth,
                savingsWithdrawalsMonth,
                netSavingsDepositsMonth,
                totalIncome.subtract(totalExpense),
                endDate,
                accountBalanceUntilMonth,
                endDate,
                savingsBalanceUntilMonth,
                endDate,
                totalBalanceUntilMonth);

        BigDecimal monthlySavingsBalance = savingsDepositsMonth.subtract(savingsWithdrawalsMonth);

        return MonthlyBalanceResponse.of(
                year,
                month,
                totalIncome,
                totalExpense,
                netSavingsDepositsMonth,
                savingsDepositsMonth,
                savingsWithdrawalsMonth,
                monthlySavingsBalance,
                accountBalanceUntilMonth,
                savingsBalanceUntilMonth);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAccountBalanceUntilDate(User user, LocalDate date) {
        BigDecimal totalIncomeUntilDate =
                transactionRepository.sumByUserAndTypeAndDateBefore(user, TransactionType.INCOME, date.plusDays(1));
        BigDecimal totalExpenseUntilDate =
                transactionRepository.sumByUserAndTypeAndDateBefore(user, TransactionType.EXPENSE, date.plusDays(1));

        if (totalIncomeUntilDate == null) {
            totalIncomeUntilDate = BigDecimal.ZERO;
        }
        if (totalExpenseUntilDate == null) {
            totalExpenseUntilDate = BigDecimal.ZERO;
        }

        BigDecimal savingsDepositsUntilDate =
                transactionRepository.sumSavingsDepositsByUserAndDateBefore(user, date.plusDays(1));
        BigDecimal savingsWithdrawalsUntilDate =
                transactionRepository.sumSavingsWithdrawalsByUserAndDateBefore(user, date.plusDays(1));

        if (savingsDepositsUntilDate == null) {
            savingsDepositsUntilDate = BigDecimal.ZERO;
        }
        if (savingsWithdrawalsUntilDate == null) {
            savingsWithdrawalsUntilDate = BigDecimal.ZERO;
        }

        BigDecimal savingsBalanceUntilDate = savingsDepositsUntilDate.subtract(savingsWithdrawalsUntilDate);

        BigDecimal totalBalanceUntilDate = totalIncomeUntilDate.subtract(totalExpenseUntilDate);

        BigDecimal accountBalanceUntilDate = totalBalanceUntilDate.subtract(savingsBalanceUntilDate);

        log.info(
                "Saldo em conta calculado até {}: R$ {} (Total: R$ {}, Poupança: R$ {})",
                date,
                accountBalanceUntilDate,
                totalBalanceUntilDate,
                savingsBalanceUntilDate);

        return accountBalanceUntilDate;
    }

    private String extractEmailFromToken(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return jwtTokenProvider.extractEmailFromToken(token);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    public UserBalance getUserBalanceOrCreateDefault(User user) {
        return userBalanceRepository.findByUserId(user.getId()).orElseGet(() -> createDefaultBalance(user));
    }

    @Override
    @Transactional
    public UserBalance getUserBalanceWithLockOrCreateDefault(UUID userId) {
        return userBalanceRepository.findByUserIdWithLock(userId).orElseGet(() -> {
            User user = userRepository.findById(userId).orElseThrow();
            UserBalance newBalance = createDefaultBalance(user);
            return userBalanceRepository.save(newBalance);
        });
    }

    @Override
    @Transactional
    public void updateBalanceAfterTransaction(UUID userId, TransactionType type, BigDecimal amount) {
        UserBalance userBalance = getUserBalanceWithLockOrCreateDefault(userId);

        BigDecimal totalIncome = userBalance.getTotalIncome();
        BigDecimal totalExpense = userBalance.getTotalExpense();

        if (type == TransactionType.INCOME) {
            totalIncome = totalIncome.add(amount);
        } else if (type == TransactionType.EXPENSE) {
            totalExpense = totalExpense.add(amount);
        }

        BigDecimal newAccountBalance = totalIncome.subtract(totalExpense).subtract(userBalance.getSavingsBalance());
        userBalance.updateBalance(totalIncome, totalExpense, newAccountBalance, userBalance.getSavingsBalance());

        userBalanceRepository.save(userBalance);

        log.info(
                "Saldo atualizado após transação {} - Conta: R$ {}, Total: R$ {}",
                type,
                userBalance.getAccountBalance(),
                userBalance.getTotalBalance());
    }

    @Override
    @Transactional
    public void revertBalanceAfterTransactionDelete(UUID userId, TransactionType type, BigDecimal amount) {
        UserBalance userBalance = getUserBalanceWithLockOrCreateDefault(userId);

        BigDecimal totalIncome = userBalance.getTotalIncome();
        BigDecimal totalExpense = userBalance.getTotalExpense();

        if (type == TransactionType.INCOME) {
            totalIncome = totalIncome.subtract(amount);
        } else if (type == TransactionType.EXPENSE) {
            totalExpense = totalExpense.subtract(amount);
        }

        BigDecimal newAccountBalance = totalIncome.subtract(totalExpense).subtract(userBalance.getSavingsBalance());
        userBalance.updateBalance(totalIncome, totalExpense, newAccountBalance, userBalance.getSavingsBalance());

        userBalanceRepository.save(userBalance);

        log.info(
                "Saldo revertido após deletar transação {} - Conta: R$ {}, Total: R$ {}",
                type,
                userBalance.getAccountBalance(),
                userBalance.getTotalBalance());
    }

    @Override
    @Transactional
    public void updateBalanceAfterTransactionUpdate(
            UUID userId, TransactionType type, BigDecimal oldAmount, BigDecimal newAmount) {
        UserBalance userBalance = getUserBalanceWithLockOrCreateDefault(userId);

        BigDecimal totalIncome = userBalance.getTotalIncome();
        BigDecimal totalExpense = userBalance.getTotalExpense();

        if (type == TransactionType.INCOME) {
            totalIncome = totalIncome.subtract(oldAmount);
        } else if (type == TransactionType.EXPENSE) {
            totalExpense = totalExpense.subtract(oldAmount);
        }

        if (type == TransactionType.INCOME) {
            totalIncome = totalIncome.add(newAmount);
        } else if (type == TransactionType.EXPENSE) {
            totalExpense = totalExpense.add(newAmount);
        }

        BigDecimal newAccountBalance = totalIncome.subtract(totalExpense).subtract(userBalance.getSavingsBalance());
        userBalance.updateBalance(totalIncome, totalExpense, newAccountBalance, userBalance.getSavingsBalance());

        userBalanceRepository.save(userBalance);

        log.info(
                "Saldo atualizado após modificar transação {} - Conta: R$ {}, Total: R$ {}",
                type,
                userBalance.getAccountBalance(),
                userBalance.getTotalBalance());
    }

    @Override
    @Transactional
    public void moveToSavings(UUID userId, BigDecimal amount) {
        UserBalance userBalance = getUserBalanceWithLockOrCreateDefault(userId);

        BigDecimal newAccountBalance = userBalance.getAccountBalance().subtract(amount);
        BigDecimal newSavingsBalance = userBalance.getSavingsBalance().add(amount);

        userBalance.updateBalance(
                userBalance.getTotalIncome(), userBalance.getTotalExpense(), newAccountBalance, newSavingsBalance);

        userBalanceRepository.save(userBalance);

        log.info(
                "Valor movido para poupança - Conta: R$ {}, Poupança: R$ {}, Total: R$ {}",
                userBalance.getAccountBalance(),
                userBalance.getSavingsBalance(),
                userBalance.getTotalBalance());
    }

    @Override
    @Transactional
    public void updateSavingsBalance(UUID userId, BigDecimal oldAmount, BigDecimal newAmount) {
        UserBalance userBalance = getUserBalanceWithLockOrCreateDefault(userId);

        BigDecimal difference = newAmount.subtract(oldAmount);
        BigDecimal newAccountBalance = userBalance.getAccountBalance().subtract(difference);
        BigDecimal newSavingsBalance = userBalance.getSavingsBalance().add(difference);

        userBalance.updateBalance(
                userBalance.getTotalIncome(), userBalance.getTotalExpense(), newAccountBalance, newSavingsBalance);

        userBalanceRepository.save(userBalance);

        log.info(
                "Saldo de poupança atualizado - Conta: R$ {}, Poupança: R$ {}, Total: R$ {}",
                userBalance.getAccountBalance(),
                userBalance.getSavingsBalance(),
                userBalance.getTotalBalance());
    }

    @Override
    @Transactional
    public void moveFromSavings(UUID userId, BigDecimal amount) {
        UserBalance userBalance = getUserBalanceWithLockOrCreateDefault(userId);

        BigDecimal newAccountBalance = userBalance.getAccountBalance().add(amount);
        BigDecimal newSavingsBalance = userBalance.getSavingsBalance().subtract(amount);

        userBalance.updateBalance(
                userBalance.getTotalIncome(), userBalance.getTotalExpense(), newAccountBalance, newSavingsBalance);

        userBalanceRepository.save(userBalance);

        log.info(
                "Valor retornado da poupança - Conta: R$ {}, Poupança: R$ {}, Total: R$ {}",
                userBalance.getAccountBalance(),
                userBalance.getSavingsBalance(),
                userBalance.getTotalBalance());
    }

    private UserBalance createDefaultBalance(User user) {
        UserBalance defaultBalance = new UserBalance();
        defaultBalance.setUser(user);
        defaultBalance.setTotalIncome(BigDecimal.ZERO);
        defaultBalance.setTotalExpense(BigDecimal.ZERO);
        defaultBalance.setAccountBalance(BigDecimal.ZERO);
        defaultBalance.setSavingsBalance(BigDecimal.ZERO);
        defaultBalance.setTotalBalance(BigDecimal.ZERO);
        return defaultBalance;
    }
}

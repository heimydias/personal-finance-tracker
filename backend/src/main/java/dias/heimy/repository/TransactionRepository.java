package dias.heimy.repository;

import dias.heimy.domain.entity.Transaction;
import dias.heimy.domain.entity.User;
import dias.heimy.domain.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findByUser(User user, Pageable pageable);

    Page<Transaction> findByUserAndType(User user, TransactionType type, Pageable pageable);

    List<Transaction> findByUserAndTransactionDateBetween(User user, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t "
            + "WHERE t.user = :user AND t.type = :type "
            + "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumByUserAndTypeAndDateBetween(
            @Param("user") User user,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t "
            + "WHERE t.user = :user AND t.type = :type "
            + "AND t.transactionDate < :endDate")
    BigDecimal sumByUserAndTypeAndDateBefore(
            @Param("user") User user, @Param("type") TransactionType type, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type")
    BigDecimal sumAmountByUserAndType(@Param("userId") UUID userId, @Param("type") TransactionType type);

    /**
     * Soma transações TRANSFER que são depósitos em poupança em um período
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t "
            + "WHERE t.user = :user AND t.type = 'TRANSFER' "
            + "AND t.transferType = 'DEPOSIT' "
            + "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumSavingsDepositsByUserAndDateBetween(
            @Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Soma transações TRANSFER que são resgates de poupança em um período
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t "
            + "WHERE t.user = :user AND t.type = 'TRANSFER' "
            + "AND t.transferType = 'WITHDRAWAL' "
            + "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumSavingsWithdrawalsByUserAndDateBetween(
            @Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Soma transações TRANSFER que são depósitos em poupança até uma data
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t "
            + "WHERE t.user = :user AND t.type = 'TRANSFER' "
            + "AND t.transferType = 'DEPOSIT' "
            + "AND t.transactionDate < :endDate")
    BigDecimal sumSavingsDepositsByUserAndDateBefore(@Param("user") User user, @Param("endDate") LocalDate endDate);

    /**
     * Soma transações TRANSFER que são resgates de poupança até uma data
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t "
            + "WHERE t.user = :user AND t.type = 'TRANSFER' "
            + "AND t.transferType = 'WITHDRAWAL' "
            + "AND t.transactionDate < :endDate")
    BigDecimal sumSavingsWithdrawalsByUserAndDateBefore(@Param("user") User user, @Param("endDate") LocalDate endDate);
}

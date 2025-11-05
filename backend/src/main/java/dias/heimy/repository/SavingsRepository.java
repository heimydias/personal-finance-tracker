package dias.heimy.repository;

import dias.heimy.domain.entity.Savings;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingsRepository extends JpaRepository<Savings, UUID> {

    @Query("SELECT s FROM Savings s WHERE s.user.id = :userId")
    Page<Savings> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT s FROM Savings s WHERE s.id = :id AND s.user.id = :userId")
    Optional<Savings> findByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    @Query("SELECT COUNT(s) > 0 FROM Savings s WHERE s.id = :id AND s.user.id = :userId")
    boolean existsByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);
}

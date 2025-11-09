package dias.heimy.service.impl;

import dias.heimy.domain.entity.User;
import dias.heimy.domain.entity.UserBalance;
import dias.heimy.repository.UserBalanceRepository;
import dias.heimy.repository.UserRepository;
import dias.heimy.service.UserBalanceLockService;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBalanceLockServiceImpl implements UserBalanceLockService {

    private final UserBalanceRepository userBalanceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserBalance getUserBalanceWithLockOrCreateDefault(UUID userId) {
        return userBalanceRepository.findByUserIdWithLock(userId).orElseGet(() -> {
            log.info("Saldo não encontrado para usuário {}. Criando saldo padrão.", userId);
            User user = userRepository.findById(userId).orElseThrow();
            UserBalance newBalance = createDefaultBalance(user);
            return userBalanceRepository.save(newBalance);
        });
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

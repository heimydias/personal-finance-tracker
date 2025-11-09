package dias.heimy.service;

import dias.heimy.domain.entity.UserBalance;
import java.util.UUID;

public interface UserBalanceLockService {

    UserBalance getUserBalanceWithLockOrCreateDefault(UUID userId);
}

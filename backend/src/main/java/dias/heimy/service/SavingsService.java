package dias.heimy.service;

import dias.heimy.dto.request.SavingsRequest;
import dias.heimy.dto.response.ConsolidatedYieldResponse;
import dias.heimy.dto.response.MonthlyYieldResponse;
import dias.heimy.dto.response.SavingsResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SavingsService {

    SavingsResponse createSavings(SavingsRequest request, String authorizationHeader);

    SavingsResponse getSavingsById(UUID id, String authorizationHeader);

    Page<SavingsResponse> listSavings(Pageable pageable, String authorizationHeader);

    SavingsResponse updateSavings(UUID id, SavingsRequest request, String authorizationHeader);

    void deleteSavings(UUID id, String authorizationHeader);

    MonthlyYieldResponse calculateMonthlyYield(UUID id, String authorizationHeader);

    ConsolidatedYieldResponse calculateConsolidatedYield(String authorizationHeader);
}

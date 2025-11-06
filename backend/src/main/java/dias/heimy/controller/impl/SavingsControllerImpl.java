package dias.heimy.controller.impl;

import dias.heimy.controller.SavingsController;
import dias.heimy.dto.request.SavingsRequest;
import dias.heimy.dto.response.ConsolidatedYieldResponse;
import dias.heimy.dto.response.MonthlyYieldResponse;
import dias.heimy.dto.response.PageResponse;
import dias.heimy.dto.response.SavingsResponse;
import dias.heimy.service.SavingsService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SavingsControllerImpl implements SavingsController {

    private final SavingsService savingsService;

    @Override
    public ResponseEntity<SavingsResponse> createSavings(SavingsRequest request, String authorizationHeader) {
        log.info("Requisição para criar poupança: {} - R$ {}", request.name(), request.amount());
        SavingsResponse response = savingsService.createSavings(request, authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<SavingsResponse> getSavingsById(UUID id, String authorizationHeader) {
        log.info("Requisição para buscar poupança: {}", id);
        SavingsResponse response = savingsService.getSavingsById(id, authorizationHeader);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PageResponse<SavingsResponse>> listSavings(
            int page, int size, String sort, String authorizationHeader) {
        log.info("Requisição para listar poupanças - page: {}, size: {}, sort: {}", page, size, sort);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<SavingsResponse> pageResult = savingsService.listSavings(pageable, authorizationHeader);
        PageResponse<SavingsResponse> response = PageResponse.of(
                pageResult.getNumber(), pageResult.getSize(), pageResult.getTotalElements(), pageResult.getContent());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SavingsResponse> updateSavings(UUID id, SavingsRequest request, String authorizationHeader) {
        log.info("Requisição para atualizar poupança: {}", id);
        SavingsResponse response = savingsService.updateSavings(id, request, authorizationHeader);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteSavings(UUID id, String authorizationHeader) {
        log.info("Requisição para deletar poupança: {}", id);
        savingsService.deleteSavings(id, authorizationHeader);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<MonthlyYieldResponse> calculateMonthlyYield(UUID id, String authorizationHeader) {
        log.info("Requisição para calcular rendimento mensal da poupança: {}", id);
        MonthlyYieldResponse response = savingsService.calculateMonthlyYield(id, authorizationHeader);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ConsolidatedYieldResponse> calculateConsolidatedYield(String authorizationHeader) {
        log.info("Requisição para calcular rendimento consolidado de todas as poupanças");
        ConsolidatedYieldResponse response = savingsService.calculateConsolidatedYield(authorizationHeader);
        return ResponseEntity.ok(response);
    }
}

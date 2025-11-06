package dias.heimy.controller;

import static dias.heimy.constants.PathConstants.SAVINGS;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dias.heimy.dto.request.SavingsRequest;
import dias.heimy.dto.response.ConsolidatedYieldResponse;
import dias.heimy.dto.response.MonthlyYieldResponse;
import dias.heimy.dto.response.PageResponse;
import dias.heimy.dto.response.SavingsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Savings", description = "Endpoints para gerenciamento de poupanças")
@RequestMapping(value = SAVINGS, produces = APPLICATION_JSON_VALUE)
public interface SavingsController {

    @Operation(
            summary = "Criar nova poupança",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @PostMapping
    ResponseEntity<SavingsResponse> createSavings(
            @Valid @RequestBody SavingsRequest request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader);

    @Operation(
            summary = "Buscar poupança por ID",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping("/{id}")
    ResponseEntity<SavingsResponse> getSavingsById(
            @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader);

    @Operation(
            summary = "Listar poupanças do usuário",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping
    ResponseEntity<PageResponse<SavingsResponse>> listSavings(
            @Parameter(description = "Número da página (0-indexed)", example = "0") @RequestParam(defaultValue = "0")
                    int page,
            @Parameter(description = "Tamanho da página", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Ordenação (ex: id, name, amount)", example = "id")
                    @RequestParam(defaultValue = "id")
                    String sort,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader);

    @Operation(
            summary = "Atualizar poupança",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @PutMapping("/{id}")
    ResponseEntity<SavingsResponse> updateSavings(
            @PathVariable UUID id,
            @Valid @RequestBody SavingsRequest request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader);

    @Operation(
            summary = "Deletar poupança",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteSavings(
            @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader);

    @Operation(
            summary = "Calcular rendimento mensal da poupança",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping("/{id}/yield")
    ResponseEntity<MonthlyYieldResponse> calculateMonthlyYield(
            @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader);

    @Operation(
            summary = "Calcular rendimento consolidado de todas as poupanças",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping("/yield")
    ResponseEntity<ConsolidatedYieldResponse> calculateConsolidatedYield(
            @Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader);
}

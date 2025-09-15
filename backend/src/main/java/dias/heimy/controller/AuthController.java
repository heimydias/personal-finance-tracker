package dias.heimy.controller;

import static dias.heimy.constants.PathConstants.AUTH_REFRESH_TOKEN;
import static dias.heimy.constants.PathConstants.AUTH_TOKEN;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dias.heimy.dto.request.LoginRequest;
import dias.heimy.dto.request.RefreshTokenRequest;
import dias.heimy.dto.response.AuthenticationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(produces = APPLICATION_JSON_VALUE)
public interface AuthController {

    @Operation(
            tags = "Auth",
            summary = "Obter token de acesso",
            description = "Autentica usuário com email e senha, retornando tokens JWT de acesso e refresh.")
    @ApiResponse(
            responseCode = "200",
            description = "Token gerado com sucesso",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @PostMapping(AUTH_TOKEN)
    ResponseEntity<AuthenticationResponse> token(@Valid @RequestBody LoginRequest request);

    @Operation(
            tags = "Auth",
            summary = "Renovar token de acesso",
            description = "Gera novo token de acesso usando o refresh token.")
    @ApiResponse(
            responseCode = "200",
            description = "Token renovado com sucesso",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class)))
    @ApiResponse(responseCode = "400", description = "Refresh token inválido")
    @ApiResponse(responseCode = "401", description = "Refresh token expirado")
    @PostMapping(AUTH_REFRESH_TOKEN)
    ResponseEntity<AuthenticationResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request);
}

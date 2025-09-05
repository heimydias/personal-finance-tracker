package dias.heimy.controller;

import static dias.heimy.constants.PathConstants.AUTH_LOGIN;
import static dias.heimy.constants.PathConstants.AUTH_REFRESH;
import static dias.heimy.constants.PathConstants.AUTH_REGISTER;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dias.heimy.dto.request.LoginRequest;
import dias.heimy.dto.request.RefreshTokenRequest;
import dias.heimy.dto.request.UserRegisterRequest;
import dias.heimy.dto.response.AuthenticationResponse;
import dias.heimy.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
            summary = "Registro de usuário",
            description = "Registra novo usuário no sistema com email, senha e role.")
    @ApiResponse(
            responseCode = "201",
            description = "Usuário registrado com sucesso",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class),
                            examples =
                                    @ExampleObject(
                                            value =
                                                    """
                {
                  "id": "550e8400-e29b-41d4-a716-446655440000",
                  "email": "newuser@example.com",
                  "role": USER.name(),
                  "createdAt": "2024-01-15T10:30:00",
                  "updatedAt": "2024-01-15T10:30:00"
                }
                """)))
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou usuário já existe")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PostMapping(AUTH_REGISTER)
    ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest request);

    @Operation(
            tags = "Auth",
            summary = "Login de usuário",
            description = "Autentica usuário com email e senha, retornando tokens JWT de acesso e refresh.")
    @ApiResponse(
            responseCode = "200",
            description = "Login realizado com sucesso",
            content =
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class),
                            examples =
                                    @ExampleObject(
                                            value =
                                                    """
                        {
                          "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                          "tokenType": "Bearer",
                          "expiresIn": 3600,
                          "userEmail": "admin@example.com",
                          "userRole": "ADMIN"
                        }
                        """)))
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PostMapping(AUTH_LOGIN)
    ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request);

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
                            schema = @Schema(implementation = AuthenticationResponse.class),
                            examples =
                                    @ExampleObject(
                                            value =
                                                    """
            {
              "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
              "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
              "tokenType": "Bearer",
              "expiresIn": 3600,
              "userEmail": "user@example.com",
              "userRole": USER.name()
            }
            """)))
    @ApiResponse(responseCode = "400", description = "Refresh token inválido")
    @ApiResponse(responseCode = "401", description = "Refresh token expirado")
    @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PostMapping(AUTH_REFRESH)
    ResponseEntity<AuthenticationResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request);
}

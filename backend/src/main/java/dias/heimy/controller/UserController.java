package dias.heimy.controller;

import static dias.heimy.constants.PathConstants.USERS;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import dias.heimy.dto.request.UserRegisterRequest;
import dias.heimy.dto.request.UserUpdateRequest;
import dias.heimy.dto.response.PageResponse;
import dias.heimy.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = USERS, produces = APPLICATION_JSON_VALUE)
public interface UserController {

    @Operation(
            tags = "Users",
            summary = "Criar usuário",
            description =
                    "Cria novo usuário no sistema com email, senha e role. Para criar usuários ADMIN é necessário estar autenticado como administrador.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponse(
            responseCode = "201",
            description = "Usuário criado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou usuário já existe")
    @ApiResponse(responseCode = "401", description = "Token de acesso inválido para criar ADMIN")
    @ApiResponse(responseCode = "403", description = "Permissão insuficiente para criar ADMIN")
    @PostMapping
    ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRegisterRequest request,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
                    String authorizationHeader);

    @Operation(
            tags = "Users",
            summary = "Listar usuários",
            description = "Lista todos os usuários do sistema. Apenas administradores podem acessar.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponse(
            responseCode = "200",
            description = "Lista de usuários retornada com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token de acesso inválido")
    @ApiResponse(responseCode = "403", description = "Usuário não possui permissão de administrador")
    @GetMapping
    ResponseEntity<PageResponse<UserResponse>> listUsers(
            @Parameter(description = "Número da página (começa em 0)", example = "0")
                    @RequestHeader(value = "page", defaultValue = "0")
                    int page,
            @Parameter(description = "Tamanho da página", example = "10")
                    @RequestHeader(value = "size", defaultValue = "10")
                    int size,
            @Parameter(description = "Campo para ordenação", example = "email")
                    @RequestHeader(value = "sort", required = false)
                    String sort,
            @Parameter(description = "Direção da ordenação (asc ou desc)", example = "asc")
                    @RequestHeader(value = "order", defaultValue = "asc")
                    String order,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization") String authorizationHeader);

    @Operation(
            tags = "Users",
            summary = "Buscar usuário por ID",
            description =
                    "Busca usuário específico por ID. Usuários podem acessar apenas seus próprios dados, administradores podem acessar qualquer usuário.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponse(
            responseCode = "200",
            description = "Usuário encontrado",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token de acesso inválido")
    @ApiResponse(responseCode = "403", description = "Usuário não possui permissão para acessar este recurso")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @GetMapping("/{id}")
    ResponseEntity<UserResponse> getUserById(
            @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization") String authorizationHeader);

    @Operation(
            tags = "Users",
            summary = "Atualizar usuário",
            description =
                    "Atualiza dados do usuário. Usuários podem atualizar apenas seus próprios dados (exceto role), administradores podem atualizar qualquer usuário.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponse(responseCode = "204", description = "Usuário atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    @ApiResponse(responseCode = "401", description = "Token de acesso inválido")
    @ApiResponse(responseCode = "403", description = "Usuário não possui permissão para atualizar este recurso")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @PutMapping("/{id}")
    ResponseEntity<Void> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization") String authorizationHeader);

    @Operation(
            tags = "Users",
            summary = "Deletar usuário",
            description =
                    "Deleta usuário. Usuários podem deletar apenas seus próprios dados, administradores podem deletar qualquer usuário.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso")
    @ApiResponse(responseCode = "401", description = "Token de acesso inválido")
    @ApiResponse(responseCode = "403", description = "Usuário não possui permissão para deletar este recurso")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteUser(
            @PathVariable UUID id,
            @Parameter(hidden = true) @RequestHeader(value = "Authorization") String authorizationHeader);
}

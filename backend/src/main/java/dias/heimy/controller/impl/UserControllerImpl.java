package dias.heimy.controller.impl;

import static org.springframework.http.HttpStatus.CREATED;

import dias.heimy.controller.UserController;
import dias.heimy.dto.request.UserRegisterRequest;
import dias.heimy.dto.request.UserUpdateRequest;
import dias.heimy.dto.response.PageResponse;
import dias.heimy.dto.response.UserResponse;
import dias.heimy.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public ResponseEntity<UserResponse> createUser(UserRegisterRequest request, String authorizationHeader) {
        log.info("Requisição para criar usuário recebida: {}", request.email());
        UserResponse response = userService.createUser(request, authorizationHeader);
        log.info("Usuário criado com sucesso: {}", request.email());
        return ResponseEntity.status(CREATED).body(response);
    }

    @Override
    public ResponseEntity<PageResponse<UserResponse>> listUsers(
            int page, int size, String sort, String order, String authorizationHeader) {
        log.info(
                "Requisição para listar usuários recebida - page: {}, size: {}, sort: {}, order: {}",
                page,
                size,
                sort,
                order);

        validatePaginationParameters(page, size, order);

        PageRequest pageable;
        if (sort != null && !sort.trim().isEmpty()) {
            Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
            String validatedSort = validateSortField(sort.trim());
            pageable = PageRequest.of(page, size, Sort.by(direction, validatedSort));
        } else {
            pageable = PageRequest.of(page, size);
        }

        PageResponse<UserResponse> users = userService.listUsers(pageable, authorizationHeader);
        log.info("Lista de usuários retornada com {} elementos", users.elements());
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<UserResponse> getUserById(UUID id, String authorizationHeader) {
        log.info("Requisição para buscar usuário por ID: {}", id);
        UserResponse user = userService.getUserById(id, authorizationHeader);
        log.info("Usuário encontrado: {}", user.email());
        return ResponseEntity.ok(user);
    }

    @Override
    public ResponseEntity<Void> updateUser(UUID id, UserUpdateRequest request, String authorizationHeader) {
        log.info("Requisição para atualizar usuário: {}", id);
        userService.updateUser(id, request, authorizationHeader);
        log.info("Usuário atualizado com sucesso: {}", id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteUser(UUID id, String authorizationHeader) {
        log.info("Requisição para deletar usuário: {}", id);
        userService.deleteUser(id, authorizationHeader);
        log.info("Usuário deletado com sucesso: {}", id);
        return ResponseEntity.noContent().build();
    }

    private void validatePaginationParameters(int page, int size, String order) {
        if (page < 0) {
            throw new IllegalArgumentException("Número da página não pode ser negativo");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Tamanho da página deve estar entre 1 e 100");
        }
        if (order != null
                && !order.trim().isEmpty()
                && !"asc".equalsIgnoreCase(order.trim())
                && !"desc".equalsIgnoreCase(order.trim())) {
            throw new IllegalArgumentException("Ordem deve ser 'asc' ou 'desc'");
        }
    }

    private String validateSortField(String sort) {
        java.util.Set<String> validFields = java.util.Set.of("email", "role", "createdAt", "updatedAt");
        if (!validFields.contains(sort)) {
            throw new IllegalArgumentException(
                    "Campo de ordenação inválido: " + sort + ". Campos válidos: " + String.join(", ", validFields));
        }
        return sort;
    }
}

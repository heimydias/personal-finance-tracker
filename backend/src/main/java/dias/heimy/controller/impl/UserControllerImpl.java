package dias.heimy.controller.impl;

import static org.springframework.http.HttpStatus.CREATED;

import dias.heimy.controller.UserController;
import dias.heimy.dto.request.UserRegisterRequest;
import dias.heimy.dto.request.UserUpdateRequest;
import dias.heimy.dto.response.UserResponse;
import dias.heimy.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<UserResponse>> listUsers(Pageable pageable, String authorizationHeader) {
        log.info("Requisição para listar usuários recebida");
        Page<UserResponse> users = userService.listUsers(pageable, authorizationHeader);
        log.info("Lista de usuários retornada com {} elementos", users.getTotalElements());
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
}

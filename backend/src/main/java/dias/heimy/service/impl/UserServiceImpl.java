package dias.heimy.service.impl;

import static dias.heimy.domain.enums.ErrorCode.ADMIN_AUTH_INSUFFICIENT;
import static dias.heimy.domain.enums.ErrorCode.ADMIN_AUTH_REQUIRED;
import static dias.heimy.domain.enums.ErrorCode.INVALID_TOKEN;
import static dias.heimy.domain.enums.ErrorCode.OPERATION_NOT_PERMITTED;
import static dias.heimy.domain.enums.ErrorCode.USER_ALREADY_EXISTS;
import static dias.heimy.domain.enums.UserRole.ADMIN;

import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.domain.entity.User;
import dias.heimy.domain.enums.UserRole;
import dias.heimy.domain.exception.BusinessException;
import dias.heimy.domain.exception.UserAlreadyExistsException;
import dias.heimy.domain.exception.UserNotFoundException;
import dias.heimy.dto.mapper.UserMapper;
import dias.heimy.dto.request.UserRegisterRequest;
import dias.heimy.dto.request.UserUpdateRequest;
import dias.heimy.dto.response.PageResponse;
import dias.heimy.dto.response.UserResponse;
import dias.heimy.repository.UserRepository;
import dias.heimy.service.UserService;
import dias.heimy.util.JwtValidationUtil;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found with id: ";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtValidationUtil jwtValidationUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(UserRegisterRequest request, String authorizationHeader) {
        validateUserCreation(request.email(), request.role(), authorizationHeader);

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        User savedUser = userRepository.save(user);

        log.info("Usuário criado com sucesso: {} com role: {}", request.email(), request.role());
        return userMapper.toResponse(savedUser);
    }

    @Override
    public PageResponse<UserResponse> listUsers(Pageable pageable, String authorizationHeader) {
        log.info("Listando usuários com paginação: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<User> users = userRepository.findAll(pageable);
        var userResponses =
                users.getContent().stream().map(userMapper::toResponse).toList();

        return PageResponse.of(users.getNumber(), users.getSize(), users.getTotalElements(), userResponses);
    }

    @Override
    public UserResponse getUserById(UUID id, String authorizationHeader) {
        User user =
                userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE + id));

        validateUserAccess(user, authorizationHeader);

        log.info("Buscando usuário por ID: {}", id);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest request, String authorizationHeader) {
        User existingUser =
                userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE + id));

        validateUserUpdateAccess(existingUser, request, authorizationHeader);

        log.info("Atualizando usuário: {}", id);

        if (request.name() != null && !request.name().trim().isEmpty()) {
            existingUser.setName(request.name());
        }

        if (request.email() != null && !request.email().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new BusinessException(USER_ALREADY_EXISTS, "Email já existe: " + request.email());
            }
            existingUser.setEmail(request.email());
        }

        if (request.password() != null && !request.password().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(request.password()));
        }

        if (request.role() != null && request.role() != existingUser.getRole()) {
            if (Boolean.TRUE.equals(existingUser.getIsSystemAdmin())) {
                throw new BusinessException(
                        OPERATION_NOT_PERMITTED, "O perfil do administrador padrão do sistema não pode ser alterado");
            }
            if (!jwtValidationUtil.isAdminAuthenticated(authorizationHeader)) {
                throw new BusinessException(
                        ADMIN_AUTH_INSUFFICIENT, "Apenas administradores podem alterar roles de usuários");
            }
            existingUser.setRole(request.role());
        }

        User savedUser = userRepository.save(existingUser);
        log.info("Usuário atualizado com sucesso: {}", id);

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id, String authorizationHeader) {
        User existingUser =
                userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE + id));

        if (Boolean.TRUE.equals(existingUser.getIsSystemAdmin())) {
            throw new BusinessException(
                    OPERATION_NOT_PERMITTED, "O administrador padrão do sistema não pode ser deletado");
        }

        validateUserAccess(existingUser, authorizationHeader);

        log.info("Deletando usuário: {}", id);
        userRepository.delete(existingUser);
        log.info("Usuário deletado com sucesso: {}", id);
    }

    private void validateUserAccess(User user, String authorizationHeader) {
        if (!jwtValidationUtil.isAdminAuthenticated(authorizationHeader)) {
            String tokenEmail = extractEmailFromToken(authorizationHeader);
            if (!user.getEmail().equals(tokenEmail)) {
                throw new BusinessException(ADMIN_AUTH_INSUFFICIENT, "Usuários só podem acessar seus próprios dados");
            }
        }
    }

    private void validateUserUpdateAccess(User user, UserUpdateRequest request, String authorizationHeader) {
        boolean isAdmin = jwtValidationUtil.isAdminAuthenticated(authorizationHeader);

        if (!isAdmin) {
            String tokenEmail = extractEmailFromToken(authorizationHeader);
            if (!user.getEmail().equals(tokenEmail)) {
                throw new BusinessException(ADMIN_AUTH_INSUFFICIENT, "Usuários só podem atualizar seus próprios dados");
            }

            if (request.role() != null && request.role() != user.getRole()) {
                throw new BusinessException(
                        ADMIN_AUTH_INSUFFICIENT, "Apenas administradores podem alterar roles de usuários");
            }
        }
    }

    private void validateUserCreation(String email, UserRole role, String authorizationHeader) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }

        if (role == ADMIN) {
            if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
                log.warn("Tentativa de criar usuário ADMIN sem token de autorização");
                throw new BusinessException(
                        ADMIN_AUTH_REQUIRED, "Apenas administradores logados podem criar usuários ADMIN");
            }

            if (!jwtValidationUtil.isAdminAuthenticated(authorizationHeader)) {
                log.warn("Tentativa de criar usuário ADMIN com token inválido");
                throw new BusinessException(
                        ADMIN_AUTH_INSUFFICIENT, "Apenas administradores podem criar outros administradores");
            }
        }

        log.debug("Validação de criação de usuário aprovada para email: {} com role: {}", email, role);
    }

    private String extractEmailFromToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new BusinessException(INVALID_TOKEN, "Token de autorização inválido");
        }

        String token = authorizationHeader.substring(7);
        return jwtTokenProvider.extractEmailFromToken(token);
    }
}

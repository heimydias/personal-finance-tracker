package dias.heimy.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.domain.entity.User;
import dias.heimy.domain.enums.UserRole;
import dias.heimy.domain.exception.DomainException;
import dias.heimy.domain.exception.UserAlreadyExistsException;
import dias.heimy.domain.exception.UserNotFoundException;
import dias.heimy.dto.mapper.UserMapper;
import dias.heimy.dto.request.UserRegisterRequest;
import dias.heimy.dto.request.UserUpdateRequest;
import dias.heimy.dto.response.UserResponse;
import dias.heimy.repository.UserRepository;
import dias.heimy.util.JwtValidationUtil;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for UserServiceImpl")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtValidationUtil jwtValidationUtil;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Should create user successfully when valid data provided")
    void shouldCreateUser_WhenValidData() {

        var request = new UserRegisterRequest("test@example.com", "password123", UserRole.USER);
        var user = createTestUser();
        var savedUser = createTestUser();
        var expectedResponse = createUserResponse();

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        var result = userService.createUser(request, null);

        assertThat(result).isEqualTo(expectedResponse);
        verify(userRepository).existsByEmail(request.email());
        verify(userMapper).toEntity(request);
        verify(passwordEncoder).encode(request.password());
        verify(userRepository).save(user);
        verify(userMapper).toResponse(savedUser);
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email already exists")
    void shouldThrowException_WhenEmailAlreadyExists() {

        var request = new UserRegisterRequest("existing@example.com", "password123", UserRole.USER);
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request, null)).isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository).existsByEmail(request.email());
        verifyNoInteractions(userMapper, passwordEncoder);
    }

    @Test
    @DisplayName("Should throw DomainException when creating ADMIN without authorization")
    void shouldThrowException_WhenCreatingAdminWithoutAuth() {

        var request = new UserRegisterRequest("admin@example.com", "password123", UserRole.ADMIN);
        when(userRepository.existsByEmail(request.email())).thenReturn(false);

        assertThatThrownBy(() -> userService.createUser(request, null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Apenas administradores logados podem criar usuários ADMIN");

        verify(userRepository).existsByEmail(request.email());
        verifyNoInteractions(userMapper, passwordEncoder);
    }

    @Test
    @DisplayName("Should create ADMIN when authenticated as admin")
    void shouldCreateAdmin_WhenAuthenticatedAsAdmin() {

        var request = new UserRegisterRequest("admin@example.com", "password123", UserRole.ADMIN);
        var user = createTestUser();
        user.setRole(UserRole.ADMIN);
        var savedUser = createTestUser();
        savedUser.setRole(UserRole.ADMIN);
        var expectedResponse = createUserResponse();

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(jwtValidationUtil.isAdminAuthenticated("Bearer valid_token")).thenReturn(true);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        var result = userService.createUser(request, "Bearer valid_token");

        assertThat(result).isEqualTo(expectedResponse);
        verify(jwtValidationUtil).isAdminAuthenticated("Bearer valid_token");
    }

    @Test
    @DisplayName("Should list users successfully when admin authenticated")
    void shouldListUsers_WhenAdminAuthenticated() {

        var pageable = PageRequest.of(0, 10);
        var users = java.util.List.of(createTestUser());
        var usersPage = new PageImpl<>(users, pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(usersPage);
        when(userMapper.toResponse(any(User.class))).thenReturn(createUserResponse());

        var result = userService.listUsers(pageable, "Bearer admin_token");

        assertThat(result).hasSize(1);
        verify(userRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should get user by ID successfully when user accesses own data")
    void shouldGetUserById_WhenUserAccessesOwnData() {

        var userId = UUID.randomUUID();
        var user = createTestUser();
        user.setId(userId);
        user.setEmail("user@example.com");
        var expectedResponse = createUserResponse();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtValidationUtil.isAdminAuthenticated("Bearer user_token")).thenReturn(false);
        when(jwtTokenProvider.extractEmailFromToken("user_token")).thenReturn("user@example.com");
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        var result = userService.getUserById(userId, "Bearer user_token");

        assertThat(result).isEqualTo(expectedResponse);
        verify(userRepository).findById(userId);
        verify(jwtTokenProvider).extractEmailFromToken("user_token");
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found")
    void shouldThrowException_WhenUserNotFound() {

        var userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId, "Bearer token"))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should throw DomainException when user tries to access other user data")
    void shouldThrowException_WhenUserTriesToAccessOtherUserData() {

        var userId = UUID.randomUUID();
        var user = createTestUser();
        user.setId(userId);
        user.setEmail("owner@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtValidationUtil.isAdminAuthenticated("Bearer user_token")).thenReturn(false);
        when(jwtTokenProvider.extractEmailFromToken("user_token")).thenReturn("other@example.com");

        assertThatThrownBy(() -> userService.getUserById(userId, "Bearer user_token"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Usuários só podem acessar seus próprios dados");

        verify(userRepository).findById(userId);
        verify(jwtTokenProvider).extractEmailFromToken("user_token");
    }

    @Test
    @DisplayName("Should update user successfully when user updates own data")
    void shouldUpdateUser_WhenUserUpdatesOwnData() {

        var userId = UUID.randomUUID();
        var request = new UserUpdateRequest("newemail@example.com", "newpassword", null);
        var existingUser = createTestUser();
        existingUser.setId(userId);
        existingUser.setEmail("user@example.com");
        var updatedUser = createTestUser();
        var expectedResponse = createUserResponse();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(jwtValidationUtil.isAdminAuthenticated("Bearer user_token")).thenReturn(false);
        when(jwtTokenProvider.extractEmailFromToken("user_token")).thenReturn("user@example.com");
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newpassword")).thenReturn("encoded_new_password");
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(expectedResponse);

        var result = userService.updateUser(userId, request, "Bearer user_token");

        assertThat(result).isEqualTo(expectedResponse);
        verify(userRepository).existsByEmail("newemail@example.com");
        verify(passwordEncoder).encode("newpassword");
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("Should throw DomainException when user tries to change role")
    void shouldThrowException_WhenUserTriesToChangeRole() {

        var userId = UUID.randomUUID();
        var request = new UserUpdateRequest(null, null, UserRole.ADMIN);
        var existingUser = createTestUser();
        existingUser.setId(userId);
        existingUser.setEmail("user@example.com");
        existingUser.setRole(UserRole.USER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(jwtValidationUtil.isAdminAuthenticated("Bearer user_token")).thenReturn(false);
        when(jwtTokenProvider.extractEmailFromToken("user_token")).thenReturn("user@example.com");

        assertThatThrownBy(() -> userService.updateUser(userId, request, "Bearer user_token"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Apenas administradores podem alterar roles de usuários");

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should delete user successfully when user deletes own account")
    void shouldDeleteUser_WhenUserDeletesOwnAccount() {

        var userId = UUID.randomUUID();
        var user = createTestUser();
        user.setId(userId);
        user.setEmail("user@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtValidationUtil.isAdminAuthenticated("Bearer user_token")).thenReturn(false);
        when(jwtTokenProvider.extractEmailFromToken("user_token")).thenReturn("user@example.com");

        userService.deleteUser(userId, "Bearer user_token");

        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
        verify(jwtTokenProvider).extractEmailFromToken("user_token");
    }

    @Test
    @DisplayName("Should admin access any user data")
    void shouldAdminAccessAnyUserData() {

        var userId = UUID.randomUUID();
        var user = createTestUser();
        user.setId(userId);
        user.setEmail("anyuser@example.com");
        var expectedResponse = createUserResponse();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtValidationUtil.isAdminAuthenticated("Bearer admin_token")).thenReturn(true);
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        var result = userService.getUserById(userId, "Bearer admin_token");

        assertThat(result).isEqualTo(expectedResponse);
        verify(userRepository).findById(userId);
        verify(jwtValidationUtil).isAdminAuthenticated("Bearer admin_token");
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    @DisplayName("Should throw DomainException when invalid token format")
    void shouldThrowException_WhenInvalidTokenFormat() {

        var userId = UUID.randomUUID();
        var user = createTestUser();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtValidationUtil.isAdminAuthenticated("invalid_token")).thenReturn(false);

        assertThatThrownBy(() -> userService.getUserById(userId, "invalid_token"))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Token de autorização inválido");

        verify(userRepository).findById(userId);
    }

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPassword("encoded_password");
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setCreatedBy("SYSTEM");
        user.setLastModifiedBy("SYSTEM");
        return user;
    }

    private UserResponse createUserResponse() {
        return new UserResponse(
                "test-id",
                "test@example.com",
                UserRole.USER,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "SYSTEM",
                "SYSTEM");
    }
}

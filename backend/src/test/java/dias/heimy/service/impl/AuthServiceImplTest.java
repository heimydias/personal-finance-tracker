package dias.heimy.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.domain.entity.User;
import dias.heimy.domain.enums.UserRole;
import dias.heimy.domain.exception.InvalidTokenException;
import dias.heimy.domain.exception.UserAlreadyExistsException;
import dias.heimy.domain.exception.UserNotFoundException;
import dias.heimy.domain.valueobject.JwtToken;
import dias.heimy.domain.valueobject.RefreshToken;
import dias.heimy.dto.mapper.UserMapper;
import dias.heimy.dto.request.LoginRequest;
import dias.heimy.dto.request.RefreshTokenRequest;
import dias.heimy.dto.request.UserRegisterRequest;
import dias.heimy.dto.response.UserResponse;
import dias.heimy.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for AuthServiceImpl")
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("Should register user successfully when valid data provided")
    void shouldRegisterUser_WhenValidData() {

        var request = new UserRegisterRequest("test@example.com", "password123", UserRole.USER);
        var user = createTestUser();
        var expectedResponse = createUserResponse();

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(user);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        var result = authService.register(request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(userRepository).existsByEmail(request.email());
        verify(userMapper).toEntity(request);
        verify(passwordEncoder).encode(request.password());
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Should throw UserAlreadyExistsException when email already exists")
    void shouldThrowException_WhenEmailAlreadyExists() {

        var request = new UserRegisterRequest("existing@example.com", "password123", UserRole.USER);
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request)).isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository).existsByEmail(request.email());
        verifyNoInteractions(userMapper, passwordEncoder);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should login successfully when valid credentials provided")
    void shouldLogin_WhenValidCredentials() {

        var request = new LoginRequest("test@example.com", "password123");
        var user = createTestUser();
        var authentication = createAuthentication();
        var accessToken =
                new JwtToken("access_token", Instant.now(), Instant.now().plusSeconds(3600));
        var refreshToken = new RefreshToken("refresh_token", Instant.now().plusSeconds(86400));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(authentication)).thenReturn(accessToken);
        when(jwtTokenProvider.generateRefreshToken(request.email())).thenReturn(refreshToken);

        var result = authService.login(request);

        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo("access_token");
        assertThat(result.refreshToken()).isEqualTo("refresh_token");
        assertThat(result.userEmail()).isEqualTo(user.getEmail());
        assertThat(result.userRole()).isEqualTo(user.getRole().getRoleName());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(request.email());
        verify(jwtTokenProvider).generateAccessToken(authentication);
        verify(jwtTokenProvider).generateRefreshToken(request.email());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when authentication fails")
    void shouldThrowException_WhenAuthenticationFails() {

        var request = new LoginRequest("test@example.com", "wrong_password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid credentials");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(userRepository, jwtTokenProvider);
    }

    @Test
    @DisplayName("Should refresh token successfully when valid refresh token provided")
    void shouldRefreshToken_WhenValidRefreshToken() {

        var request = new RefreshTokenRequest("valid_refresh_token");
        var user = createTestUser();
        var accessToken =
                new JwtToken("new_access_token", Instant.now(), Instant.now().plusSeconds(3600));
        var refreshToken = new RefreshToken("new_refresh_token", Instant.now().plusSeconds(86400));

        when(jwtTokenProvider.validateRefreshToken(request.refreshToken())).thenReturn(true);
        when(jwtTokenProvider.extractEmailFromRefreshToken(request.refreshToken()))
                .thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(
                        user.getEmail(), user.getRole().getAuthority()))
                .thenReturn(accessToken);
        when(jwtTokenProvider.generateRefreshToken(user.getEmail())).thenReturn(refreshToken);

        var result = authService.refreshToken(request);

        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo("new_access_token");
        assertThat(result.refreshToken()).isEqualTo("new_refresh_token");
        assertThat(result.userEmail()).isEqualTo(user.getEmail());

        verify(jwtTokenProvider).validateRefreshToken(request.refreshToken());
        verify(jwtTokenProvider).extractEmailFromRefreshToken(request.refreshToken());
        verify(userRepository).findByEmail(user.getEmail());
        verify(jwtTokenProvider)
                .generateAccessToken(user.getEmail(), user.getRole().getAuthority());
        verify(jwtTokenProvider).generateRefreshToken(user.getEmail());
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when refresh token is invalid")
    void shouldThrowException_WhenInvalidRefreshToken() {

        var request = new RefreshTokenRequest("invalid_refresh_token");
        when(jwtTokenProvider.validateRefreshToken(request.refreshToken())).thenReturn(false);

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("Invalid refresh token");

        verify(jwtTokenProvider).validateRefreshToken(request.refreshToken());
        verify(jwtTokenProvider, never()).extractEmailFromRefreshToken(anyString());
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found during refresh")
    void shouldThrowException_WhenUserNotFoundDuringRefresh() {

        var request = new RefreshTokenRequest("valid_refresh_token");
        var email = "notfound@example.com";

        when(jwtTokenProvider.validateRefreshToken(request.refreshToken())).thenReturn(true);
        when(jwtTokenProvider.extractEmailFromRefreshToken(request.refreshToken()))
                .thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refreshToken(request)).isInstanceOf(UserNotFoundException.class);

        verify(jwtTokenProvider).validateRefreshToken(request.refreshToken());
        verify(jwtTokenProvider).extractEmailFromRefreshToken(request.refreshToken());
        verify(userRepository).findByEmail(email);
    }

    // Helper methods
    private User createTestUser() {
        var user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPassword("encoded_password");
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private UserResponse createUserResponse() {
        return new UserResponse("test-id", "test@example.com", UserRole.USER, LocalDateTime.now(), LocalDateTime.now());
    }

    private Authentication createAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                "test@example.com", "password123", java.util.List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}

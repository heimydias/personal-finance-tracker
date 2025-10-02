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
import dias.heimy.domain.exception.InvalidTokenException;
import dias.heimy.domain.valueobject.JwtToken;
import dias.heimy.domain.valueobject.RefreshToken;
import dias.heimy.dto.request.LoginRequest;
import dias.heimy.dto.request.RefreshTokenRequest;
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
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

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
        when(jwtTokenProvider.generateAccessToken("test@example.com", "ROLE_USER"))
                .thenReturn(accessToken);
        when(jwtTokenProvider.generateRefreshToken(request.email())).thenReturn(refreshToken);

        var result = authService.login(request);

        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo("access_token");
        assertThat(result.refreshToken()).isEqualTo("refresh_token");
        assertThat(result.userId()).isEqualTo(user.getId().toString());
        assertThat(result.userName()).isEqualTo(user.getName());
        assertThat(result.userEmail()).isEqualTo(user.getEmail());
        assertThat(result.userRole()).isEqualTo("USER");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(request.email());
        verify(jwtTokenProvider).generateAccessToken("test@example.com", "ROLE_USER");
        verify(jwtTokenProvider).generateRefreshToken(request.email());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when invalid credentials")
    void shouldThrowException_WhenInvalidCredentials() {

        var request = new LoginRequest("test@example.com", "wrong_password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> authService.login(request)).isInstanceOf(BadCredentialsException.class);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(userRepository, jwtTokenProvider);
    }

    @Test
    @DisplayName("Should throw RuntimeException when user not found during login")
    void shouldThrowException_WhenUserNotFoundDuringLogin() {

        var request = new LoginRequest("test@example.com", "password123");
        var authentication = createAuthentication();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(request.email());
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    @DisplayName("Should refresh token successfully when valid refresh token")
    void shouldRefreshToken_WhenValidRefreshToken() {

        var request = new RefreshTokenRequest("valid_refresh_token");
        var user = createTestUser();
        var accessToken =
                new JwtToken("new_access_token", Instant.now(), Instant.now().plusSeconds(3600));
        var refreshToken = new RefreshToken("new_refresh_token", Instant.now().plusSeconds(86400));

        when(jwtTokenProvider.extractEmailFromRefreshToken(request.refreshToken()))
                .thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(user.getEmail(), "ROLE_USER")).thenReturn(accessToken);
        when(jwtTokenProvider.generateRefreshToken(user.getEmail())).thenReturn(refreshToken);

        var result = authService.refreshToken(request);

        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo("new_access_token");
        assertThat(result.refreshToken()).isEqualTo("new_refresh_token");

        verify(jwtTokenProvider).extractEmailFromRefreshToken(request.refreshToken());
        verify(userRepository).findByEmail(user.getEmail());
        verify(jwtTokenProvider).generateAccessToken(user.getEmail(), "ROLE_USER");
        verify(jwtTokenProvider).generateRefreshToken(user.getEmail());
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when refresh token is invalid")
    void shouldThrowException_WhenInvalidRefreshToken() {

        var request = new RefreshTokenRequest("invalid_refresh_token");
        when(jwtTokenProvider.extractEmailFromRefreshToken(request.refreshToken()))
                .thenThrow(new InvalidTokenException("Invalid refresh token"));

        assertThatThrownBy(() -> authService.refreshToken(request)).isInstanceOf(InvalidTokenException.class);

        verify(jwtTokenProvider).extractEmailFromRefreshToken(request.refreshToken());
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should throw RuntimeException when user not found during refresh")
    void shouldThrowException_WhenUserNotFoundDuringRefresh() {

        var request = new RefreshTokenRequest("valid_refresh_token");
        var email = "nonexistent@example.com";

        when(jwtTokenProvider.extractEmailFromRefreshToken(request.refreshToken()))
                .thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado");

        verify(jwtTokenProvider).extractEmailFromRefreshToken(request.refreshToken());
        verify(userRepository).findByEmail(email);
    }

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encoded_password");
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private Authentication createAuthentication() {
        return new UsernamePasswordAuthenticationToken(
                "test@example.com", "password123", java.util.List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}

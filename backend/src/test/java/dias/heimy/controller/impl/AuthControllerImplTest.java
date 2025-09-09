package dias.heimy.controller.impl;

import static dias.heimy.constants.PathConstants.AUTH_LOGIN;
import static dias.heimy.constants.PathConstants.AUTH_REFRESH;
import static dias.heimy.constants.PathConstants.AUTH_REGISTER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dias.heimy.config.security.JwtAuthenticationFilter;
import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.config.security.SecurityConfig;
import dias.heimy.config.web.ExceptionHandlerAdvice;
import dias.heimy.domain.enums.UserRole;
import dias.heimy.domain.exception.InvalidTokenException;
import dias.heimy.domain.exception.UserAlreadyExistsException;
import dias.heimy.dto.request.LoginRequest;
import dias.heimy.dto.request.RefreshTokenRequest;
import dias.heimy.dto.request.UserRegisterRequest;
import dias.heimy.dto.response.AuthenticationResponse;
import dias.heimy.dto.response.UserResponse;
import dias.heimy.repository.UserRepository;
import dias.heimy.service.CustomUserDetailsService;
import dias.heimy.service.impl.AuthServiceImpl;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {AuthControllerImpl.class, SecurityConfig.class, ExceptionHandlerAdvice.class})
@DisplayName("Tests for AuthControllerImpl")
class AuthControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthServiceImpl authService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should register user successfully when valid data provided")
    void shouldRegisterUser_WhenValidData() throws Exception {
        // Given
        var request = new UserRegisterRequest("test@example.com", "password123", UserRole.USER);
        var expectedResponse = new UserResponse(
                "test-id", "test@example.com", UserRole.USER, LocalDateTime.now(), LocalDateTime.now());

        when(authService.register(any(UserRegisterRequest.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post(AUTH_REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(authService).register(any(UserRegisterRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when register with invalid email")
    void shouldReturnBadRequest_WhenRegisterWithInvalidEmail() throws Exception {
        // Given
        var request = new UserRegisterRequest("invalid-email", "password123", UserRole.USER);

        // When & Then
        mockMvc.perform(post(AUTH_REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 409 when user already exists")
    void shouldReturnConflict_WhenUserAlreadyExists() throws Exception {
        // Given
        var request = new UserRegisterRequest("existing@example.com", "password123", UserRole.USER);
        when(authService.register(any(UserRegisterRequest.class)))
                .thenThrow(new UserAlreadyExistsException("User already exists"));

        // When & Then
        mockMvc.perform(post(AUTH_REGISTER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(authService).register(any(UserRegisterRequest.class));
    }

    @Test
    @DisplayName("Should login successfully when valid credentials provided")
    void shouldLogin_WhenValidCredentials() throws Exception {
        // Given
        var request = new LoginRequest("test@example.com", "password123");
        var expectedResponse = new AuthenticationResponse(
                "access_token", "refresh_token", "Bearer", 3600L, "test@example.com", "USER");

        when(authService.login(any(LoginRequest.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post(AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access_token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh_token"))
                .andExpect(jsonPath("$.userEmail").value("test@example.com"))
                .andExpect(jsonPath("$.userRole").value("USER"));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Should return 401 when login with invalid credentials")
    void shouldReturnUnauthorized_WhenInvalidCredentials() throws Exception {
        // Given
        var request = new LoginRequest("test@example.com", "wrong_password");
        when(authService.login(any(LoginRequest.class))).thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post(AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when login with invalid email format")
    void shouldReturnBadRequest_WhenLoginWithInvalidEmail() throws Exception {
        // Given
        var request = new LoginRequest("invalid-email", "password123");

        // When & Then
        mockMvc.perform(post(AUTH_LOGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should refresh token successfully when valid refresh token provided")
    void shouldRefreshToken_WhenValidRefreshToken() throws Exception {
        // Given
        var request = new RefreshTokenRequest("valid_refresh_token");
        var expectedResponse = new AuthenticationResponse(
                "new_access_token", "new_refresh_token", "Bearer", 3600L, "test@example.com", "USER");

        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post(AUTH_REFRESH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new_access_token"))
                .andExpect(jsonPath("$.refreshToken").value("new_refresh_token"))
                .andExpect(jsonPath("$.userEmail").value("test@example.com"));

        verify(authService).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    @DisplayName("Should return 401 when refresh with invalid token")
    void shouldReturnUnauthorized_WhenInvalidRefreshToken() throws Exception {
        // Given
        var request = new RefreshTokenRequest("invalid_refresh_token");
        when(authService.refreshToken(any(RefreshTokenRequest.class)))
                .thenThrow(new InvalidTokenException("Invalid refresh token"));

        // When & Then
        mockMvc.perform(post(AUTH_REFRESH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authService).refreshToken(any(RefreshTokenRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when refresh with empty token")
    void shouldReturnBadRequest_WhenEmptyRefreshToken() throws Exception {
        // Given
        var request = new RefreshTokenRequest("");

        // When & Then
        mockMvc.perform(post(AUTH_REFRESH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

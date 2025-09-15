package dias.heimy.controller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dias.heimy.dto.request.LoginRequest;
import dias.heimy.dto.request.RefreshTokenRequest;
import dias.heimy.dto.response.AuthenticationResponse;
import dias.heimy.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for AuthControllerImpl")
class AuthControllerImplTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthControllerImpl authController;

    @Test
    @DisplayName("Should login successfully when valid credentials provided")
    void shouldLogin_WhenValidCredentials() {

        var request = new LoginRequest("test@example.com", "password123");
        var expectedResponse = new AuthenticationResponse(
                "access_token", "refresh_token", "Bearer", 3600L, "test@example.com", "USER");

        when(authService.login(any(LoginRequest.class))).thenReturn(expectedResponse);

        var result = authController.token(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Should refresh token successfully when valid refresh token provided")
    void shouldRefreshToken_WhenValidRefreshToken() {

        var request = new RefreshTokenRequest("valid_refresh_token");
        var expectedResponse = new AuthenticationResponse(
                "new_access_token", "new_refresh_token", "Bearer", 3600L, "test@example.com", "USER");

        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(expectedResponse);

        var result = authController.refreshToken(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(expectedResponse);
        verify(authService).refreshToken(any(RefreshTokenRequest.class));
    }
}

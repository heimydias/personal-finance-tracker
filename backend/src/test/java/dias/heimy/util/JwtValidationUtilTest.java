package dias.heimy.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.domain.enums.UserRole;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for JwtValidationUtil")
class JwtValidationUtilTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private JwtValidationUtil jwtValidationUtil;

    @Test
    @DisplayName("Should return true when valid admin token provided")
    void shouldReturnTrue_WhenValidAdminToken() {

        var authHeader = "Bearer valid_admin_token";
        when(jwtTokenProvider.validateToken("valid_admin_token")).thenReturn(true);
        when(jwtTokenProvider.extractRoleFromToken("valid_admin_token")).thenReturn("ADMIN");

        var result = jwtValidationUtil.isAdminAuthenticated(authHeader);

        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAdminAuthenticationCases")
    @DisplayName("Should return false for invalid admin authentication cases")
    void shouldReturnFalse_ForInvalidAdminAuthenticationCases(String authHeader, String token, String scenario) {
        if ("invalid_token".equals(token)) {
            when(jwtTokenProvider.validateToken(token)).thenReturn(false);
        } else if ("valid_user_token".equals(token)) {
            when(jwtTokenProvider.validateToken(token)).thenReturn(true);
            when(jwtTokenProvider.extractRoleFromToken(token)).thenReturn("USER");
        } else if ("problematic_token".equals(token)) {
            when(jwtTokenProvider.validateToken(token)).thenThrow(new RuntimeException("JWT error"));
        }

        var result = jwtValidationUtil.isAdminAuthenticated(authHeader);

        assertThat(result).isFalse();
    }

    private static Stream<Arguments> provideInvalidAdminAuthenticationCases() {
        return Stream.of(
                Arguments.of(null, null, "null authorization header"),
                Arguments.of("", null, "empty authorization header"),
                Arguments.of("Invalid header", null, "header doesn't start with Bearer"),
                Arguments.of("Bearer invalid_token", "invalid_token", "invalid token"),
                Arguments.of("Bearer valid_user_token", "valid_user_token", "user role instead of admin"),
                Arguments.of("Bearer problematic_token", "problematic_token", "exception during validation"));
    }

    @ParameterizedTest
    @MethodSource("provideUserRoleDeterminationCases")
    @DisplayName("Should determine USER role for various scenarios")
    void shouldDetermineUserRole_ForVariousScenarios(
            UserRole requestedRole, String authHeader, String token, String scenario) {
        if ("invalid_token".equals(token)) {
            when(jwtTokenProvider.validateToken(token)).thenReturn(false);
        } else if ("valid_user_token".equals(token)) {
            when(jwtTokenProvider.validateToken(token)).thenReturn(true);
            when(jwtTokenProvider.extractRoleFromToken(token)).thenReturn("USER");
        }

        var result = jwtValidationUtil.determineUserRole(requestedRole, authHeader);

        assertThat(result).isEqualTo(UserRole.USER);
    }

    private static Stream<Arguments> provideUserRoleDeterminationCases() {
        return Stream.of(
                Arguments.of(null, null, null, "no role provided and no auth header"),
                Arguments.of(UserRole.USER, "Bearer token", null, "USER role provided"),
                Arguments.of(UserRole.ADMIN, "Bearer invalid_token", "invalid_token", "ADMIN role but invalid token"),
                Arguments.of(
                        UserRole.ADMIN,
                        "Bearer valid_user_token",
                        "valid_user_token",
                        "ADMIN role but user is not admin"));
    }

    @Test
    @DisplayName("Should determine ADMIN role when ADMIN role provided and valid admin token")
    void shouldDetermineAdminRole_WhenAdminRoleAndValidToken() {

        var authHeader = "Bearer valid_admin_token";
        when(jwtTokenProvider.validateToken("valid_admin_token")).thenReturn(true);
        when(jwtTokenProvider.extractRoleFromToken("valid_admin_token")).thenReturn("ADMIN");

        var result = jwtValidationUtil.determineUserRole(UserRole.ADMIN, authHeader);

        assertThat(result).isEqualTo(UserRole.ADMIN);
    }

    @Test
    @DisplayName("Should return true when admin token has ROLE_ADMIN format")
    void shouldReturnTrue_WhenAdminTokenHasRoleAdminFormat() {

        var authHeader = "Bearer valid_admin_token";
        when(jwtTokenProvider.validateToken("valid_admin_token")).thenReturn(true);
        when(jwtTokenProvider.extractRoleFromToken("valid_admin_token")).thenReturn("ROLE_ADMIN");

        var result = jwtValidationUtil.isAdminAuthenticated(authHeader);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when auth header has only whitespace")
    void shouldReturnFalse_WhenAuthHeaderIsWhitespace() {

        var result = jwtValidationUtil.isAdminAuthenticated("   ");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return USER role when requested role is null")
    void shouldReturnUserRole_WhenRequestedRoleIsNull() {

        var result = jwtValidationUtil.determineUserRole(null, "Bearer token");

        assertThat(result).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("Should return USER role when auth header is empty")
    void shouldReturnUserRole_WhenAuthHeaderIsEmpty() {

        var result = jwtValidationUtil.determineUserRole(UserRole.ADMIN, "");

        assertThat(result).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("Should return USER role when auth header is whitespace")
    void shouldReturnUserRole_WhenAuthHeaderIsWhitespace() {

        var result = jwtValidationUtil.determineUserRole(UserRole.ADMIN, "   ");

        assertThat(result).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("Should determine ADMIN role when token has ROLE_ADMIN format")
    void shouldDetermineAdminRole_WhenTokenHasRoleAdminFormat() {

        var authHeader = "Bearer valid_admin_token";
        when(jwtTokenProvider.validateToken("valid_admin_token")).thenReturn(true);
        when(jwtTokenProvider.extractRoleFromToken("valid_admin_token")).thenReturn("ROLE_ADMIN");

        var result = jwtValidationUtil.determineUserRole(UserRole.ADMIN, authHeader);

        assertThat(result).isEqualTo(UserRole.ADMIN);
    }
}

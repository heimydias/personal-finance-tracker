package dias.heimy.util;

import dias.heimy.config.security.JwtTokenProvider;
import dias.heimy.domain.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidationUtil {

    private final JwtTokenProvider jwtTokenProvider;

    public boolean isAdminAuthenticated(String authorizationHeader) {
        try {
            if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
                return false;
            }

            if (!authorizationHeader.startsWith("Bearer ")) {
                log.debug("Authorization header não possui formato Bearer correto");
                return false;
            }

            String token = authorizationHeader.substring(7);

            if (!jwtTokenProvider.validateToken(token)) {
                log.debug("Token JWT inválido");
                return false;
            }

            String role = jwtTokenProvider.extractRoleFromToken(token);
            boolean isAdmin = "ADMIN".equals(role) || "ROLE_ADMIN".equals(role);

            if (!isAdmin) {
                log.debug("Token válido mas usuário não possui role ADMIN: {}", role);
            }

            return isAdmin;

        } catch (Exception e) {
            log.debug("Erro ao validar token de admin: {}", e.getMessage());
            return false;
        }
    }

    public UserRole determineUserRole(UserRole requestedRole, String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            return UserRole.USER;
        }

        if (requestedRole == UserRole.USER) {
            return UserRole.USER;
        }

        if (requestedRole == UserRole.ADMIN) {
            if (isAdminAuthenticated(authorizationHeader)) {
                return UserRole.ADMIN;
            } else {
                log.warn("Tentativa de criar usuário ADMIN sem autenticação de admin válida");
                return UserRole.USER;
            }
        }

        return UserRole.USER;
    }
}

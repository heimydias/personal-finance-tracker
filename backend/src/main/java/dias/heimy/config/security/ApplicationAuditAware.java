package dias.heimy.config.security;

import static java.util.Optional.of;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

@Slf4j
@Configuration
public class ApplicationAuditAware implements AuditorAware<String> {

    private static final String SYSTEM_USER = "SYSTEM";

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            log.debug("Usuário não autenticado - usando '{}' como auditor", SYSTEM_USER);
            return of(SYSTEM_USER);
        }

        try {
            Object principal = authentication.getPrincipal();

            String username =
                    switch (principal) {
                        case UserDetails userDetails -> userDetails.getUsername();
                        case String stringPrincipal -> stringPrincipal;
                        default -> {
                            log.warn(
                                    "Tipo de principal não reconhecido: {}",
                                    principal.getClass().getName());
                            yield null;
                        }
                    };

            if (username == null) {
                return of(SYSTEM_USER);
            }

            log.debug("Usuário autenticado para auditoria: {}", username);
            return of(username);
        } catch (Exception e) {
            log.warn("Erro ao extrair usuário para auditoria: {}", e.getMessage());
            return of(SYSTEM_USER);
        }
    }
}

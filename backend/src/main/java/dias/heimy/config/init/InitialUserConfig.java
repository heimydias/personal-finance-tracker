package dias.heimy.config.init;

import dias.heimy.domain.entity.User;
import dias.heimy.domain.enums.UserRole;
import dias.heimy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InitialUserConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.admin.email}")
    private String adminEmail;

    @Value("${app.jwt.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        createAdminUserIfNotExists();
        createUsersIfNotExists();
    }

    private void createAdminUserIfNotExists() {
        if (!userRepository.existsByEmail(adminEmail)) {
            log.info("Criando usuário administrador padrão: {}", adminEmail);

            User adminUser = new User();
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRole(UserRole.ADMIN);

            userRepository.save(adminUser);

            log.info("Usuário administrador criado com sucesso: {}", adminEmail);
        } else {
            log.info("Usuário administrador já existe: {}", adminEmail);
        }
    }

    private void createUsersIfNotExists() {
        log.info("Criando usuários comuns");
        for (int i = 1; i < 30; i++) {
            User user = new User();
            String email = "user" + i + "@user.com";
            if (!userRepository.existsByEmail(email)) {
                user.setEmail(email);
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode("user123"));
                user.setRole(UserRole.USER);
                userRepository.save(user);
            }
        }
        log.info("Usuários comuns criados com sucesso");
    }
}

package dias.heimy.config.init;

import static dias.heimy.domain.enums.UserRole.ADMIN;

import dias.heimy.domain.entity.User;
import dias.heimy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void run(String... args) throws Exception {
        createAdminUserIfNotExists();
    }

    private void createAdminUserIfNotExists() {
        if (!userRepository.existsByEmail(adminEmail)) {
            log.info("Criando usuário administrador padrão: {}", adminEmail);

            User adminUser = new User();
            adminUser.setName("System Admin");
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRole(ADMIN);
            adminUser.setIsSystemAdmin(true);

            userRepository.save(adminUser);

            log.info("Usuário administrador criado com sucesso: {}", adminEmail);
        } else {
            log.info("Usuário administrador já existe: {}", adminEmail);
        }
    }
}

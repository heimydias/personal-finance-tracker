package dias.heimy.config.init;

import static dias.heimy.domain.enums.UserRole.ADMIN;
import static dias.heimy.domain.enums.UserRole.USER;

import dias.heimy.domain.entity.User;
import dias.heimy.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
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

    @Value("${app.jwt.users.default-password}")
    private String defaultUserPassword;

    @Override
    @Transactional
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
            adminUser.setRole(ADMIN);

            userRepository.save(adminUser);

            log.info("Usuário administrador criado com sucesso: {}", adminEmail);
        } else {
            log.info("Usuário administrador já existe: {}", adminEmail);
        }
    }

    private void createUsersIfNotExists() {
        log.info("Criando usuários comuns");

        List<String> emails = new ArrayList<>();
        for (int i = 1; i < 30; i++) {
            emails.add("user" + i + "@user.com");
        }

        List<User> existingUsers = userRepository.findByEmailIn(emails);
        List<String> existingEmails = existingUsers.stream().map(User::getEmail).toList();
        log.info("Encontrados {} usuários já existentes", existingEmails.size());
        String encodedPassword = passwordEncoder.encode(defaultUserPassword);
        List<User> usersToCreate = emails.stream()
                .filter(email -> !existingEmails.contains(email))
                .map(email -> {
                    User user = new User();
                    user.setEmail(email);
                    user.setPassword(encodedPassword);
                    user.setRole(USER);
                    return user;
                })
                .toList();

        if (!usersToCreate.isEmpty()) {
            userRepository.saveAll(usersToCreate);
            log.info("Criados {} novos usuários comuns", usersToCreate.size());
        } else {
            log.info("Todos os usuários comuns já existem");
        }
    }
}

package dias.heimy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dias.heimy.domain.entity.User;
import dias.heimy.domain.enums.UserRole;
import dias.heimy.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for CustomUserDetailsService")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("Should load user by username successfully when user exists")
    void shouldLoadUserByUsername_WhenUserExists() {

        var email = "test@example.com";
        var user = createTestUser();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        var result = userDetailsService.loadUserByUsername(email);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(email);
        assertThat(result.getPassword()).isEqualTo("encoded_password");
        assertThat(result.getAuthorities()).hasSize(1).extracting("authority").containsExactly("ROLE_USER");
        assertThat(result.isAccountNonExpired()).isTrue();
        assertThat(result.isAccountNonLocked()).isTrue();
        assertThat(result.isCredentialsNonExpired()).isTrue();
        assertThat(result.isEnabled()).isTrue();

        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void shouldThrowException_WhenUserDoesNotExist() {

        var email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: " + email);

        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Should load admin user with correct authorities")
    void shouldLoadAdminUser_WithCorrectAuthorities() {

        var email = "admin@example.com";
        var adminUser = createAdminUser();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(adminUser));

        var result = userDetailsService.loadUserByUsername(email);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(email);
        assertThat(result.getAuthorities()).hasSize(1).extracting("authority").containsExactly("ROLE_ADMIN");

        verify(userRepository).findByEmail(email);
    }

    @Test
    @DisplayName("Should handle null email gracefully")
    void shouldHandleNullEmail_Gracefully() {

        String nullEmail = null;
        when(userRepository.findByEmail(nullEmail)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(nullEmail))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: " + nullEmail);

        verify(userRepository).findByEmail(nullEmail);
    }

    @Test
    @DisplayName("Should handle empty email gracefully")
    void shouldHandleEmptyEmail_Gracefully() {
        var emptyEmail = "";
        when(userRepository.findByEmail(emptyEmail)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(emptyEmail))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found with email: " + emptyEmail);

        verify(userRepository).findByEmail(emptyEmail);
    }

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

    private User createAdminUser() {
        var user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("admin@example.com");
        user.setPassword("encoded_admin_password");
        user.setRole(UserRole.ADMIN);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
}

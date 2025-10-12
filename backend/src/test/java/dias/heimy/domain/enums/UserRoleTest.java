package dias.heimy.domain.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for UserRole")
class UserRoleTest {

    @Test
    @DisplayName("Should have ADMIN and USER values")
    void shouldHaveAdminAndUserValues() {

        var values = UserRole.values();

        assertThat(values).hasSize(2).contains(UserRole.ADMIN, UserRole.USER);
    }

    @Test
    @DisplayName("Should get ADMIN authority")
    void shouldGetAdminAuthority() {

        var role = UserRole.ADMIN;

        assertThat(role.getAuthority()).isEqualTo("ROLE_ADMIN");
        assertThat(role.getRoleName()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Should get USER authority")
    void shouldGetUserAuthority() {

        var role = UserRole.USER;

        assertThat(role.getAuthority()).isEqualTo("ROLE_USER");
        assertThat(role.getRoleName()).isEqualTo("USER");
    }

    @Test
    @DisplayName("Should get ADMIN by name")
    void shouldGetAdminByName() {

        var role = UserRole.valueOf("ADMIN");

        assertThat(role).isEqualTo(UserRole.ADMIN);
    }

    @Test
    @DisplayName("Should get USER by name")
    void shouldGetUserByName() {

        var role = UserRole.valueOf("USER");

        assertThat(role).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("Should verify enum constants")
    void shouldVerifyEnumConstants() {

        assertThat(UserRole.ADMIN).isNotNull();
        assertThat(UserRole.USER).isNotNull();
        assertThat(UserRole.ADMIN).isNotEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("Should extract role name from authority")
    void shouldExtractRoleNameFromAuthority() {

        assertThat(UserRole.ADMIN.getRoleName()).isEqualTo("ADMIN");
        assertThat(UserRole.USER.getRoleName()).isEqualTo("USER");
        assertThat(UserRole.ADMIN.getRoleName()).doesNotContain("ROLE_");
        assertThat(UserRole.USER.getRoleName()).doesNotContain("ROLE_");
    }
}

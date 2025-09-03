package dias.heimy.domain.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public String getRoleName() {
        return authority.substring(5);
    }
}

package pl.kurs.entity;

import lombok.AllArgsConstructor;
import lombok.ToString;

import javax.management.relation.RoleNotFoundException;

@AllArgsConstructor
@ToString
public enum Role {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_MODERATOR("ROLE_MODERATOR"),
    ROLE_USER("ROLE_USER"),
    ROLE_GUEST("ROLE_GUEST");

    private final String name;

    public static Role fromString(String roleName) throws RoleNotFoundException {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new RoleNotFoundException("Role name cannot be empty");
        }

        return switch (roleName.trim().toUpperCase()) {
            case "ROLE_ADMIN" -> ROLE_ADMIN;
            case "ROLE_MODERATOR" -> ROLE_MODERATOR;
            case "ROLE_USER" -> ROLE_USER;
            case "ROLE_GUEST" -> ROLE_GUEST;
            default -> throw new RoleNotFoundException("Unknown role name: " + roleName);
        };
    }

}

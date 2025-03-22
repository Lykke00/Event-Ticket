package easv.event.enums;

public enum UserRole {
    ADMIN("Administrator"),
    COORDINATOR("Koordinator");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public static UserRole fromRole(String role) {
        for (UserRole roles : values()) {
            if (roles.getRole().equals(role))
                return roles;
        }
        return null;
    }

    @Override
    public String toString() {
        return role;
    }
}

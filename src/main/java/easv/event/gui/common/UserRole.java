package easv.event.gui.common;

public enum UserRole {
    ADMIN("Admin"),
    COORDINATOR("Koordinator");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return role;
    }
}

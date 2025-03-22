package easv.event.be;

import easv.event.enums.UserRole;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
    private String location;
    private String passwordHash;

    public User(int id, String firstName, String lastName, String email, String role, String location) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = UserRole.fromRole(role);
        this.location = location;
    }

    public User(String firstName, String lastName, String email, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = UserRole.fromRole(role);
    }

    public User(String firstName, String lastName, String email, String location, String role, String passwordHash) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.email = email;
        this.role = UserRole.fromRole(role);
        this.passwordHash = passwordHash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getLocation() {
        return location;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}

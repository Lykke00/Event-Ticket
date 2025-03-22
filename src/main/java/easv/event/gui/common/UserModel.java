package easv.event.gui.common;

import easv.event.be.User;
import easv.event.enums.UserRole;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class UserModel {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty firstName = new SimpleStringProperty();
    private final SimpleStringProperty lastName = new SimpleStringProperty();
    private final SimpleStringProperty location = new SimpleStringProperty();
    private final SimpleStringProperty email = new SimpleStringProperty();
    private final SimpleObjectProperty<UserRole> role = new SimpleObjectProperty<>();

    public UserModel(int id, String firstName, String lastName, String location, String email, UserRole role) {
        this.id.set(id);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.location.set(location);
        this.email.set(email);
        this.role.set(role);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public SimpleStringProperty locationProperty() {
        return location;
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public SimpleObjectProperty<UserRole> roleProperty() {
        return role;
    }

    public static UserModel fromEntity(User user) {
        return new UserModel(user.getId(), user.getFirstName(), user.getLastName(), user.getLocation(), user.getEmail(), user.getRole());
    }

    @Override
    public String toString() {
        return firstName.get() + " " + lastName.get() + " (" + location.get() + ")";
    }
}

package easv.event.gui.common;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.Random;

public class EventItemModel {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();

    private final SimpleObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty time = new SimpleStringProperty();

    private final StringProperty location = new SimpleStringProperty();
    private final IntegerProperty soldTickets = new SimpleIntegerProperty();

    private final ObservableList<UserModel> coordinators = FXCollections.observableArrayList();

    public EventItemModel(int id, String name, String location, int soldTickets, String time, String date) {
        this.id.set(id);
        this.name.set(name);
        this.location.set(location);
        this.soldTickets.set(soldTickets);
        this.time.set(time);

        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date);
        } catch (Exception e) {
            parsedDate = LocalDate.now();
        }

        this.date.set(parsedDate);
        this.description.set(generateRandomString(100));
    }

    public EventItemModel(int id, String name, String location, int soldTickets, String time, String date, ObservableList<UserModel> coordinators) {
        this.id.set(id);
        this.name.set(name);
        this.location.set(location);
        this.soldTickets.set(soldTickets);
        this.time.set(time);

        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date);
        } catch (Exception e) {
            parsedDate = LocalDate.now();
        }

        this.date.set(parsedDate);
        this.description.set(generateRandomString(100));
        this.coordinators.addAll(coordinators);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty locationProperty() {
        return location;
    }

    public IntegerProperty soldTicketsProperty() {
        return soldTickets;
    }

    public StringProperty timeProperty() {
        return time;
    }

    public SimpleObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public ObservableList<UserModel> coordinatorsProperty() {
        return coordinators;
    }

    @Override
    public String toString() {
        return name.get();
    }

    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }

        return result.toString();
    }
}

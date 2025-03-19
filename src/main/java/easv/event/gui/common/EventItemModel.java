package easv.event.gui.common;

import easv.event.be.Event;
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

    public EventItemModel(int id, String name, String description, String location, int soldTickets, String time, LocalDate date) {
        this.id.set(id);
        this.name.set(name);
        this.description.set(description);
        this.location.set(location);
        this.soldTickets.set(soldTickets);
        this.time.set(time);
        this.date.set(date);
    }

    public EventItemModel(String name, String description, String location, String time, LocalDate date) {
        this.name.set(name);
        this.description.set(description);
        this.location.set(location);
        this.time.set(time);
        this.date.set(date);
    }

    public EventItemModel(int id, String name, String description, String location, int soldTickets, String time, LocalDate date, ObservableList<UserModel> coordinators) {
        this.id.set(id);
        this.name.set(name);
        this.description.set(description);
        this.location.set(location);
        this.soldTickets.set(soldTickets);
        this.time.set(time);

        this.date.set(date);
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

    public static EventItemModel fromEntity(Event event) {
        return new EventItemModel(event.getId(), event.getTitle(), event.getDescription(), event.getLocation(), event.getSoldTickets(), event.getStartsAt(), event.getDate());
    }

    public static Event toEntity(EventItemModel eventItemModel) {
        return new Event(eventItemModel.idProperty().get(), eventItemModel.nameProperty().get(), eventItemModel.descriptionProperty().get(), eventItemModel.dateProperty().get(), eventItemModel.timeProperty().get(), eventItemModel.locationProperty().get());
    }

    @Override
    public String toString() {
        return name.get();
    }


}

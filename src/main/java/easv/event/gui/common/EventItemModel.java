package easv.event.gui.common;

import easv.event.be.Event;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class EventItemModel {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();

    private final SimpleObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty time = new SimpleStringProperty();

    private final StringProperty location = new SimpleStringProperty();
    private final IntegerProperty soldTickets = new SimpleIntegerProperty();
    private final BooleanProperty active = new SimpleBooleanProperty();

    private final ObservableList<UserModel> coordinators = FXCollections.observableArrayList();

    public EventItemModel(int id, String name, String description, String location, int soldTickets, String time, LocalDate date, boolean active) {
        this.id.set(id);
        this.name.set(name);
        this.description.set(description);
        this.location.set(location);
        this.soldTickets.set(soldTickets);
        this.time.set(time);
        this.date.set(date);
        this.active.set(active);
    }

    public EventItemModel(String name, String description, String location, String time, LocalDate date, boolean active) {
        this.name.set(name);
        this.description.set(description);
        this.location.set(location);
        this.time.set(time);
        this.date.set(date);
        this.active.set(active);
    }

    public EventItemModel(int id, String name, String description, String location, int soldTickets, String time, LocalDate date, ObservableList<UserModel> coordinators, boolean active) {
        this.id.set(id);
        this.name.set(name);
        this.description.set(description);
        this.location.set(location);
        this.soldTickets.set(soldTickets);
        this.time.set(time);
        this.active.set(active);
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

    public BooleanProperty activeProperty() {
        return active;
    }

    public void updateModel(EventItemModel eventItemModel) {
        this.id.set(eventItemModel.idProperty().get());
        this.name.set(eventItemModel.nameProperty().get());
        this.description.set(eventItemModel.descriptionProperty().get());
        this.location.set(eventItemModel.locationProperty().get());
        this.soldTickets.set(eventItemModel.soldTicketsProperty().get());
        this.time.set(eventItemModel.timeProperty().get());
        this.date.set(eventItemModel.dateProperty().get());
        this.active.set(eventItemModel.activeProperty().get());
    }

    public static EventItemModel fromEntity(Event event) {
        List<UserModel> coordinatorModels = event.getCoordinators().stream()
                .map(UserModel::fromEntity)
                .toList();

        return new EventItemModel(event.getId(), event.getTitle(), event.getDescription(), event.getLocation(), event.getSoldTickets(), event.getStartsAt(), event.getDate(), FXCollections.observableArrayList(coordinatorModels), event.isActive());
    }

    public static Event toEntity(EventItemModel eventItemModel) {
        return new Event(eventItemModel.idProperty().get(), eventItemModel.nameProperty().get(), eventItemModel.descriptionProperty().get(), eventItemModel.dateProperty().get(), eventItemModel.timeProperty().get(), eventItemModel.locationProperty().get(), eventItemModel.activeProperty().get());
    }

    public static EventItemModel copy(EventItemModel eventItemModel) {
        return new EventItemModel(eventItemModel.idProperty().get(), eventItemModel.nameProperty().get(), eventItemModel.descriptionProperty().get(), eventItemModel.locationProperty().get(), eventItemModel.soldTicketsProperty().get(), eventItemModel.timeProperty().get(), eventItemModel.dateProperty().get(), eventItemModel.coordinatorsProperty(), eventItemModel.activeProperty().get());
    }

    @Override
    public String toString() {
        return name.get();
    }


}
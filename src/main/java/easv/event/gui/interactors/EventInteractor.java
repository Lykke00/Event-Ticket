package easv.event.gui.interactors;

import easv.event.be.Event;
import easv.event.be.User;
import easv.event.bll.EventManager;
import easv.event.enums.UserRole;
import easv.event.gui.MainModel;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.common.UserModel;
import easv.event.gui.modals.EventAssign.EventAssignModel;
import easv.event.gui.pages.Event.EventModel;
import easv.event.gui.utils.BackgroundTaskExecutor;
import easv.event.gui.utils.DialogHandler;
import javafx.concurrent.Task;

import java.util.List;

public class EventInteractor {
    private final EventModel eventModel;
    private EventManager eventManager;

    public EventInteractor(AuthInteractor authInteractor) {
        this.eventModel = new EventModel();

        try {
            this.eventManager = new EventManager();
        } catch (Exception e) {
            DialogHandler.showExceptionError("Database fejl", "EventManager kunne ikke oprette forbindelse til databasen", e);
        }
    }

    public void initialize() {
        AuthInteractor authInteractor = MainModel.getInstance().getAuthInteractor();
        UserModel userModel = authInteractor.getAuthModel().userProperty().get();

        setEventsForUser(userModel);
    }

    public void setEventsForUser(UserModel user) {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        boolean isAdmin = user.roleProperty().get().equals(UserRole.ADMIN);

                        return isAdmin ? eventManager.getAllEvents() : eventManager.getEventsForUser(user.idProperty().get());
                    } catch (Exception e) {
                        throw new RuntimeException("En fejl skete ved at prøve og hente Events for bruger", e);
                    }
                },
                eventsForUser -> {
                    List<EventItemModel> eventItemModels = eventsForUser.stream()
                            .map(EventItemModel::fromEntity)
                            .toList();
                    eventModel.eventsListProperty().setAll(eventItemModels);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "EventDAO kunne ikke hente data for bruger", exception);
                }
        );
    }

    public EventItemModel getEventFromId(int id) {
        return eventModel.eventsListProperty().stream()
                .filter(event -> event.idProperty().get() == id)
                .findFirst()
                .orElse(null);
    }

    public EventModel getEventModel() {
        return this.eventModel;
    }

    public void deleteEvent(EventItemModel eventItemModel) {
        BackgroundTaskExecutor.execute(
                () -> { // Hvad skal baggrundstråden køre?
                    try {
                        Event event = EventItemModel.toEntity(eventItemModel);
                        return eventManager.deleteEvent(event);
                    } catch (Exception e) {
                        throw new RuntimeException("En fejl skete ved at prøve at slette Event fra databasen", e);
                    }
                }, // --------------------------------------------------------
                wasDeleted -> { // hvad outputter den tilbage og hvad skal der så ske?
                    if (!wasDeleted)
                        return;

                    eventModel.deleteEvent(eventItemModel);
                },
                exception -> { //hvis nu en fejl sker
                    DialogHandler.showExceptionError("Database fejl", "EventDAO kunne ikke hente data for bruger", exception);
                }
        );
    }

    public void getCoordinatorsForEvent(EventItemModel eventItemModel) {
        BackgroundTaskExecutor.execute(
                () -> { // Hvad skal baggrundstråden køre?
                    try {
                        Event event = EventItemModel.toEntity(eventItemModel);
                        return eventManager.getCoordinatorsForEvent(event);
                    } catch (Exception e) {
                        throw new RuntimeException("En fejl skete ved at prøve at få fat i koordinatore for event", e);
                    }
                }, // --------------------------------------------------------
                coordinators -> { // hvad outputter den tilbage og hvad skal der så ske?
                    List<UserModel> coordinatorModels = coordinators.stream()
                            .map(UserModel::fromEntity)
                            .toList();

                    eventItemModel.coordinatorsProperty().setAll(coordinatorModels);
                },
                exception -> { //hvis nu en fejl sker
                    DialogHandler.showExceptionError("Database fejl", "EventDAO kunne ikke få fat i koordinatore for Event", exception);
                }
        );
    }

    public void createEvent(EventItemModel eventItemModel) {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        Event event = EventItemModel.toEntity(eventItemModel);
                        return eventManager.createEvent(event);
                    } catch (Exception e) {
                        throw new RuntimeException("En fejl skete ved at prøve og oprette et nyt Event", e);
                    }
                },
                createdEvent -> {
                    EventItemModel createdEventItemModel = EventItemModel.fromEntity(createdEvent);
                    eventModel.eventsListProperty().add(createdEventItemModel);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "EventDAO kunne ikke hente data for bruger", exception);
                }
        );
    }

    public void changeCoordinatorsForEvent(EventItemModel eventItemModel, List<UserModel> added, List<UserModel> removed) {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        Event event = EventItemModel.toEntity(eventItemModel);

                        List<User> addedUsers = added.stream()
                                .map(UserModel::toEntity)
                                .toList();

                        List<User> removedUsers = removed.stream()
                                .map(UserModel::toEntity)
                                .toList();

                        return eventManager.changeCoordinators(event, addedUsers, removedUsers);
                    } catch (Exception e) {
                        throw new RuntimeException("En fejl skete ved at prøve og ændre koordinatore for event", e);
                    }
                },
                success -> {
                    if (!success) return;

                    eventItemModel.coordinatorsProperty().setAll(added);
                    eventItemModel.coordinatorsProperty().removeAll(removed);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "EventDAO kunne ikke ændre koordinatore for event", exception);
                }
        );
    }

    public void getCoordinatorsForAssign(EventAssignModel model) {
        BackgroundTaskExecutor.execute(
                () -> { // Hvad skal baggrundstråden køre?
                    try {
                        model.loadingFromDatabaseProperty().set(true);
                        Event event = EventItemModel.toEntity(model.eventItemModel());
                        return eventManager.getCoordinatorsForEvent(event);
                    } catch (Exception e) {
                        throw new RuntimeException("En fejl skete ved at prøve at få fat i koordinatore for event", e);
                    }
                }, // --------------------------------------------------------
                coordinators -> { // hvad outputter den tilbage og hvad skal der så ske?
                    List<UserModel> coordinatorModels = coordinators.stream()
                            .map(UserModel::fromEntity)
                            .toList();

                    model.eventItemModel().coordinatorsProperty().setAll(coordinatorModels);
                    model.loadingFromDatabaseProperty().set(false);
                },
                exception -> { //hvis nu en fejl sker
                    model.loadingFromDatabaseProperty().set(false);
                    DialogHandler.showExceptionError("Database fejl", "EventDAO kunne ikke få fat i koordinatore for Event", exception);
                }
        );
    }
}

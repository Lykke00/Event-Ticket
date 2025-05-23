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
import easv.event.gui.utils.NotificationHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

import java.util.List;
import java.util.function.Consumer;

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

                    eventModel.updateEvents(eventItemModels);
                    //eventModel.eventsListProperty().setAll(eventItemModels);
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

    public void changeEventStatus(EventItemModel eventItemModel, Consumer<Boolean> callback) {
        BackgroundTaskExecutor.execute(
                () -> { // Hvad skal baggrundstråden køre?
                    try {
                        Event event = EventItemModel.toEntity(eventItemModel);
                        return eventManager.changeStatus(event);
                    } catch (Exception e) {
                        throw new RuntimeException("En fejl skete ved at prøve at ændre Event Status i databasen", e);
                    }
                }, // --------------------------------------------------------
                wasChanged -> { // hvad outputter den tilbage og hvad skal der så ske?
                    if (!wasChanged)
                        return;

                    boolean shouldAdd = !eventItemModel.activeProperty().get();
                    if (shouldAdd) {
                        eventItemModel.activeProperty().set(true);
                        NotificationHandler.getInstance().showNotification( "Eventet " + eventItemModel.nameProperty().get() + " er blevet genaktiveret", NotificationHandler.NotificationType.SUCCESS);
                    } else {
                        eventItemModel.activeProperty().set(false);
                        NotificationHandler.getInstance().showNotification( "Eventet " + eventItemModel.nameProperty().get() + " er blevet slettet", NotificationHandler.NotificationType.SUCCESS);
                    }

                    callback.accept(wasChanged);
                },
                exception -> { //hvis nu en fejl sker
                    DialogHandler.showExceptionError("Database fejl", "EventDAO kunne ikke ændre event status", exception);
                    callback.accept(null);
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
                        UserModel loggedInUserModel = MainModel.getInstance().getAuthInteractor().getAuthModel().userProperty().get();
                        Event event = EventItemModel.toEntity(eventItemModel);
                        return eventManager.createEvent(event, UserModel.toEntity(loggedInUserModel));
                    } catch (Exception e) {
                        throw new RuntimeException("En fejl skete ved at prøve og oprette et nyt Event", e);
                    }
                },
                createdEvent -> {
                    EventItemModel createdEventItemModel = EventItemModel.fromEntity(createdEvent);
                    eventModel.addEvent(createdEventItemModel);
                    NotificationHandler.getInstance().showNotification( "Eventet " + createdEventItemModel.nameProperty().get() + " er blevet oprettet", NotificationHandler.NotificationType.SUCCESS);

                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "EventDAO kunne ikke hente data for bruger", exception);
                }
        );
    }
  
    public void editEvent(EventItemModel original, EventItemModel updatedModel) {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        Event event = EventItemModel.toEntity(updatedModel);
                        return eventManager.editEvent(event);
                    } catch (Exception e) {
                        throw new RuntimeException("En fejl skete ved at prøve at redigere event");
                    }
                },
                updated -> {
                    if (updated)
                        original.updateModel(updatedModel);

                    NotificationHandler.getInstance().showNotification( "Eventet " + updatedModel.nameProperty().get() + " er blevet redigeret", NotificationHandler.NotificationType.SUCCESS);

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
                    NotificationHandler.getInstance().showNotification( "Eventet har fået ændret tildelte koordinatore", NotificationHandler.NotificationType.SUCCESS);
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
                },
                exception -> { //hvis nu en fejl sker
                    DialogHandler.showExceptionError("Database fejl", "EventDAO kunne ikke få fat i koordinatore for Event", exception);
                },
                loading -> {
                    model.loadingFromDatabaseProperty().set(loading);
                }
        );
    }
}
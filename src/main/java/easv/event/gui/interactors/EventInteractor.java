package easv.event.gui.interactors;

import easv.event.be.Event;
import easv.event.bll.EventManager;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.common.UserModel;
import easv.event.gui.pages.Event.EventModel;
import easv.event.gui.utils.BackgroundTaskExecutor;
import easv.event.gui.utils.DialogHandler;
import javafx.concurrent.Task;

import java.util.List;

public class EventInteractor {
    private final EventModel eventModel;
    private EventManager eventManager;

    public EventInteractor() {
        this.eventModel = new EventModel();

        try {
            this.eventManager = new EventManager();
        } catch (Exception e) {
            DialogHandler.showExceptionError("Database fejl", "EventManager kunne ikke oprette forbindelse til databasen", e);
        }

        initialize();
    }

    public void initialize() {
        setEventsForUser();
    }

    public void setEventsForUser() {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        return eventManager.getEventsForUser(1); //TODO: Skal være efter logget ind bruger
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
}

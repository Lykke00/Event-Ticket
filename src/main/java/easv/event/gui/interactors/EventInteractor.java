package easv.event.gui.interactors;

import easv.event.be.Event;
import easv.event.bll.EventManager;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.pages.Event.EventModel;
import easv.event.gui.utils.BackgroundTaskExecutor;
import easv.event.gui.utils.DialogHandler;
import javafx.concurrent.Task;

import java.util.List;

public class EventInteractor {
    private final EventModel eventModel;
    private final EventManager eventManager;

    public EventInteractor() throws Exception {
        this.eventModel = new EventModel();
        this.eventManager = new EventManager();

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

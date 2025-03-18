package easv.event.gui.interactors;

import easv.event.bll.EventManager;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.pages.Event.EventModel;

public class EventInteractor {
    private final EventModel eventModel;
    private final EventManager eventManager;

    public EventInteractor() {
        this.eventModel = new EventModel();
        this.eventManager = new EventManager();

        setEventsForUser();
    }

    public void setEventsForUser() {
        //eventModel.eventsListProperty().setAll();
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
}

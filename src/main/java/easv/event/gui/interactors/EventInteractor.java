package easv.event.gui.interactors;

import easv.event.bll.EventManager;
import easv.event.gui.pages.Event.EventModel;

public class EventInteractor {
    private final EventModel eventModel;
    private final EventManager eventManager;

    public EventInteractor() {
        this.eventModel = new EventModel();
        this.eventManager = new EventManager();
    }
}

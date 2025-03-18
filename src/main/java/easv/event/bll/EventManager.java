package easv.event.bll;

import easv.event.dal.EventDAO;

public class EventManager {
    private final EventDAO eventDAO;

    public EventManager() {
        this.eventDAO = new EventDAO();
    }
}

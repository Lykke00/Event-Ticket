package easv.event.bll;

import easv.event.be.Event;
import easv.event.be.User;
import easv.event.dal.dao.EventDAO;

import java.io.IOException;
import java.util.List;

public class EventManager {
    private final EventDAO eventDAO;

    public EventManager() throws Exception {
        try {
            this.eventDAO = new EventDAO();
        } catch (IOException e) {
            throw new Exception("EventManager: Kunne ikke oprette forbindelse til databasen");
        }
    }

    public List<Event> getEventsForUser(User user) throws Exception {
        return this.getEventsForUser(user.getId());
    }

    public List<Event> getEventsForUser(int userId) throws Exception {
        return eventDAO.getEventsForUser(userId);
    }

    public boolean deleteEvent(Event event) throws Exception {
        return eventDAO.deleteEvent(event);
    }

    public List<User> getCoordinatorsForEvent(Event event) throws Exception {
        return eventDAO.getCoordinatorsForEvent(event);
    }
}

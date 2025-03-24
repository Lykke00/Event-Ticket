package easv.event.bll;

import easv.event.be.Event;
import easv.event.be.User;
import easv.event.dal.dao.EventDAO;
import easv.event.dal.dao.IEventDAO;

import java.io.IOException;
import java.util.List;

public class EventManager {
    private final IEventDAO eventDAO;

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

    public Event createEvent(Event event) throws Exception {
        Event createdEvent = eventDAO.createEvent(event);

        if (createdEvent != null) {
            User user = new User(1, "Lykke", "Bernberg", "lykber01@easv.dk", "Koordinator", "Bramming");
            boolean assign = assignCoordinator(event, user);

            if (assign) {
                event.addCoordinator(user);
            }
        }

        return createdEvent;
    }

    public boolean deleteEvent(Event event) throws Exception {
        return eventDAO.deleteEvent(event);
    }

    public List<User> getCoordinatorsForEvent(Event event) throws Exception {
        return eventDAO.getCoordinatorsForEvent(event);
    }

    public boolean assignCoordinators(Event event, List<User> users) throws Exception {
        return true;
    }

    public boolean assignCoordinator(Event event, User user) throws Exception {
        return true;
    }
}

package easv.event.dal.dao;

import easv.event.be.Event;
import easv.event.be.User;

import java.util.List;

public interface IEventDAO {
    List<Event> getEventsForUser(int userId) throws Exception;
    Event createEvent(Event event) throws Exception;
    boolean deleteEvent(Event event) throws Exception;
    List<User> getCoordinatorsForEvent(Event event) throws Exception;
    boolean assignCoordinators(Event event, List<User> users) throws Exception;
    boolean assignCoordinator(Event event, User user) throws Exception;
}

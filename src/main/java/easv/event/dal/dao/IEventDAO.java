package easv.event.dal.dao;

import easv.event.be.Event;
import easv.event.be.User;

import java.util.List;

public interface IEventDAO {
    List<Event> getEventsForUser(int userId) throws Exception;
}

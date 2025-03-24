package easv.event.dal.dao;

import easv.event.be.User;

import java.util.List;

public interface IUserDAO {
    User getUserByEmail(String email) throws Exception;
    boolean updateUser(User user) throws Exception;
    User createUser(User user) throws Exception;

    List<User> getAllCoordinators() throws Exception;
    List<User> getAll() throws Exception;

}

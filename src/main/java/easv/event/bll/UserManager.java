package easv.event.bll;

import easv.event.be.Event;
import easv.event.be.User;
import easv.event.dal.dao.IUserDAO;
import easv.event.dal.dao.UserDAO;
import at.favre.lib.crypto.bcrypt.BCrypt;
import easv.event.enums.UserRole;

import java.io.IOException;
import java.util.List;

public class UserManager {
    private final IUserDAO userDAO;
    private final static int BCRYPT_COST = 12;

    public UserManager() throws IOException {
        this.userDAO = new UserDAO();
    }

    public User authenticateUser(String email, String password) throws Exception {
        User user = userDAO.getUserByEmail(email);
        if (user == null)
            throw new Exception("DOESNT_EXIST");

        boolean okPassword = BCrypt.verifyer().verify(password.toCharArray(), user.getPasswordHash()).verified;
        if (!okPassword)
            throw new Exception("PASSWORD_INCORRECT");

        return user;
    }

    public User registerUser(User user) throws Exception {
        String hashedPassword = BCrypt.withDefaults().hashToString(BCRYPT_COST, user.getPasswordHash().toCharArray());
        user.setPasswordHash(hashedPassword);

        User userExists = userDAO.getUserByEmail(user.getEmail());
        if (userExists != null)
            throw new Exception("USER_EXISTS");

        return userDAO.createUser(user);
    }

    public List<User> getAllCoordinators() throws Exception {
        return userDAO.getAllCoordinators();
    }

    public List<User> getAll() throws Exception {
        return userDAO.getAll();
    }

    public boolean deleteCoordinator(User coordinator) throws Exception {
        // Tjekker om brugeren faktisk er en koordinator
        if (coordinator.getRole() != UserRole.COORDINATOR)
            throw new Exception("Brugeren er ikke en koordinator");

        // Kalder DAL-laget for at slette koordinatoren
        return userDAO.deleteCoordinator(coordinator);
    }

    public boolean editCoordinator(User coordinator) throws Exception {
        return userDAO.editCoordinator(coordinator);
    }

    public List<Event> getEventsByCoordinator(User coordinator) throws Exception {
        return userDAO.getEventsByCoordinator(coordinator);
    }
}



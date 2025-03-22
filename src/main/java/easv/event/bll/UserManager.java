package easv.event.bll;

import easv.event.be.User;
import easv.event.dal.dao.IUserDAO;
import easv.event.dal.dao.UserDAO;
import at.favre.lib.crypto.bcrypt.BCrypt;

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


    public static void main(String[] args) {
        UserManager userManager = null;
        try {
            userManager = new UserManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            User user = userManager.authenticateUser("hej@test.com", "kode123");
            System.out.println(user.getLocation());
            //userManager.registerUser("hej@test.com", "kode123");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}



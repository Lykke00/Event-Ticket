package easv.event.dal.dao;

import easv.event.be.User;
import easv.event.dal.DBConnector;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {
    private final DBConnector dbConnector;

    public UserDAO() throws IOException {
        this.dbConnector = new DBConnector();
    }

    @Override
        public User getUserByEmail(String email) throws Exception {
            String query = """
                SELECT users.id, users.first_name, users.last_name, users.email, users.password, users.location, users_roles.name as role_name 
                FROM users
                JOIN users_roles ON users.role = users_roles.id
                WHERE users.email = ?
                """;

            try (Connection conn = dbConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, email);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String userEmail = rs.getString("email");
                    String passwordHash = rs.getString("password");
                    String location = rs.getString("location");
                    String role = rs.getString("role_name");

                    return new User(id, firstName, lastName, userEmail, location, role, passwordHash);
                }

                return null;
            } catch (Exception e) {
                throw new Exception("Kunne ikke hente bruger med email: " + email, e);
            }
        }

    @Override
    public boolean updateUser(User user) throws Exception {
        String query = """
                UPDATE users 
                SET first_name = ?, last_name = ?, email = ?, password = ?, role = ?, location = ? 
                WHERE id = ?
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());

            // You'll need to get the role ID from role name
            // This is a simplified version - you might need a method to get role ID
            stmt.setInt(5, getRoleIdByName(conn, user.getRole().getRole()));

            stmt.setString(6, user.getLocation());
            stmt.setInt(7, user.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            throw new Exception("Kunne ikke opdatere bruger med id: " + user.getId(), e);
        }
    }

    @Override
    public User createUser(User user) throws Exception {
        String query = """
                INSERT INTO users (first_name, last_name, email, password, role, location) 
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());

            // You'll need to get the role ID from role name
            stmt.setInt(5, getRoleIdByName(conn, user.getRole().getRole()));

            stmt.setString(6, user.getLocation());

            int rowsAffected = stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }

            return user;
        } catch (Exception e) {
            throw new Exception("Kunne ikke oprette bruger i database", e);
        }
    }

    @Override
    public List<User> getAllCoordinators() throws Exception {
        List<User> coordinators = new ArrayList<>();

        String query = """
                    SELECT users.id, users.first_name, users.last_name, users.email, users.password, users.location, users_roles.name as role_name
                    FROM users
                    JOIN users_roles ON users.role = users_roles.id
                    WHERE users_roles.name = 'Koordinator'
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userEmail = resultSet.getString("email");
                String passwordHash = resultSet.getString("password");
                String location = resultSet.getString("location");
                String role = resultSet.getString("role_name");

                User user = new User(id, firstName, lastName, userEmail, location, role, passwordHash);
                coordinators.add(user);
            }

            return coordinators;
        } catch (Exception e) {
            throw new Exception("Kunne ikke få fat i alle koordinatorer i databasen", e);
        }
    }

    @Override
    public List<User> getAll() throws Exception {
        List<User> users = new ArrayList<>();

        String query = """
                    SELECT users.id, users.first_name, users.last_name, users.email, users.password, users.location, users_roles.name as role_name
                    FROM users
                    JOIN users_roles ON users.role = users_roles.id
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userEmail = resultSet.getString("email");
                String passwordHash = resultSet.getString("password");
                String location = resultSet.getString("location");
                String role = resultSet.getString("role_name");

                User user = new User(firstName, lastName, userEmail, location, role, passwordHash);
                users.add(user);
            }

            return users;
        } catch (Exception e) {
            throw new Exception("Kunne ikke få fat i alle brugere i databasen", e);
        }
    }

    private int getRoleIdByName(Connection conn, String roleName) throws SQLException {
        String query = "SELECT id FROM users_roles WHERE name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, roleName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                throw new SQLException("Role not found: " + roleName);
            }
        }
    }
}

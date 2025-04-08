package easv.event.dal.dao;

import easv.event.be.Event;
import easv.event.be.Ticket;
import easv.event.be.User;
import easv.event.dal.DBConnector;
import easv.event.dal.IDBConnector;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {
    private final IDBConnector dbConnector;

    public UserDAO(IDBConnector dbConnector) {
        this.dbConnector = dbConnector;
    }


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
                int id = resultSet.getInt("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String userEmail = resultSet.getString("email");
                String passwordHash = resultSet.getString("password");
                String location = resultSet.getString("location");
                String role = resultSet.getString("role_name");

                User user = new User(id, firstName, lastName, userEmail, location, role, passwordHash);
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
    @Override
    public boolean deleteCoordinator(User coordinator) throws Exception {
        Connection conn = null;
        String fullName = coordinator.getFirstName() + " " + coordinator.getLastName();
        try {
            conn = dbConnector.getConnection();
            conn.setAutoCommit(false);

            String deleteTicketsSQL = "DELETE FROM events_coordinators WHERE user_id = ?";
            try (PreparedStatement ticketStmt = conn.prepareStatement(deleteTicketsSQL)) {
                ticketStmt.setInt(1, coordinator.getId());
                ticketStmt.executeUpdate();
            }

            String deleteTicketTypeSQL = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement typeStmt = conn.prepareStatement(deleteTicketTypeSQL)) {
                typeStmt.setInt(1, coordinator.getId());
                typeStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    throw new Exception("Kunne ikke rulle transaktionen tilbage", rollbackEx);
                }
            }
            throw new Exception("Kunne ikke slette " + fullName + " fra databasen", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    throw new Exception("Kunne ikke lukke databaseforbindelsen", closeEx);
                }
            }
        }
    }

    @Override
    public List<Event> getEventsByCoordinator(User coordinator) throws Exception {
        List<Event> events = new ArrayList<>();

        String query = """
             SELECT events.* FROM events_coordinators
             JOIN events ON events_coordinators.event_id = events.id
             WHERE events_coordinators.user_id = ? AND events.active = 1
            """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, coordinator.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                LocalDate date = rs.getDate("date").toLocalDate();
                String startsAt = rs.getString("starts_at");
                String location = rs.getString("location");
                boolean active = rs.getBoolean("active");

                Event event = new Event(id, title, description, date, startsAt, location, active);
                events.add(event);
            }

            return events;
        } catch (Exception e) {
            throw new Exception("Kunne ikke hente alle events for bruger i databasen", e);
        }
    }

    @Override
    public boolean editCoordinator(User coordinator) throws Exception {
        String query = """
                UPDATE users
                SET first_name = ?,  last_name = ?, email = ?, role = ?, location = ?
                WHERE id = ?
                """;
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, coordinator.getFirstName());
            stmt.setString(2, coordinator.getLastName());
            stmt.setString(3, coordinator.getEmail());
            stmt.setInt(4, coordinator.getRole().getId());
            stmt.setString(5, coordinator.getLocation());
            stmt.setInt(6, coordinator.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new Exception("Kunne ikke redigere koordinatoren i databasen",e);
        }
    }
}


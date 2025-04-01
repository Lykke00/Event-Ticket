package easv.event.dal.dao;

import easv.event.be.Event;
import easv.event.be.User;
import easv.event.dal.DBConnector;
import easv.event.gui.utils.DialogHandler;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventDAO implements IEventDAO {
    private final DBConnector dbConnector;

    public EventDAO() throws IOException {
        this.dbConnector = new DBConnector();
    }

    @Override
    public List<Event> getEventsForUser(int userId) throws Exception {
        List<Event> events = new ArrayList<>();

        // vælg alt fra events
        // tilføj tabellen events_coordinators hvor selve events tabellens id er det samme som events_coordinators.event_id
        // hvor event_coordinators.user_id = et eller andet
        String query = """
                SELECT events.* FROM events
                JOIN events_coordinators ON events.id = events_coordinators.event_id
                WHERE events.active = 1 AND events_coordinators.user_id = ?
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);

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
            throw new Exception("Kunne ikke hente alle Events for bruger. Bruger id: " + userId);
        }
    }

    @Override
    public List<Event> getAllEvents() throws Exception {
        List<Event> events = new ArrayList<>();

        // vælg alt fra events
        // tilføj tabellen events_coordinators hvor selve events tabellens id er det samme som events_coordinators.event_id
        // hvor event_coordinators.user_id = et eller andet
        String query = """
                SELECT * FROM events
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

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
            throw new Exception("Kunne ikke hente alle Events");
        }
    }


    @Override
    public Event createEvent(Event event) throws Exception {
        String query = """
                INSERT INTO events (title, description, date, starts_at, location) 
                        VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3, Date.valueOf(event.getDate()));
            stmt.setString(4, event.getStartsAt());
            stmt.setString(5, event.getLocation());

            int rowsAffected = stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    event.setId(rs.getInt(1));
                }
            }

            return event;
        } catch (Exception e) {
            throw new Exception("Kunne ikke oprette event i database", e);
        }
    }

    @Override
    public boolean deleteEvent(Event event) throws Exception {
        String sql = "UPDATE events SET active = 0 WHERE id = ?";
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, event.getId());
            stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            throw new Exception("Kunne ikke slette Event fra databasen");
        }
    }

    @Override
    public List<User> getCoordinatorsForEvent(Event event) throws Exception {
        List<User> users = new ArrayList<>();

        // vælg alt fra events
        // tilføj tabellen events_coordinators hvor selve events tabellens id er det samme som events_coordinators.event_id
        // hvor event_coordinators.user_id = et eller andet
        String query = """
                SELECT users.id, users.first_name, users.last_name, users.email, users.location, users_roles.name FROM events
                JOIN events_coordinators ON events.id = events_coordinators.event_id
                JOIN users ON events_coordinators.user_id = users.id
                JOIN users_roles ON users.role = users_roles.id
                WHERE events.active = 1 AND events.id = ?
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, event.getId());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                String role = rs.getString("name");
                String location = rs.getString("location");

                User user = new User(id, firstName, lastName, email, role, location);
                users.add(user);
            }

            return users;
        } catch (Exception e) {
            throw new Exception("Kunne ikke hente koordinator for event");
        }
    }

    public boolean changeCoordinators(Event event, List<User> added, List<User> removed) throws Exception {
        String insertQuery = "INSERT INTO events_coordinators (user_id, event_id) VALUES (?, ?)";
        String deleteQuery = "DELETE FROM events_coordinators WHERE user_id = ? AND event_id = ?";

        Connection conn = null;

        try {
            conn = dbConnector.getConnection();
            // start transaktion
            conn.setAutoCommit(false);

            // fjern koordinatore
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                alterCoordinatorBatch(deleteStmt, event, removed); // batch eksikvering
            }

            // indsæt nye koordinator
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                alterCoordinatorBatch(insertStmt, event, added); // batch eksikvering
            }

            // commit hvis transaktion er OK
            conn.commit();
            return true;
        } catch (SQLException e) {
            // rul tilbage hvis transaktion fejler
            if (conn != null) {
                try {
                    conn.rollback(); // kald rollback
                } catch (Exception ex) {
                    throw new Exception("Kunne ikke rulle ændringer tilbage i databasen");
                }
            }
            throw new Exception("Kunne ikke ændre koordiator for event", e);
        } finally {
            // når det ikke er en try with resources, skal vi selv manuelt lukke forbindelse
            // til databasen
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (Exception e) {
                throw new Exception("Kunne ikke lukke forbindelse til databasen", e);
            }
        }
    }



    @Override
    public boolean assignCoordinators(Event event, List<User> users) throws Exception {
        String query =
                "INSERT INTO events_coordinators (user_id, event_id) VALUES (?, ?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // kør batch i en transkation
            conn.setAutoCommit(false);

            boolean result = alterCoordinatorBatch(stmt, event, users);

            // kør alle vores batch op til DB
            conn.commit();

            return result;
        } catch (SQLException e) {
            throw new Exception("Kunne ikke tilføje koordinator til event", e);
        }
    }

    @Override
    public boolean assignCoordinator(Event event, User user) throws Exception {
        String query =
                "INSERT INTO events_coordinators (user_id, event_id) VALUES (?, ?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, user.getId());
            stmt.setInt(2, event.getId());

            int rowsAffected = stmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            throw new Exception("Kunne ikke tilføje koordinator til event", e);
        }
    }

    @Override
    public boolean removedAssignedCoordinators(Event event, List<User> users) throws Exception {
        String query =
                "DELETE FROM events_coordinators WHERE user_id = ? AND event_id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // kør batch i en transkation
            conn.setAutoCommit(false);

            boolean result = alterCoordinatorBatch(stmt, event, users);

            // kør alle vores batch op til DB
            conn.commit();

            return result;
        } catch (SQLException e) {
            throw new Exception("Kunne ikke fjerne koordinatore fra event", e);
        }
    }

    /**
     *
     * @param stmt the prepared stmt either insert or delete
     * @param event affected Event
     * @param users affected users
     * @return result from affected Batch
     * @throws SQLException
     */
    private boolean alterCoordinatorBatch(PreparedStatement stmt, Event event, List<User> users) throws SQLException {
        // sørg for at først ? er user id og 2 er event id
        for (User user : users) {
            stmt.setInt(1, user.getId());
            stmt.setInt(2, event.getId());
            stmt.addBatch();
        }

        int[] result = stmt.executeBatch();

        for (int res : result) {
            if (res > 0)
                return true;
        }
        return false;
    }
}

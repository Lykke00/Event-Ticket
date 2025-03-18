package easv.event.dal.dao;

import easv.event.be.Event;
import easv.event.be.User;
import easv.event.dal.DBConnector;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
                WHERE events_coordinators.user_id = ?
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

                Event event = new Event(id, title, description, date, startsAt, location);
                events.add(event);
            }

            return events;
        } catch (Exception e) {
            throw new Exception("Kunne ikke hente alle Events for bruger. Bruger id: " + userId);
        }
    }
}

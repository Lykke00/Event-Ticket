
package easv.event.dal.dao;

import easv.event.be.Event;
import easv.event.be.Ticket;
import easv.event.be.TicketEvent;
import easv.event.be.TicketType;
import easv.event.dal.DBConnector;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TicketDAO implements ITicketDAO {
    private final DBConnector dbConnector;

    public TicketDAO() throws IOException {
        this.dbConnector = new DBConnector();
    }

    @Override
    public Ticket createTicket(Ticket ticket) throws Exception {
        String query = """
                INSERT INTO tickets (name, type) 
                VALUES (?, ?)
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, ticket.getName());
            stmt.setInt(2, ticket.getTicketType().getId());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ticket.setId(rs.getInt(1));
                }
            }

            return ticket;
        } catch (Exception e) {
            throw new Exception("Kunne ikke oprette ticket i database", e);
        }
    }

    @Override
    public List<Ticket> getAllTickets() throws Exception {
        List<Ticket> tickets = new ArrayList<>();

        String query = """
                SELECT tickets.*, ticket_types.type AS type_name, ticket_types.id AS type_id FROM tickets
                JOIN ticket_types ON tickets.type = ticket_types.id
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String ticketName = resultSet.getString("name");
                int ticketTypeId = resultSet.getInt("type_id");
                String ticketTypename = resultSet.getString("type_name");

                TicketType ticketType = new TicketType(ticketTypeId, ticketTypename);
                Ticket ticket = new Ticket(id, ticketName, ticketType);
                tickets.add(ticket);
            }

            return tickets;
        } catch (Exception e) {
            throw new Exception("Kunne ikke hente alle billetter fra databasen", e);
        }
    }

    @Override
    public List<TicketType> getAllTicketTypes() throws Exception {
        List<TicketType> ticketTypes = new ArrayList<>();

        String query = """
                    SELECT * FROM ticket_types
                    """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String type = resultSet.getString("type");

                TicketType ticketType = new TicketType(id, type);
                ticketTypes.add(ticketType);
            }

            return ticketTypes;
        } catch (Exception e) {
            throw new Exception("Kunne ikke hente alle bilet typer fra databasen", e);
        }
    }

    @Override
    public TicketType createTicketType(TicketType ticketType) throws Exception {
        String query = """
                INSERT INTO ticket_types (type) 
                VALUES (?)
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, ticketType.getName());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    ticketType.setId(rs.getInt(1));
                }
            }

            return ticketType;
        } catch (Exception e) {
            throw new Exception("Kunne ikke oprette billet type i databasen", e);
        }
    }

    @Override
    public boolean deleteTicketType(TicketType ticketType, List<Ticket> tickets) throws Exception {
        Connection conn = null;
        try {
            conn = dbConnector.getConnection();
            conn.setAutoCommit(false);

            String deleteTicketsSQL = "DELETE FROM tickets WHERE id = ?";
            try (PreparedStatement ticketStmt = conn.prepareStatement(deleteTicketsSQL)) {
                for (Ticket ticket : tickets) {
                    ticketStmt.setInt(1, ticket.getId());
                    ticketStmt.addBatch();
                }
                ticketStmt.executeBatch();
            }

            String deleteTicketTypeSQL = "DELETE FROM ticket_types WHERE id = ?";
            try (PreparedStatement typeStmt = conn.prepareStatement(deleteTicketTypeSQL)) {
                typeStmt.setInt(1, ticketType.getId());
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
            throw new Exception("Kunne ikke slette billet type fra databasen", e);
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
    public boolean doesTicketNameExist(String name) throws Exception {
        String query = """
                SELECT * FROM tickets WHERE name = ?
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.executeQuery();

            try (ResultSet rs = stmt.getResultSet()) {
                return rs.next();
            }
        } catch (Exception e) {
            throw new Exception("Kunne ikke tjekke om billet navn eksisterer", e);
        }
    }

    @Override
    public boolean doesTicketTypeExist(String name) throws Exception {
        String query = """
                SELECT * FROM ticket_types WHERE type = ?
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.executeQuery();

            try (ResultSet rs = stmt.getResultSet()) {
                return rs.next();
            }
        } catch (Exception e) {
            throw new Exception("Kunne ikke tjekke om billet type eksisterer", e);
        }
    }

    @Override
    public List<Ticket> getTicketsForType(TicketType type) throws Exception {
        List<Ticket> tickets = new ArrayList<>();

        String query = """
                SELECT tickets.*, ticket_types.type AS type_name, ticket_types.id AS type_id FROM tickets
                JOIN ticket_types ON tickets.type = ticket_types.id
                WHERE tickets.type = ?
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, type.getId());
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String ticketName = resultSet.getString("name");
                int ticketTypeId = resultSet.getInt("type_id");
                String ticketTypename = resultSet.getString("type_name");

                TicketType ticketType = new TicketType(ticketTypeId, ticketTypename);
                Ticket ticket = new Ticket(id, ticketName, ticketType);
                tickets.add(ticket);
            }

            return tickets;
        } catch (Exception e) {
            throw new Exception("Kunne ikke hente alle billetter for billet type i databasen", e);
        }
    }

    @Override
    public boolean editTicketType(TicketType ticketType) throws Exception {
        String sql = "UPDATE ticket_types SET type = ? WHERE id = ?";
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ticketType.getName());
            stmt.setInt(2, ticketType.getId());
            stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            throw new Exception("Kunne ikke ændre bilet typen i databasen");
        }
    }

    @Override
    public boolean editTicket(Ticket ticket) throws Exception {
        String sql = "UPDATE tickets SET name = ?, type = ? WHERE id = ?";
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ticket.getName());
            stmt.setInt(2, ticket.getTicketType().getId());
            stmt.setInt(3, ticket.getId());

            stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            throw new Exception("Kunne ikke redigere biletten i databasen");
        }
    }

    @Override
    public List<Event> getEventsByTicket(Ticket ticket) throws Exception {
        List<Event> events = new ArrayList<>();

        String query = """
                 SELECT events.* FROM ticket_events
                 JOIN events ON ticket_events.event_id = events.id
                 WHERE ticket_events.ticket_id = ? AND events.active = 1
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, ticket.getId());
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
            throw new Exception("Kunne ikke hente alle events for billet i databasen", e);
        }
    }

    @Override
    public boolean deleteTicket(Ticket ticket) throws Exception {
        Connection conn = null;
        try {
            conn = dbConnector.getConnection();
            conn.setAutoCommit(false);

            String deleteTicketsSQL = "DELETE FROM ticket_events WHERE ticket_id = ?";
            try (PreparedStatement ticketStmt = conn.prepareStatement(deleteTicketsSQL)) {
                ticketStmt.setInt(1, ticket.getId());
                ticketStmt.executeUpdate();
            }

            String deleteTicketTypeSQL = "DELETE FROM tickets WHERE id = ?";
            try (PreparedStatement typeStmt = conn.prepareStatement(deleteTicketTypeSQL)) {
                typeStmt.setInt(1, ticket.getId());
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
            throw new Exception("Kunne ikke slette billetten fra databasen", e);
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
    public List<TicketEvent> addTicketToEvent(TicketEvent ticket, List<Event> eventsToAdd, List<Event> eventsToRemove) throws Exception {
        List<TicketEvent> updatedTickets = new ArrayList<>();
        Connection conn = null;

        try {
            conn = dbConnector.getConnection();
            conn.setAutoCommit(false);

            String deleteSql = "DELETE FROM ticket_events WHERE ticket_id = ? AND event_id = ?";
            String insertSql = "INSERT INTO ticket_events (ticket_id, event_id, price) OUTPUT INSERTED.id VALUES (?, ?, ?)";

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                 PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

                if (!eventsToRemove.isEmpty()) {
                    for (Event event : eventsToRemove) {
                        deleteStmt.setInt(1, ticket.getTicket().getId());
                        deleteStmt.setInt(2, event.getId());
                        deleteStmt.addBatch();
                    }

                    deleteStmt.executeBatch();
                }

                if (!eventsToAdd.isEmpty()) {
                    for (Event event : eventsToAdd) {
                        insertStmt.setInt(1, ticket.getTicket().getId());
                        insertStmt.setInt(2, event.getId());
                        insertStmt.setDouble(3, ticket.getPrice());

                        try (ResultSet rs = insertStmt.executeQuery()) {
                            if (rs.next()) {
                                int id = rs.getInt(1);
                                TicketEvent newTicket = new TicketEvent(id, ticket, event);
                                updatedTickets.add(newTicket);
                            }
                        }
                    }
                }

                conn.commit();
                return updatedTickets;

            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException rollbackEx) {
                        throw new Exception("Kunne ikke rulle transaktionen tilbage", rollbackEx);
                    }
                }
                throw new Exception("Kunne ikke indsætte eller slette billetter i databasen", e);
            }
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
    public List<TicketEvent> getTicketEventByTicket(Ticket ticket) throws Exception {
        List<TicketEvent> tickets = new ArrayList<>();

        String query = """
                SELECT events.*, tickets.id AS ticket_id, tickets.name AS ticket_name, tickets.type AS ticket_type, ticket_types.type AS tickettype_name, ticket_events.id AS ticketevent_id, ticket_events.price AS ticketevent_price FROM ticket_events
                JOIN events ON ticket_events.event_id = events.id
                JOIN tickets ON ticket_events.ticket_id = tickets.id
                JOIN ticket_types ON tickets.type = ticket_types.id
                WHERE ticket_events.ticket_id = ?
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, ticket.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                LocalDate date = rs.getDate("date").toLocalDate();
                String startsAt = rs.getString("starts_at");
                String location = rs.getString("location");
                boolean active = rs.getBoolean("active");

                int ticketeventId = rs.getInt("ticketevent_id");
                double ticketeventPrice = rs.getDouble("ticketevent_price");

                int ticketId = rs.getInt("ticket_id");
                String ticketName = rs.getString("ticket_name");
                int ticketTypeId = rs.getInt("ticket_type");

                String ticketTypename = rs.getString("tickettype_name");
                TicketType ticketType = new TicketType(ticketTypeId, ticketTypename);

                Ticket ticketObj = new Ticket(ticketId, ticketName, ticketType);

                Event event = new Event(id, title, description, date, startsAt, location, active);
                TicketEvent ticketEvent = new TicketEvent(ticketeventId, event, ticketObj, ticketeventPrice);
                tickets.add(ticketEvent);
            }

            return tickets;
        } catch (Exception e) {
            throw new Exception("Kunne ikke hente alle events for billet i databasen", e);
        }
    }

    @Override
    public boolean editTicketEvent(TicketEvent entity) throws Exception {
        String sql = "UPDATE ticket_events SET price = ? WHERE ticket_id = ? AND event_id = ?";
        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, entity.getPrice());
            stmt.setInt(2, entity.getTicket().getId());
            stmt.setInt(3, entity.getEvent().getId());

            stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            throw new Exception("Kunne ikke redigere event billet i databasen");
        }
    }

    @Override
    public boolean removeTicketEvent(TicketEvent entity) throws Exception {
        String sql = "DELETE FROM ticket_events WHERE ticket_id = ? AND event_id = ?";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, entity.getTicket().getId());
            stmt.setInt(2, entity.getEvent().getId());

            stmt.executeUpdate();

            return true;
        } catch (Exception e) {
            throw new Exception("Kunne ikke slette event billet i databasen");
        }
    }

    @Override
    public List<TicketEvent> getTicketEventByEvent(Event event) throws Exception {
        List<TicketEvent> tickets = new ArrayList<>();

        String query = """
                SELECT events.*, tickets.id AS ticket_id, tickets.name AS ticket_name, tickets.type AS ticket_type, ticket_types.type AS tickettype_name, ticket_events.id AS ticketevent_id, ticket_events.price AS ticketevent_price FROM ticket_events
                JOIN events ON ticket_events.event_id = events.id
                JOIN tickets ON ticket_events.ticket_id = tickets.id
                JOIN ticket_types ON tickets.type = ticket_types.id
                WHERE ticket_events.event_id = ?
                """;

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, event.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                LocalDate date = rs.getDate("date").toLocalDate();
                String startsAt = rs.getString("starts_at");
                String location = rs.getString("location");
                boolean active = rs.getBoolean("active");

                int ticketeventId = rs.getInt("ticketevent_id");
                double ticketeventPrice = rs.getDouble("ticketevent_price");

                int ticketId = rs.getInt("ticket_id");
                String ticketName = rs.getString("ticket_name");
                int ticketTypeId = rs.getInt("ticket_type");

                String ticketTypename = rs.getString("tickettype_name");
                TicketType ticketType = new TicketType(ticketTypeId, ticketTypename);

                Ticket ticketObj = new Ticket(ticketId, ticketName, ticketType);

                Event eventObj = new Event(id, title, description, date, startsAt, location, active);
                TicketEvent ticketEvent = new TicketEvent(ticketeventId, eventObj, ticketObj, ticketeventPrice);
                tickets.add(ticketEvent);
            }
            return tickets;
        } catch (Exception e) {
            throw new Exception("Kunne ikke hente alle events for billet i databasen", e);
        }
    }

    @Override
    public boolean sellTicket(TicketEvent ticket, UUID uuid, int amount, String email) throws Exception {
        String sql = "INSERT INTO tickets_bought (ticket_event_id, customer_email, amount, total_price, uuid) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticket.getId());
            stmt.setString(2, email);
            stmt.setInt(3, amount);
            stmt.setDouble(4, ticket.getPrice() * amount);
            stmt.setString(5, uuid.toString());

            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            throw new Exception("Kunne ikke sælge billetten", e);
        }
    }
}
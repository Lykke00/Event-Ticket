
package easv.event.dal.dao;

import easv.event.be.Ticket;
import easv.event.be.TicketType;
import easv.event.dal.DBConnector;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
}

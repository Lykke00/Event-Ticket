package easv.event.bll;

import easv.event.be.Ticket;
import easv.event.be.TicketType;
import easv.event.dal.IDBConnector;
import easv.event.dal.TestDBConnector;
import easv.event.dal.dao.TicketDAO;
import easv.event.dal.dao.UserDAO;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.junit.jupiter.Container;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TicketManagerTest {
    @Container
    public static MSSQLServerContainer<?> sqlServer = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
            .acceptLicense()
            .withPassword("StrongPassword123!")
            .withInitScript("database.sql")
            .withConnectTimeoutSeconds(120);

    private IDBConnector dbConnector;
    private TicketDAO ticketDAO;
    private TicketManager ticketManager;

    @BeforeAll
    public static void setUpClass() {
        if (!sqlServer.isRunning())
            sqlServer.start();

        // Indlæs testdata efter skemaet er oprettet
        try (Connection conn = DriverManager.getConnection(
                sqlServer.getJdbcUrl(),
                sqlServer.getUsername(),
                sqlServer.getPassword())) {

            // Læs testdata SQL-filen
            String testDataSql;
            try (InputStream is = TicketManagerTest.class.getClassLoader().getResourceAsStream("testdata.sql")) {
                if (is == null) {
                    throw new IOException("Kunne ikke finde testdata.sql i classpath");
                }
                testDataSql = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException("Fejl ved indlæsning af testdata-fil: " + e.getMessage(), e);
            }

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(testDataSql);
            } catch (SQLException e) {
                throw new RuntimeException("Fejl ved udførelse af testdata-SQL: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Kunne ikke oprette forbindelse til test-databasen: " + e.getMessage(), e);
        }
    }

    @BeforeEach
    public void setUp() {
        dbConnector = new TestDBConnector(sqlServer);
        ticketDAO = new TicketDAO(dbConnector);
        ticketManager = new TicketManager(ticketDAO);
    }

    @Test
    @Order(1)
    public void createTicketTypeSuccess() {
        assertDoesNotThrow(() -> {
            TicketType type = new TicketType("EASV Promoter");

            TicketType created = ticketManager.createTicketType(type);

            assertNotNull(created);
            assertTrue(created.getId() > 0);
        }, "Burde ikke kaste exception");
    }

    @Test
    @Order(2)
    public void getAllTicketsSuccess() {
        assertDoesNotThrow(() -> {
            List<Ticket> tickets = ticketManager.getAllTickets();

            assertNotNull(tickets);
            assertFalse(tickets.isEmpty());
        }, "Burde ikke kaste exception");
    }

    @Test
    @Order(3)
    public void ticketTypeExistSuccess() {
        assertDoesNotThrow(() -> {
            boolean exists = ticketManager.doesTicketTypeExist("EASV Promoter");

            assertTrue(exists);
        },  "Burde ikke kaste exception");
    }
}

package easv.event.bll;

import easv.event.be.User;
import easv.event.dal.IDBConnector;
import easv.event.dal.TestDBConnector;
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
public class UserManagerTest {

    @Container
    public static MSSQLServerContainer<?> sqlServer = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2022-latest")
            .acceptLicense()
            .withPassword("StrongPassword123!")
            .withInitScript("database.sql")
            .withConnectTimeoutSeconds(120);

    private IDBConnector dbConnector;
    private UserDAO userDAO;
    private UserManager userManager;

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
            try (InputStream is = UserManagerTest.class.getClassLoader().getResourceAsStream("testdata.sql")) {
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
        userDAO = new UserDAO(dbConnector);
        userManager = new UserManager(userDAO);
    }

    @Test
    public void getAllUsersSuccess() {
        assertDoesNotThrow(() -> {
            List<User> users = userManager.getAll();

            assertNotNull(users);
            assertFalse(users.isEmpty());
        }, "Metode skal ikke kaste en exception");
    }

    @Test
    @Order(1)
    public void userRegistrationSuccess() {
        assertDoesNotThrow(() -> {
            User user = new User("Kasper", "Nielsen", "kasnie01@easv365.dk", "Esbjerg", "Koordinator", "adgangskode1");
            User registered = userManager.registerUser(user);

            assertNotNull(registered);
            assertTrue(registered.getId() > 0);
            assertEquals(user.getEmail(), registered.getEmail());
        }, "Metode skal ikke kaste en exception");
    }

    @Test
    @Order(2)
    public void userRegistrationMailExists() {
        //bliver registreret fra testdata.sql
        User user = new User("admin", "admin", "admin@easv.dk", "Esbjerg", "Koordinator", "adgangskode1");

        Exception exception = assertThrows(Exception.class, () -> {
            userManager.registerUser(user);
        }, "Brugeren skal ikke kunne registreres, da mailen eksisterer");

        assertEquals("USER_EXISTS",  exception.getMessage());
    }

    @Test
    @Order(3)
    public void userSuccessfulLogin() {
        String email = "kasnie01@easv365.dk";
        String password = "adgangskode1";

        assertDoesNotThrow(() -> {
            User loggedIn = userManager.authenticateUser(email, password);

            assertNotNull(loggedIn);
            assertEquals(email, loggedIn.getEmail());
        }, "Brugeren kunne ikke logges på");
    }

    @Test
    @Order(4)
    public void userLoginWrongPassword() {
        String email = "kasnie01@easv365.dk";
        String password = "forkertPW";

        Exception exception = assertThrows(Exception.class, () -> {
            userManager.authenticateUser(email,password);
        }, "Brugeren skal ikke kunne registreres, da mailen eksisterer");

        assertEquals("PASSWORD_INCORRECT",  exception.getMessage());
    }

    @Test
    @Order(5)
    public void userLoginWrongEmail() {
        String email = "forkert@mail.dk";
        String password = "Kode123";

        Exception exception = assertThrows(Exception.class, () -> {
            userManager.authenticateUser(email,password);
        });
        assertEquals("DOESNT_EXIST",  exception.getMessage());
    }
}

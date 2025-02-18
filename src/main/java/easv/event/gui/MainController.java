package easv.event.gui;

import easv.event.gui.utils.PageHandler;
import easv.event.gui.utils.Pages;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private final PageHandler pageHandler = PageHandler.getInstance();

    private final static String CSS_ACTIVE_TAB = "tab-active";
    private final static String CSS_INACTIVE_TAB = "tab-inactive";

    @FXML
    private BorderPane mainWindow;

    @FXML
    private Button btnEventsPage, btnTicketPage, btnUsersPage, btnLogOut;

    private final ArrayList<Button> headerButtons = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        headerButtons.addAll(Arrays.asList(btnEventsPage, btnTicketPage, btnUsersPage, btnLogOut));
        updateActiveBtn(btnEventsPage);

        pageHandler.setBorderPane(mainWindow);

        setupTabBtns();
    }

    /* Lambda udtryk til at register tryk på de forskellige knapper */
    private void setupTabBtns() {
        btnEventsPage.setOnAction(event -> {
            pageHandler.setCurrentPage(Pages.EVENT);
            updateActiveBtn(btnEventsPage);
        });

        btnTicketPage.setOnAction(event -> {
            pageHandler.setCurrentPage(Pages.TICKETS);
            updateActiveBtn(btnTicketPage);
        });

        btnUsersPage.setOnAction(event -> {
            pageHandler.setCurrentPage(Pages.USERS);
            updateActiveBtn(btnUsersPage);
        });

        btnLogOut.setOnAction(event -> {
            System.exit(0);
        });
    }

    private void updateActiveBtn(Button activeButton) {
        for (Button button : headerButtons) {
            button.getStyleClass().remove(CSS_ACTIVE_TAB);
            button.getStyleClass().remove(CSS_INACTIVE_TAB);
        }

        activeButton.getStyleClass().add(CSS_ACTIVE_TAB);
    }
}

package easv.event.gui;

import easv.event.enums.UserRole;
import easv.event.gui.common.AuthModel;
import easv.event.gui.common.UserModel;
import easv.event.gui.utils.DialogHandler;
import easv.event.gui.utils.PageData;
import easv.event.gui.utils.PageHandler;
import easv.event.gui.pages.Pages;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private final AuthModel authModel = MainModel.getInstance().getAuthInteractor().getAuthModel();

    private final PageHandler pageHandler = PageHandler.getInstance();

    private final static String CSS_ACTIVE_TAB = "tab-active";
    private final static String CSS_INACTIVE_TAB = "tab-inactive";
    private final static String TITLE = "Event System";

    @FXML
    private HBox hBoxTabs;

    @FXML
    private BorderPane mainWindow;

    @FXML
    private Button btnEventsPage, btnTicketPage, btnUsersPage, btnLogOut;

    @FXML
    private HBox hBoxLogo;

    private final ArrayList<Button> headerButtons = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        headerButtons.addAll(Arrays.asList(btnEventsPage, btnTicketPage, btnUsersPage, btnLogOut));
        updateActiveBtn(btnEventsPage);

        pageHandler.setBorderPane(mainWindow);
        setupTabBtns();

        logoClickReturnHome();

        //TODO: User permission, tilføj tilbage når alt er lavet
        userPermissionView();

        btnUsersPage.disableProperty().bind(
                authModel.userProperty().flatMap(user ->
                        user.roleProperty().isEqualTo(UserRole.COORDINATOR)
                )
        );
    }

    // fjern bruger tabben hvis permissions ikke er korrekt
    private void userPermissionView() {

        authModel.userProperty().addListener(new ChangeListener<UserModel>() {
            @Override
            public void changed(ObservableValue<? extends UserModel> observable, UserModel oldValue, UserModel newValue) {
                if (newValue != null) {
                    boolean isCoordinator = authModel.userProperty().get().roleProperty().get().equals(UserRole.COORDINATOR);
                    //if (isCoordinator && hBoxTabs.getChildren().)
                    btnUsersPage.visibleProperty().bind(
                            authModel.userProperty().flatMap(user ->
                                    user.roleProperty().isEqualTo(UserRole.COORDINATOR).not()
                            )
                    );
                        //hBoxTabs.getChildren().(btnUsersPage);
                }
            }
        });

    }

    private void logoClickReturnHome() {
        hBoxLogo.getStyleClass().add("header-logo-click");
        hBoxLogo.setOnMouseClicked(event -> {
            PageHandler.getInstance().setCurrentPage(Pages.EVENT);
            updateActiveBtn(btnEventsPage);
        });
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

        btnLogOut.setOnAction(event -> DialogHandler.showConfirmationDialog(
                "Er du sikker på du vil logge ud?",
                "Vil du logge ud af programmet?",
                "Hvis du logger ud, skal du logge ind i programmet igen.",
                () -> {
                    logOut();
                })
        );
    }

    private void updateActiveBtn(Button activeButton) {
        for (Button button : headerButtons) {
            button.getStyleClass().remove(CSS_ACTIVE_TAB);
            button.getStyleClass().remove(CSS_INACTIVE_TAB);
        }

        activeButton.getStyleClass().add(CSS_ACTIVE_TAB);
    }

    private void logOut() {
        MainModel.getInstance().getAuthInteractor().getAuthModel().logout();
        Stage currentStage = (Stage) mainWindow.getScene().getWindow();

        try {
            PageData loginPage = PageHandler.getInstance().getStoredPage(Pages.LOGIN);

            currentStage.setScene(loginPage.getPageScene());
            currentStage.setTitle(TITLE);
            currentStage.centerOnScreen();
        } catch (Exception e) {
            DialogHandler.showExceptionError("Logud Fejl", "Kunne ikke returnere login side", e);
        }
    }

}

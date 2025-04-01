package easv.event.gui.pages.Login;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.PasswordTextField;
import atlantafx.base.theme.Styles;
import easv.event.gui.MainModel;
import easv.event.gui.common.AuthModel;
import easv.event.gui.interactors.AuthInteractor;
import easv.event.gui.pages.Pages;
import easv.event.gui.utils.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private final AuthInteractor authInteractor = MainModel.getInstance().getAuthInteractor();
    private final AuthModel authModel = authInteractor.getAuthModel();

    private final static int WIDTH = 1200;
    private final static int HEIGHT = 800;
    private final static String TITLE = "Event System";

    private boolean loaded = false;

    @FXML
    private CustomTextField txtFieldEmail;

    @FXML
    private PasswordTextField txtFieldPassword;

    @FXML
    private Button btnContinue;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validate();
        loginListener();
        txtFieldListener();
        setupEnterKeyHandler();
    }

    private void setupEnterKeyHandler() {
        txtFieldEmail.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                if (!txtFieldPassword.getText().isEmpty()) {
                    btnContinue.fire();
                } else {
                    txtFieldPassword.requestFocus();
                }
            }
        });

        txtFieldPassword.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                if (!btnContinue.isDisabled()) {
                    btnContinue.fire();
                }
            }
        });
    }

    private void validate() {
        BooleanBinding notValidFields = txtFieldEmail.textProperty().isEmpty()
                .or(authModel.databaseLoadProperty())
                .or(txtFieldPassword.textProperty().isEmpty())
                .or(Bindings.createBooleanBinding(() -> !isValidEmail(txtFieldEmail.getText()), txtFieldEmail.textProperty()));

        btnContinue.disableProperty().bind(notValidFields);
        btnContinue.textProperty().bind(
                Bindings.when(authModel.databaseLoadProperty())
                        .then("Indlæser...")
                        .otherwise("Fortsæt")
        );
    }

    private void txtFieldListener() {
        authModel.loginFailedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                txtFieldEmail.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                txtFieldPassword.pseudoClassStateChanged(Styles.STATE_DANGER, true);
            }
        });

        txtFieldEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty())
                txtFieldEmail.pseudoClassStateChanged(Styles.STATE_DANGER, false);
        });

        txtFieldPassword.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty())
                txtFieldPassword.pseudoClassStateChanged(Styles.STATE_DANGER, false);
        });
    }

    private void loginListener() {
        authModel.loggedInProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    loadApplication();

                    txtFieldEmail.clear();
                    txtFieldPassword.clear();
                }
            }
        });
    }

    private void loadApplication() {
        try {
            Stage stage = (Stage) btnContinue.getScene().getWindow();
            PageData pageData = PageHandler.getInstance().storeCurrentPage(Pages.MAIN);

            if (loaded) {
                setStageCenter(stage);
                stage.setScene(pageData.getPageScene());
                return;
            }

            // FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = pageData.getPageParent();

            // StackPane bruges grundet ModalPane, så vi kan vise
            // modals samt evt. notifikationer i fremtiden
            StackPane stackPane = new StackPane(root);

            // Tilføj CSS til root (stackpane)
            String css = getClass().getResource("/css/main.css").toExternalForm();
            stackPane.getStylesheets().add(css);

            // Send vores StackPane videre til MainController
            // så vi kan håndtere modals
            ModalHandler.getInstance().setRoot(stackPane);

            // sæt stackpane til notification
            NotificationHandler.initialize(stackPane);

            Scene scene = new Scene(stackPane, WIDTH, HEIGHT);
            pageData.setScene(scene);

            stage.setScene(pageData.getPageScene());
            stage.setTitle(TITLE);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/favicon.png")));

            setStageCenter(stage);

            stage.show();
            loaded = true;
        } catch (Exception e) {
            DialogHandler.showExceptionError("Hovsa, fejl!", "Der er desværre sket en fejl, kopier stacktrace og send det til en administrator.", e);
        }
    }

    private void setStageCenter(Stage stage) {
        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
        stage.setX((screenWidth - WIDTH) / 2);
        stage.setY((screenHeight - HEIGHT) / 2);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    @FXML
    private void btnActionContinue(ActionEvent actionEvent) {
        String email = txtFieldEmail.getText();
        String password = txtFieldPassword.getPassword();

        authInteractor.logIn(email, password);
    }
}
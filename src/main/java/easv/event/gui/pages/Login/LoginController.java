package easv.event.gui.pages.Login;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.PasswordTextField;
import easv.event.gui.utils.DialogHandler;
import easv.event.gui.utils.ModalHandler;
import easv.event.gui.utils.NotificationHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class LoginController {
    private final static int WIDTH = 800;
    private final static int HEIGHT = 600;
    private final static String TITLE = "Event System";

    @FXML
    private CustomTextField txtFieldEmail;

    @FXML
    private PasswordTextField txtFieldPassword;

    @FXML
    private Button btnContinue;

    @FXML
    private void btnActionContinue(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnContinue.getScene().getWindow();

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

            stage.setScene(new Scene(stackPane, WIDTH, HEIGHT));
            stage.setTitle(TITLE);
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/favicon.png")));

            double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
            stage.setX((screenWidth - WIDTH) / 2);
            stage.setY((screenHeight - HEIGHT) / 2);

            stage.show();

        } catch (Exception e) {
            DialogHandler.showExceptionError(btnContinue.getScene(), "Hovsa, fejl!", "Der er desværre sket en fejl, kopier stacktrace og send det til en administrator.", e);
        }
    }

}

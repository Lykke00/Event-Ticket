package easv.event;

import atlantafx.base.theme.PrimerLight;
import easv.event.gui.utils.ModalHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private final static int WIDTH = 800;
    private final static int HEIGHT = 600;
    private final static String TITLE = "Event System";

    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/main.fxml"));
        Parent root = fxmlLoader.load();

        // StackPane bruges grundet ModalPane, så vi kan vise
        // modals samt evt. notifikationer i fremtiden
        StackPane stackPane = new StackPane(root);

        // Tilføj CSS til root (stackpane)
        String css = getClass().getResource("/css/main.css").toExternalForm();
        stackPane.getStylesheets().add(css);

        // Send vores StackPane videre til MainController
        // så vi kan håndtere modals
        ModalHandler.getInstance().setRoot(stackPane);

        Scene scene = new Scene(stackPane, WIDTH, HEIGHT);
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

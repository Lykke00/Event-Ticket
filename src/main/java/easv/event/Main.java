package easv.event;

import atlantafx.base.theme.PrimerLight;
import easv.event.gui.pages.Pages;
import easv.event.gui.utils.PageData;
import easv.event.gui.utils.PageHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private final static int WIDTH = 800;
    private final static int HEIGHT = 600;
    private final static String TITLE = "Event System";

    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        PageData pageData = PageHandler.getInstance().storeCurrentPage(Pages.LOGIN);

        Scene scene = pageData.getPageScene();
        stage.setTitle(TITLE);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/favicon.png")));

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

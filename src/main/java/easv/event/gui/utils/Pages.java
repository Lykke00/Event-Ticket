package easv.event.gui.utils;

import easv.event.gui.view.ModalHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.util.Objects;

public enum Pages {
    EVENT("/fxml/pages/EventPage.fxml"),
    TICKETS("/fxml/pages/TicketPage.fxml"),
    USERS("/fxml/pages/UserPage.fxml"),
    EVENT_ITEM_PAGE("/fxml/pages/EventItemPage.fxml");

    private final String path;
    private Object controller;

    Pages(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public Node load() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(getPath()));

        try {
            Node node = loader.load();
            controller = loader.getController();
            return node;
        } catch (Exception e) {
            ModalHandler.showExceptionError(null, "Hovsa, fejl!", "Der er desværre sket en fejl, kopier stacktrace og send det til en administrator.", e);
            return null;
        }
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Object getController() {
        return controller;
    }
}

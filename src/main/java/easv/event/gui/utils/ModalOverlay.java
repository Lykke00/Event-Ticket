package easv.event.gui.utils;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.layout.ModalBox;
import atlantafx.base.theme.Styles;
import javafx.animation.Animation;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;

/**
 * Wrapper for ModalPane to fix animations only happening once during its lifetime.
 */
public class ModalOverlay extends ModalPane {
    public ModalOverlay() {
        super();
        // Animate when clicking bg to close
        this.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            Node target = event.getTarget() instanceof Node ? (Node) event.getTarget() : null;
            // Hvis det er en scrollpane og har styleclassen scrollable-content
            System.out.println(event.getTarget().toString());
            boolean isStackPane = target instanceof StackPane;
            if (isStackPane && target.getStyleClass().contains("scrollable-content") ||
                isStackPane && target.getStyleClass().contains("close-button") ||
                isStackPane && target.getStyleClass().contains("icon")) {
                event.consume();
                hide();
            }
        });
    }

    @Override
    public void show(Node node) {
        Animation inAnimation = inTransitionFactory.get().apply(node);
        super.show(node);
        inAnimation.play();
    }

    @Override
    public void hide() {
        Animation outAnimation = outTransitionFactory.get().apply(getContent());
        outAnimation.setOnFinished(evt -> super.hide(true));
        outAnimation.play();
    }

    public void showFXML(String fxmlPath) {
        displayModal(fxmlPath);
    }


    private void displayModal(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent modalContent = loader.load();

            ModalBox modalBox = new ModalBox();

            modalBox.addContent(modalContent);
            modalBox.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

            modalBox.getStyleClass().add("modal-box");

            show(modalBox);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void hide(boolean clear) {
        hide();
    }
}
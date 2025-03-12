package easv.event.gui.utils;

import atlantafx.base.theme.Styles;
import javafx.scene.control.Button;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class ButtonStyle {

    public static void setWarning(Button button, FontIcon icon) {
        button.setGraphic(icon);
        button.getStyleClass().addAll(
                Styles.BUTTON_ICON, Styles.DANGER
        );
    }

    public static void setSuccess(Button button, FontIcon icon) {
        button.setGraphic(icon);
        button.getStyleClass().addAll(
                Styles.BUTTON_ICON, Styles.SUCCESS
        );
    }

    public static void setPrimary(Button button, FontIcon icon) {
        button.setGraphic(icon);
        button.getStyleClass().addAll(
                Styles.BUTTON_ICON, Styles.ACCENT
        );
    }
}

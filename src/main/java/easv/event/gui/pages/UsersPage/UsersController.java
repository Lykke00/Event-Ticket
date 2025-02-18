package easv.event.gui.pages.UsersPage;

import atlantafx.base.controls.CustomTextField;
import easv.event.gui.utils.ButtonStyle;
import easv.event.gui.utils.ModalHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class UsersController implements Initializable {
    @FXML
    private CustomTextField txtFieldSearch;

    @FXML
    private Button btnAddNewUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        txtFieldSearch.setLeft(new FontIcon(Feather.SEARCH));
        ButtonStyle.setPrimary(btnAddNewUser, new FontIcon(Feather.PLUS));

        btnAddNewUser.setOnAction(event -> {
        });
    }
}

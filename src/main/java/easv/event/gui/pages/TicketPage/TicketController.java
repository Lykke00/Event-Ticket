package easv.event.gui.pages.TicketPage;

import atlantafx.base.controls.CustomTextField;
import easv.event.gui.utils.ButtonStyle;
import easv.event.gui.view.ModalHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class TicketController implements Initializable {

    @FXML
    private CustomTextField txtFieldSearch;

    @FXML
    private Button btnAddNewTicket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ButtonStyle.setPrimary(btnAddNewTicket, new FontIcon(Feather.PLUS));
        txtFieldSearch.setLeft(new FontIcon(Feather.SEARCH));

        btnAddNewTicket.setOnAction(event -> ModalHandler.showExceptionError(btnAddNewTicket.getScene(), "Hovsa, fejl!", "Der er desværre sket en fejl, kopier stacktrace og send det til en administrator.", new Exception("Dette er en test exception")));
    }
}

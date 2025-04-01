package easv.event.gui.modals.EditTicketType;

import easv.event.gui.MainModel;
import easv.event.gui.common.TicketTypeItemModel;
import easv.event.gui.interactors.TicketInteractor;
import easv.event.gui.modals.IModalController;
import easv.event.gui.utils.NotificationHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class EditTicketTypeController {
    private final TicketInteractor ticketInteractor = MainModel.getInstance().getTicketInteractor();

    @FXML
    private TextField txtFieldName;

    @FXML
    private Button btnEditTicketType;

    @FXML
    private void btnCancelEditTicketType(ActionEvent actionEvent) {
    }

    @FXML
    private void btnActionEditTicketType(ActionEvent actionEvent) {
        String newName = txtFieldName.getText();

        TicketTypeItemModel original = ticketInteractor.getEditTicketTypeModel().getTicketType();
        TicketTypeItemModel copy = TicketTypeItemModel.copy(original);

        copy.nameProperty().set(newName);

        ticketInteractor.editTicketType(original, copy);
    }
}
package easv.event.gui.modals.EditTicketType;

import atlantafx.base.theme.Styles;
import easv.event.gui.MainModel;
import easv.event.gui.common.TicketTypeItemModel;
import easv.event.gui.interactors.TicketInteractor;
import easv.event.gui.modals.IModalController;
import easv.event.gui.utils.ModalHandler;
import easv.event.gui.utils.NotificationHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class EditTicketTypeController implements IModalController {
    private final TicketInteractor ticketInteractor = MainModel.getInstance().getTicketInteractor();

    @FXML
    private TextField txtFieldName;

    @FXML
    private Button btnEditTicketType;

    @Override
    public void load() {
        txtFieldName.setText(ticketInteractor.getEditTicketTypeModel().getTicketType().nameProperty().get());
    }

    @FXML
    private void btnCancelEditTicketType(ActionEvent actionEvent) {
        ModalHandler.getInstance().hideModal();
    }

    @FXML
    private void btnActionEditTicketType(ActionEvent actionEvent) {
        String newName = txtFieldName.getText();

        TicketTypeItemModel original = ticketInteractor.getEditTicketTypeModel().getTicketType();
        TicketTypeItemModel copy = TicketTypeItemModel.copy(original);

        copy.nameProperty().set(newName);

        ticketInteractor.editTicketType(original, copy, succes -> {
            if (succes) {
                NotificationHandler.getInstance().showNotification( "Billet typen " + original.nameProperty().get() + " er blevet redigeret", NotificationHandler.NotificationType.SUCCESS);
                original.updateModel(copy);
                ModalHandler.getInstance().hideModal();
            }
        });
    }
}
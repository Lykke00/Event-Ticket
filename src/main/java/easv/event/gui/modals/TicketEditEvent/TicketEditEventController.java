package easv.event.gui.modals.TicketEditEvent;

import atlantafx.base.controls.MaskTextField;
import easv.event.gui.MainModel;
import easv.event.gui.common.TicketEventItemModel;
import easv.event.gui.interactors.TicketInteractor;
import easv.event.gui.utils.ModalHandler;
import easv.event.gui.utils.NotificationHandler;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class TicketEditEventController implements Initializable {
    private final TicketInteractor ticketInteractor = MainModel.getInstance().getTicketInteractor();

    @FXML
    private MaskTextField maskTxtFieldPrice;

    @FXML
    private Button btnEditEvent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObjectProperty<TicketEventItemModel> ticketEditEventModel = MainModel.getInstance().getTicketEditEventModel().ticketEventItemModelProperty();
        maskTxtFieldPrice.setLeft(new FontIcon(Feather.DOLLAR_SIGN));

        validate();
        updateTextField(ticketEditEventModel.get());

        ticketEditEventModel.addListener((observable, oldItem, newItem) -> {
            updateTextField(newItem);
        });
    }

    private void validate() {
        BooleanBinding isPriceEmpty = maskTxtFieldPrice.textProperty().isEmpty();
        BooleanBinding isPriceInvalid = maskTxtFieldPrice.textProperty().length().lessThan(1);

        btnEditEvent.disableProperty().bind(isPriceEmpty.or(isPriceInvalid));
    }

    private void updateTextField(TicketEventItemModel newItem) {
        maskTxtFieldPrice.setText(newItem.priceProperty().get() + "");
    }

    @FXML
    private void btnCancelEditEvent(ActionEvent actionEvent) {
        ModalHandler.getInstance().hideModal();
    }

    @FXML
    private void btnActionEditEvent(ActionEvent actionEvent) {
        int price = Integer.parseInt(maskTxtFieldPrice.getText());

        TicketEventItemModel original = MainModel.getInstance().getTicketEditEventModel().ticketEventItemModelProperty().get();
        TicketEventItemModel copy = TicketEventItemModel.copy(original);

        copy.priceProperty().set(price);

        ticketInteractor.editTicketEvent(original, copy, succes -> {
            if (succes)
                ModalHandler.getInstance().hideModal();
        });
    }

}

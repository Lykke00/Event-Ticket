package easv.event.gui.modals.NewTicket;

import atlantafx.base.theme.Styles;
import easv.event.gui.MainModel;
import easv.event.gui.common.TicketItemModel;
import easv.event.gui.common.TicketType;
import easv.event.gui.pages.Ticket.TicketModel;
import easv.event.gui.utils.ModalHandler;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class NewTicketController implements Initializable {
    private final TicketModel ticketModel = MainModel.getInstance().getTicketModel();
    private final static ModalHandler modalHandler = ModalHandler.getInstance();

    @FXML
    private ChoiceBox<String> choiceBoxType;

    @FXML
    private Button btnCreateTicket;

    @FXML
    private TextField txtFieldName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupChoiceBox();

        validate();
    }

    private void setupChoiceBox() {
        TicketType[] values = TicketType.values();
        for (TicketType value : values)
            choiceBoxType.getItems().add(value.getType());

        choiceBoxType.getSelectionModel().selectFirst();
    }

    private void validate() {
        BooleanBinding txtFieldInvalid = txtFieldName.textProperty().isEmpty()
                .or(txtFieldName.textProperty().length().lessThan(3));

        BooleanBinding choiceBoxIsEmpty = choiceBoxType.valueProperty().isNull();
        BooleanBinding isAnyFieldEmpty = txtFieldInvalid.or(choiceBoxIsEmpty);

        btnCreateTicket.disableProperty().bind(isAnyFieldEmpty);
    }

    @FXML
    private void btnCancelCreateTicket(ActionEvent actionEvent) {
        modalHandler.hideModal();
    }

    @FXML
    private void btnActionCreateTicket(ActionEvent actionEvent) {
        TicketType ticketType = TicketType.fromString(choiceBoxType.getValue());

        // ikke valid valg, gør choicebox rød
        if (ticketType == null) {
            choiceBoxType.pseudoClassStateChanged(Styles.STATE_DANGER, true);
            return;
        }

        ticketModel.ticketItemModelsListProperty().add(new TicketItemModel(ticketModel.ticketItemModelsListProperty().size() + 1, txtFieldName.getText(), ticketType));
        modalHandler.hideModal();
    }
}

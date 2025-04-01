package easv.event.gui.modals.NewTicket;

import atlantafx.base.theme.Styles;
import easv.event.gui.MainModel;
import easv.event.gui.common.TicketItemModel;
import easv.event.gui.common.TicketTypeItemModel;
import easv.event.gui.interactors.TicketInteractor;
import easv.event.gui.modals.IModalController;
import easv.event.gui.utils.ModalHandler;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class NewTicketController implements Initializable, IModalController {
    private final TicketInteractor ticketInteractor = MainModel.getInstance().getTicketInteractor();
    private final static ModalHandler modalHandler = ModalHandler.getInstance();

    @FXML
    private VBox vBox;

    @FXML
    private ChoiceBox<TicketTypeItemModel> choiceBoxType;

    @FXML
    private Button btnCreateTicket;

    @FXML
    private TextField txtFieldName;

    Label msgLabel = new Label();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldListener();
        setupChoiceBox();
        validate();

        vBox.getChildren().add(msgLabel);
    }

    @Override
    public void load() {
        ticketInteractor.loadAllTicketTypes();
    }

    private void textFieldListener() {
        txtFieldName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                msgLabel.setVisible(false);
                txtFieldName.pseudoClassStateChanged(Styles.STATE_DANGER, false);
            }
        });
    }

    private void setupChoiceBox() {
        ObservableList<TicketTypeItemModel> values = ticketInteractor.getTicketTypeModel().sortedTicketTypeList();

        choiceBoxType.setItems(values);
        choiceBoxType.setConverter(new StringConverter<TicketTypeItemModel>() {
            @Override
            public String toString(TicketTypeItemModel item) {
                return (item != null) ? item.nameProperty().get() : "";
            }

            @Override
            public TicketTypeItemModel fromString(String string) {
                return null;
            }
        });

        values.addListener((ListChangeListener<TicketTypeItemModel>) change -> {
            if (!values.isEmpty() && choiceBoxType.getSelectionModel().isEmpty()) {
                choiceBoxType.getSelectionModel().selectFirst();
            }
        });

        if (!values.isEmpty())
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
        TicketTypeItemModel ticketType = choiceBoxType.getValue();
        String ticketName = txtFieldName.getText();

        // ikke valid valg, gør choicebox rød
        if (ticketType == null) {
            choiceBoxType.pseudoClassStateChanged(Styles.STATE_DANGER, true);
            return;
        }

        ticketInteractor.doesTicketExist(ticketName, exists -> {
            if (exists) {
                txtFieldName.pseudoClassStateChanged(Styles.STATE_DANGER, true);

                msgLabel.setVisible(true);
                msgLabel.setText("Billet navn eksisterer allerede");
                msgLabel.getStyleClass().addAll(Styles.TEXT, Styles.DANGER);
            } else {
                txtFieldName.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                TicketItemModel newTicket = new TicketItemModel(ticketName, ticketType);
                ticketInteractor.createTicket(newTicket);
                modalHandler.hideModal();
            }
        });

    }
}
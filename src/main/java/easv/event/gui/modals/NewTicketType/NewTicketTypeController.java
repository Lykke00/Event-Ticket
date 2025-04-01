package easv.event.gui.modals.NewTicketType;

import atlantafx.base.theme.Styles;
import easv.event.gui.MainModel;
import easv.event.gui.common.TicketItemModel;
import easv.event.gui.common.TicketTypeItemModel;
import easv.event.gui.interactors.TicketInteractor;
import easv.event.gui.utils.ModalHandler;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class NewTicketTypeController implements Initializable {
    private final TicketInteractor ticketInteractor = MainModel.getInstance().getTicketInteractor();
    private final static ModalHandler modalHandler = ModalHandler.getInstance();

    @FXML
    private TextField txtFieldName;

    @FXML
    private Button btnCreateTicketType;

    @FXML
    private VBox vBox;

    private final Label msgLabel = new Label();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        textFieldListener();
        vBox.getChildren().add(msgLabel);

        validate();
    }

    private void validate() {
        BooleanBinding invalid = txtFieldName.textProperty().isEmpty()
                .or(txtFieldName.textProperty().length().lessThan(3))
                .or(ticketInteractor.getNewTicketTypeModel().databaseLoadingProperty());

        btnCreateTicketType.disableProperty().bind(invalid);
    }

    private void textFieldListener() {
        txtFieldName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                txtFieldName.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                msgLabel.setVisible(false);
            }
        });
    }

    public void btnActionCreateTicketType(ActionEvent actionEvent) {
        String name = txtFieldName.getText();

        ticketInteractor.doesTicketTypeExist(name, exists -> {
            if (exists) {
                txtFieldName.pseudoClassStateChanged(Styles.STATE_DANGER, true);

                msgLabel.setVisible(true);
                msgLabel.setText("Billet type eksisterer allerede");
                msgLabel.getStyleClass().addAll(Styles.TEXT, Styles.DANGER);
            } else {
                TicketTypeItemModel ticketTypeItemModel = new TicketTypeItemModel(name);
                ticketInteractor.createTicketType(ticketTypeItemModel);
                modalHandler.hideModal();

            }
        });
    }

    public void btnCancelCreateTicketType(ActionEvent actionEvent) {
        modalHandler.hideModal();
    }

}
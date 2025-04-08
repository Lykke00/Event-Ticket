package easv.event.gui.modals.SellTicket;

import easv.event.gui.MainModel;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.common.TicketEventItemModel;
import easv.event.gui.interactors.TicketInteractor;
import easv.event.gui.modals.IModalController;
import easv.event.gui.utils.ModalHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class SellTicketController implements Initializable, IModalController {
    private final TicketInteractor ticketInteractor = MainModel.getInstance().getTicketInteractor();
    private final SellTicketModel model = ticketInteractor.getSellTicketModel();

    @FXML
    private Spinner<Integer> spinnerAmount;

    @FXML
    private Label lblTotal;

    @FXML
    private TextField txtFieldEmail;

    @FXML
    private ChoiceBox<TicketEventItemModel> choiceBoxTicket;

    @FXML
    private Button btnSellTicket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateChoice();
        validate();
        setupSpinner();
        updateTotalLabel();
    }

    @Override
    public void load() {
        updateChoice();
    }

    private void updateChoice() {
        ticketInteractor.getTicketEventByEvent(model.eventModelProperty().get());

        model.databaseLoadingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                choiceBoxTicket.getItems().clear();
                choiceBoxTicket.getItems().setAll(model.ticketEventItemModelListProperty());
                choiceBoxTicket.getSelectionModel().selectFirst();
            }
        });

        choiceBoxTicket.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                updateTotalLabel(newValue);
        });
    }

    private void validate() {
        BooleanBinding emptyFields = txtFieldEmail.textProperty().isEmpty()
                .or(Bindings.createBooleanBinding(() -> !isValidEmail(txtFieldEmail.getText()), txtFieldEmail.textProperty()));

        btnSellTicket.disableProperty().bind(emptyFields);
    }

    private void setupSpinner() {
        spinnerAmount.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        spinnerAmount.setEditable(true);
    }

    private void updateTotalLabel() {
        spinnerAmount.valueProperty().addListener((observable, oldValue, newValue) -> {
            TicketEventItemModel selectedTicket = choiceBoxTicket.getSelectionModel().getSelectedItem();
            if (selectedTicket != null) {
                updateTotalLabel(selectedTicket);
            }
        });
    }

    private void updateTotalLabel(TicketEventItemModel selectedTicket) {
        int amount = spinnerAmount.getValue();
        lblTotal.setText("Total: " + selectedTicket.priceProperty().get() * amount + " DKK");
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    @FXML
    private void btnCancelSellTicket(ActionEvent actionEvent) {
        ModalHandler.getInstance().hideModal();
    }

    @FXML
    private void btnActionSellTicket(ActionEvent actionEvent) {
        TicketEventItemModel selectedTicket = choiceBoxTicket.getSelectionModel().getSelectedItem();
        String email = txtFieldEmail.getText();
        int amount = spinnerAmount.getValue();

        ticketInteractor.sellTicket(selectedTicket, amount, email, success -> {
            if (success) {
                ModalHandler.getInstance().hideModal();
            }
        });
    }
}

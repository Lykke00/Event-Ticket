package easv.event.gui.modals.TicketAddEvent;

import atlantafx.base.controls.MaskTextField;
import easv.event.gui.MainModel;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.common.TicketEventItemModel;
import easv.event.gui.common.TicketItemModel;
import easv.event.gui.common.UserModel;
import easv.event.gui.interactors.EventInteractor;
import easv.event.gui.interactors.TicketInteractor;
import easv.event.gui.modals.IModalController;
import easv.event.gui.pages.Event.EventModel;
import easv.event.gui.pages.Ticket.ItemView.TicketItemViewModel;
import easv.event.gui.utils.ModalHandler;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TicketAddEventController implements Initializable, IModalController {
    private final EventInteractor eventInteractor = MainModel.getInstance().getEventInteractor();
    private final TicketInteractor ticketInteractor = MainModel.getInstance().getTicketInteractor();
    private final EventModel eventModel = eventInteractor.getEventModel();

    private TicketItemViewModel ticketItemModel;

    @FXML
    private VBox vBoxEvent;

    @FXML
    private MaskTextField maskTxtFieldPrice;

    @FXML
    private CheckComboBox<EventItemModel> comboBoxEvent = new CheckComboBox<>();

    @FXML
    private Button btnAddEvent;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //comboBoxEvent.getItems().addAll(eventModel.eventsListProperty());

        comboBoxEvent.setPrefWidth(Control.USE_COMPUTED_SIZE);
        comboBoxEvent.setMaxWidth(Double.MAX_VALUE);

        updateEventComboBox(ticketInteractor.getTicketItemViewModel().ticketItemModel());

        vBoxEvent.getChildren().add(comboBoxEvent);

        txtFieldPriceOnlyNumbers();
        validate();

        maskTxtFieldPrice.setLeft(new FontIcon(Feather.DOLLAR_SIGN));
    }

    @Override
    public void load() {
        ticketItemModel = ticketInteractor.getTicketItemViewModel();
        ticketInteractor.getEventTicketsByEvent(ticketItemModel.ticketItemModel());

        ticketItemModel.databaseLoadingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                updateEventComboBox(ticketItemModel.ticketItemModel());
            }
        });
    }

    private void updateEventComboBox(TicketItemModel ticketItemModel) {
        ObservableList<EventItemModel> allEventModels = eventInteractor.getEventModel().eventsListProperty();
        ObservableList<TicketEventItemModel> ticketEvents = ticketItemModel != null ? ticketItemModel.getTicketEventItemModels() : FXCollections.observableArrayList();

        comboBoxEvent.getCheckModel().clearChecks();
        comboBoxEvent.getItems().setAll(allEventModels);

        for (TicketEventItemModel ticket : ticketEvents) {
            EventItemModel event = allEventModels.stream()
                    .filter(e -> e.idProperty().get() == ticket.eventProperty().get().idProperty().get())
                    .findFirst()
                    .orElse(null);
            if (event != null) {
                comboBoxEvent.getCheckModel().check(event);
            }
        }
    }

    private void txtFieldPriceOnlyNumbers() {
        maskTxtFieldPrice.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                maskTxtFieldPrice.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void validate() {
        BooleanBinding isPriceEmpty = maskTxtFieldPrice.textProperty().isEmpty();
        BooleanBinding isPriceInvalid = maskTxtFieldPrice.textProperty().length().lessThan(1);

        btnAddEvent.disableProperty().bind(isPriceEmpty.or(isPriceInvalid));
    }


    @FXML
    private void btnCancelAddEvent(ActionEvent actionEvent) {
        ModalHandler.getInstance().hideModal();
    }

    @FXML
    private void btnActionAddEvent(ActionEvent actionEvent) {
        ObservableList<TicketEventItemModel> ticketEvents = ticketItemModel.ticketItemModel().getTicketEventItemModels();
        ObservableList<EventItemModel> selectedEvents = comboBoxEvent.getCheckModel().getCheckedItems();
        ObservableList<EventItemModel> allEvents = comboBoxEvent.getItems();

        ObservableList<EventItemModel> addedEvents = FXCollections.observableArrayList();
        ObservableList<EventItemModel> removedEvents = FXCollections.observableArrayList();

        selectedEvents.forEach(event -> {
            boolean exists = ticketEvents.stream()
                    .anyMatch(ticketEvent -> ticketEvent.eventProperty().get().idProperty().get() == event.idProperty().get());

            if (!exists) {
                addedEvents.add(event);
            }
        });

        allEvents.forEach(event -> {
            boolean stillSelected = selectedEvents.contains(event);

            boolean wasPreviouslyAdded = ticketEvents.stream()
                    .anyMatch(ticketEvent -> ticketEvent.eventProperty().get().idProperty().get() == event.idProperty().get());

            if (wasPreviouslyAdded && !stillSelected) {
                removedEvents.add(event);
            }
        });

        double price = Double.parseDouble(maskTxtFieldPrice.getText());
        ticketInteractor.addTicketToEvent(ticketItemModel.ticketItemModel(), price, addedEvents, removedEvents);

        ModalHandler.getInstance().hideModal();
    }
}

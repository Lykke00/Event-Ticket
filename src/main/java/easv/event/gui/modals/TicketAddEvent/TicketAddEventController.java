package easv.event.gui.modals.TicketAddEvent;

import atlantafx.base.controls.MaskTextField;
import easv.event.gui.MainModel;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.common.TicketEventItemModel;
import easv.event.gui.common.TicketItemModel;
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
import java.util.ResourceBundle;

public class TicketAddEventController implements Initializable {
    private EventModel eventModel = MainModel.getInstance().getEventModel();
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
        ticketItemModel = MainModel.getInstance().getTicketItemViewModel();
        comboBoxEvent.getItems().addAll(eventModel.eventsListProperty());

        comboBoxEvent.setPrefWidth(Control.USE_COMPUTED_SIZE);
        comboBoxEvent.setMaxWidth(Double.MAX_VALUE);

        updateEventComboBox(ticketItemModel.ticketItemModelProperty().get());

        vBoxEvent.getChildren().add(comboBoxEvent);

        ticketItemModel.ticketItemModelProperty().addListener((observable, oldItem, newItem) -> {
            updateEventComboBox(newItem);
        });

        txtFieldPriceOnlyNumbers();
        validate();

        maskTxtFieldPrice.setLeft(new FontIcon(Feather.DOLLAR_SIGN));
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

    private void updateEventComboBox(TicketItemModel ticketItemModel) {
        ObservableList<EventItemModel> allEvents = eventModel.eventsListProperty();
        ObservableList<TicketEventItemModel> ticketEvents = ticketItemModel != null ? ticketItemModel.getTicketEventItemModels() : FXCollections.observableArrayList();

        comboBoxEvent.getItems().clear();

        ObservableList<EventItemModel> selectedEvents = FXCollections.observableArrayList();

        ticketEvents.forEach(ticketEvent -> {
            EventItemModel event = allEvents.stream()
                    .filter(e -> e.idProperty().get() == ticketEvent.eventIdProperty().get())
                    .findFirst()
                    .orElse(null);
            if (event != null) {
                selectedEvents.add(event);
            }
        });

        ObservableList<EventItemModel> filteredEvents = FXCollections.observableArrayList(
                allEvents.stream()
                        .filter(event -> ticketEvents.stream().noneMatch(ticketEvent -> ticketEvent.eventIdProperty().get() == event.idProperty().get()))
                        .toList()
        );

        comboBoxEvent.getItems().setAll(FXCollections.observableArrayList(selectedEvents));
        comboBoxEvent.getItems().addAll(filteredEvents);

        selectedEvents.forEach(comboBoxEvent.getCheckModel()::check);
    }

    @FXML
    private void btnCancelAddEvent(ActionEvent actionEvent) {
        ModalHandler.getInstance().hideModal();
    }

    @FXML
    private void btnActionAddEvent(ActionEvent actionEvent) {
        ObservableList<TicketEventItemModel> ticketEvents = ticketItemModel.ticketItemModelProperty().get().getTicketEventItemModels();

        ObservableList<EventItemModel> selectedEvents = comboBoxEvent.getCheckModel().getCheckedItems();
        ObservableList<TicketEventItemModel> addedEvents = FXCollections.observableArrayList();

        // hvis den allerede er tilføjet, så ikke tilføj den igen
        selectedEvents.forEach(event -> {
            boolean exists = ticketEvents.stream()
                    .anyMatch(ticketEvent -> ticketEvent.eventIdProperty().get() == event.idProperty().get());

            if (!exists)
                addedEvents.add(new TicketEventItemModel(event.idProperty().get(), event.nameProperty().get(), Integer.parseInt(maskTxtFieldPrice.getText())));
        });

        ticketEvents.addAll(addedEvents);

        ticketEvents.removeIf(ticketEvent ->
                selectedEvents.stream().noneMatch(event -> event.idProperty().get() == ticketEvent.eventIdProperty().get())
        );

        ModalHandler.getInstance().hideModal();
    }
}

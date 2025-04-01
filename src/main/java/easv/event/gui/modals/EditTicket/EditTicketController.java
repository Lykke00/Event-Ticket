package easv.event.gui.modals.EditTicket;

import easv.event.gui.MainModel;
import easv.event.gui.common.TicketItemModel;
import easv.event.gui.common.TicketTypeItemModel;
import easv.event.gui.utils.ModalHandler;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

public class EditTicketController implements Initializable {
    private final EditTicketModel editTicketModel = MainModel.getInstance().getEditTicketModel();
    private final static ModalHandler modalHandler = ModalHandler.getInstance();

    @FXML
    private Label lblTitle, lblDescription;

    @FXML
    private ChoiceBox<TicketTypeItemModel> choiceBoxType;

    @FXML
    private Button btnCreateTicket;

    @FXML
    private TextField txtFieldName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupChoiceBox();

        setupBinding();
        validate();
    }

    private <T> void bindProperty(EditTicketModel editModel, Property<T> uiProperty, Function<TicketItemModel, Property<T>> modelPropertyGetter) {
        TicketItemModel eventItem = editModel.ticketItemModelProperty().get();

        if (eventItem != null) {
            uiProperty.setValue(modelPropertyGetter.apply(eventItem).getValue());

            modelPropertyGetter.apply(eventItem).addListener((observable, oldValue, newValue) -> {
                uiProperty.setValue(newValue);
            });
        }

        editModel.ticketItemModelProperty().addListener((observable, oldItem, newItem) -> {
            if (newItem != null) {
                uiProperty.setValue(modelPropertyGetter.apply(newItem).getValue());
            }
        });
    }

    private <T> void bindProperty(EditTicketModel editModel, Property<String> uiProperty,
                                  Function<TicketItemModel, Property<T>> modelPropertyGetter, Function<T, String> formatFunction) {
        TicketItemModel eventItem = editModel.ticketItemModelProperty().get();

        if (eventItem != null) {
            uiProperty.setValue(formatFunction.apply(modelPropertyGetter.apply(eventItem).getValue()));

            modelPropertyGetter.apply(eventItem).addListener((observable, oldValue, newValue) -> {
                uiProperty.setValue(formatFunction.apply(newValue));
            });
        }

        editModel.ticketItemModelProperty().addListener((observable, oldItem, newItem) -> {
            if (newItem != null) {
                uiProperty.setValue(formatFunction.apply(modelPropertyGetter.apply(newItem).getValue()));
            }
        });
    }

    private void setupBinding() {
        bindProperty(editTicketModel, txtFieldName.textProperty(), TicketItemModel::nameProperty);
        bindProperty(editTicketModel, choiceBoxType.valueProperty(), TicketItemModel::typeProperty);
        bindProperty(editTicketModel, lblDescription.textProperty(), TicketItemModel::nameProperty,
                name -> "Redigerer: " + name);
    }

    private void setupChoiceBox() {
        //icketType[] values = TicketType.values();
        // for (TicketType value : values)
        //    choiceBoxType.getItems().add(value);

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
        TicketItemModel.updateTicketItemModel(
                editTicketModel.ticketItemModelProperty().get(),
                txtFieldName.getText(),
                choiceBoxType.getValue()
        );
        modalHandler.hideModal();
    }
}
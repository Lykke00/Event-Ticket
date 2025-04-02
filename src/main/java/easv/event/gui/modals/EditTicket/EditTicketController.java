package easv.event.gui.modals.EditTicket;

import easv.event.gui.MainModel;
import easv.event.gui.common.TicketItemModel;
import easv.event.gui.common.TicketTypeItemModel;
import easv.event.gui.interactors.TicketInteractor;
import easv.event.gui.modals.IModalController;
import easv.event.gui.utils.ModalHandler;
import easv.event.gui.utils.NotificationHandler;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

public class EditTicketController implements Initializable, IModalController {
    private final TicketInteractor ticketInteractor = MainModel.getInstance().getTicketInteractor();
    private final EditTicketModel editTicketModel = ticketInteractor.getEditTicketModel();
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

    @Override
    public void load() {
        ticketInteractor.loadAllTicketTypes();
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
                choiceBoxType.getSelectionModel().select(editTicketModel.ticketItemModelProperty().get().typeProperty().get());
            }
        });

        if (!values.isEmpty())
            choiceBoxType.getSelectionModel().select(editTicketModel.ticketItemModelProperty().get().typeProperty().get());
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
        String newName = txtFieldName.getText();
        TicketTypeItemModel newType = choiceBoxType.getValue();

        TicketItemModel original = editTicketModel.ticketItemModelProperty().get();
        TicketItemModel copy = TicketItemModel.copy(original);

        copy.nameProperty().set(newName);
        copy.typeProperty().set(newType);

        ticketInteractor.editTicket(copy, succes -> {
            if (succes) {
                NotificationHandler.getInstance().showNotification( "Billetten " + original.nameProperty().get() + " er blevet redigeret", NotificationHandler.NotificationType.SUCCESS);
                original.updateModel(copy);
                ModalHandler.getInstance().hideModal();
            }
        });
    }
}
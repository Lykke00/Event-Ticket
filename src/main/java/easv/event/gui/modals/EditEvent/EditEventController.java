package easv.event.gui.modals.EditEvent;

import atlantafx.base.controls.MaskTextField;
import atlantafx.base.theme.Styles;
import easv.event.gui.MainModel;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.utils.ModalHandler;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import java.util.function.Function;

import static easv.event.gui.utils.DateFormatter.createLocalDateConverter;

public class EditEventController implements Initializable {
    private final static ModalHandler modalHandler = ModalHandler.getInstance();

    @FXML
    private Button btnUpdateEvent;

    @FXML
    private TextArea txtAreaDescription;

    @FXML
    private Label lblTitle;

    @FXML
    private MaskTextField maskTxtFieldTime;

    @FXML
    private DatePicker datePickr;

    @FXML
    private TextField txtFieldName, txtFieldLocation;

    private <T> void bindProperty(EditEventModel editModel, Property<T> uiProperty, Function<EventItemModel, Property<T>> modelPropertyGetter) {
        EventItemModel eventItem = editModel.eventItemModelProperty().get();

        if (eventItem != null) {
            uiProperty.setValue(modelPropertyGetter.apply(eventItem).getValue());

            modelPropertyGetter.apply(eventItem).addListener((observable, oldValue, newValue) -> {
                uiProperty.setValue(newValue);
            });
        }

        editModel.eventItemModelProperty().addListener((observable, oldItem, newItem) -> {
            if (newItem != null) {
                uiProperty.setValue(modelPropertyGetter.apply(newItem).getValue());
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EditEventModel itemModel = MainModel.getInstance().getEditEventModel();

        bindProperty(itemModel, lblTitle.textProperty(), EventItemModel::nameProperty);
        bindProperty(itemModel, txtFieldName.textProperty(), EventItemModel::nameProperty);
        bindProperty(itemModel, txtFieldLocation.textProperty(), EventItemModel::locationProperty);
        bindProperty(itemModel, maskTxtFieldTime.textProperty(), EventItemModel::timeProperty);
        bindProperty(itemModel, txtAreaDescription.textProperty(), EventItemModel::descriptionProperty);
        bindProperty(itemModel, datePickr.valueProperty(), EventItemModel::dateProperty);

        bindButton();
        setTimeMask(itemModel);
        setDateFeedback();
    }

    private void bindButton() {
        BooleanBinding fieldsValidBinding = new BooleanBinding() {
            {
                super.bind(
                        txtFieldName.textProperty(),
                        txtFieldLocation.textProperty(),
                        datePickr.valueProperty(),
                        maskTxtFieldTime.textProperty()
                );
            }

            @Override
            protected boolean computeValue() {
                boolean isFieldsValid = !txtFieldName.getText().trim().isEmpty() &&
                        !txtFieldLocation.getText().trim().isEmpty();

                LocalDate selectedDate = datePickr.getValue();
                boolean isDateValid = false;

                if (selectedDate != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
                    String formattedDate = selectedDate.format(formatter);

                    isDateValid = formattedDate != null && !formattedDate.isEmpty();
                }

                boolean isTimeValid = false;
                String timeText = maskTxtFieldTime.getText().trim();
                if (timeText.matches("\\d{2}:\\d{2}")) {
                    isTimeValid = true;
                }

                return isFieldsValid && isDateValid && isTimeValid;
            }
        };

        btnUpdateEvent.disableProperty().bind(fieldsValidBinding.not());
    }

    private void setTimeMask(EditEventModel itemModel) {
        var timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        //maskTxtFieldTime.setText(LocalTime.now(ZoneId.systemDefault()).format(timeFormatter));
        maskTxtFieldTime.setMask("29:59");
        maskTxtFieldTime.setText(itemModel.eventItemModelProperty().get().timeProperty().get());

        maskTxtFieldTime.setLeft(new FontIcon(Material2OutlinedMZ.TIMER));
        maskTxtFieldTime.textProperty().addListener((obs, old, val) -> {
            if (val != null) {
                try {
                    LocalTime.parse(val, timeFormatter);
                    maskTxtFieldTime.pseudoClassStateChanged(Styles.STATE_DANGER, false);
                } catch (DateTimeParseException e) {
                    maskTxtFieldTime.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                }
            }
        });
    }

    private void setDateFeedback() {
        final var today = LocalDate.now(ZoneId.systemDefault());
        final var formatter = DateTimeFormatter.ISO_DATE;

        datePickr.setConverter(createLocalDateConverter(formatter));

        datePickr.setDayCellFactory(dayCell -> {
            DateCell cell = new DateCell();
            if (cell.getItem() != null && cell.getItem().isBefore(today))
                cell.setDisable(true);
            return cell;
        });
    }

    @FXML
    private void btnCancelEditEvent(ActionEvent actionEvent) {
        modalHandler.hideModal();
    }

    @FXML
    private void btnActionUpdateEvent(ActionEvent actionEvent) {
        EditEventModel itemModel = MainModel.getInstance().getEditEventModel();
        EventItemModel eventItemModel = itemModel.eventItemModelProperty().get();

        itemModel.eventItemModelProperty().get().nameProperty().set(txtFieldName.getText());
        itemModel.eventItemModelProperty().get().descriptionProperty().set(txtAreaDescription.getText());
        itemModel.eventItemModelProperty().get().locationProperty().set(txtFieldLocation.getText());
        itemModel.eventItemModelProperty().get().timeProperty().set(maskTxtFieldTime.getText());
        itemModel.eventItemModelProperty().get().dateProperty().set(datePickr.getValue());

        MainModel.getInstance().getEventInteractor().editEvent(eventItemModel);

        modalHandler.hideModal();
    }
}

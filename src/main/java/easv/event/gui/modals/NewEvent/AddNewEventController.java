package easv.event.gui.modals.NewEvent;

import atlantafx.base.controls.MaskTextField;
import atlantafx.base.theme.Styles;
import easv.event.gui.MainModel;
import easv.event.gui.pages.Event.EventModel;
import easv.event.gui.utils.ModalHandler;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

import static easv.event.gui.utils.DateFormatter.createLocalDateConverter;

public class AddNewEventController implements Initializable {
    private final EventModel eventModel = MainModel.getInstance().getEventModel();
    private final static ModalHandler modalHandler = ModalHandler.getInstance();

    @FXML
    private Button btnCreateEvent;

    @FXML
    private MaskTextField maskTxtFieldTime;

    @FXML
    private DatePicker datePickr;

    @FXML
    private TextField txtFieldName, txtFieldLocation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTimeMask();
        bindButton();
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

        btnCreateEvent.disableProperty().bind(fieldsValidBinding.not());
    }

    private void setTimeMask() {
        var timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        maskTxtFieldTime.setText(LocalTime.now(ZoneId.systemDefault()).format(timeFormatter));
        maskTxtFieldTime.setMask("29:59");

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
    private void btnCancelCreateEvent(ActionEvent actionEvent) {
        modalHandler.hideModal();
    }

    @FXML
    private void btnActionCreateEvent(ActionEvent actionEvent) {
        eventModel.addEvent(1, txtFieldName.getText(), txtFieldLocation.getText(), maskTxtFieldTime.getText(), datePickr.getValue().toString());

        modalHandler.hideModal();
    }
}

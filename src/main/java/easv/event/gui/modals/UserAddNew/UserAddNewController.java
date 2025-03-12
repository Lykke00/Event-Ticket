package easv.event.gui.modals.UserAddNew;

import easv.event.gui.MainModel;
import easv.event.gui.common.UserRole;
import easv.event.gui.utils.ModalHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class UserAddNewController implements Initializable {

    @FXML
    private TextField txtFieldName, txtFieldSurname, txtFieldLocation, txtFieldEmail;

    @FXML
    private ChoiceBox<UserRole> comboBoxType;

    @FXML
    private Button btnAddUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validate();
        populateComboBox();
    }

    private void validate() {
        BooleanBinding emptyFields = txtFieldName.textProperty().isEmpty()
                .or(txtFieldSurname.textProperty().isEmpty())
                .or(txtFieldLocation.textProperty().isEmpty())
                .or(txtFieldEmail.textProperty().isEmpty())
                .or(comboBoxType.valueProperty().isNull())
                .or(Bindings.createBooleanBinding(() -> !isValidEmail(txtFieldEmail.getText()), txtFieldEmail.textProperty()));

        btnAddUser.disableProperty().bind(emptyFields);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    private void populateComboBox() {
        comboBoxType.getItems().addAll(UserRole.values());

        comboBoxType.getSelectionModel().selectLast();
    }

    @FXML
    private void btnCancelAddUser(ActionEvent actionEvent) {
        ModalHandler.getInstance().hideModal();
    }

    @FXML
    private void btnActionAddUser(ActionEvent actionEvent) {
        MainModel.getInstance().getUsersModel().createUser(
                txtFieldName.getText(),
                txtFieldSurname.getText(),
                txtFieldLocation.getText(),
                txtFieldEmail.getText(),
                comboBoxType.getValue()
        );

        ModalHandler.getInstance().hideModal();
    }
}

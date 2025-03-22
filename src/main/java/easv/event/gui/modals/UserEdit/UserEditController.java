package easv.event.gui.modals.UserEdit;

import easv.event.gui.MainModel;
import easv.event.gui.common.UserModel;
import easv.event.enums.UserRole;
import easv.event.gui.utils.ModalHandler;
import javafx.beans.binding.Bindings;
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

public class UserEditController implements Initializable {
    @FXML
    private TextField txtFieldName, txtFieldSurname, txtFieldLocation, txtFieldEmail;

    @FXML
    private ChoiceBox<UserRole> comboBoxType;

    @FXML
    private Button btnEditUser;

    @FXML
    private Label lblDescription;

    private <T> void bindProperty(UserEditModel editModel, Property<T> uiProperty, Function<UserModel, Property<T>> modelPropertyGetter) {
        UserModel userItem = editModel.userModelProperty().get();

        if (userItem != null) {
            uiProperty.setValue(modelPropertyGetter.apply(userItem).getValue());

            modelPropertyGetter.apply(userItem).addListener((observable, oldValue, newValue) -> {
                uiProperty.setValue(newValue);
            });
        }

        editModel.userModelProperty().addListener((observable, oldItem, newItem) -> {
            if (newItem != null) {
                uiProperty.setValue(modelPropertyGetter.apply(newItem).getValue());
            }
        });
    }

    private void bindFullNameDescription(UserEditModel editModel, Property<String> uiProperty) {
        UserModel userItem = editModel.userModelProperty().get();

        if (userItem != null) {
            updateDescription(uiProperty, userItem);

            userItem.firstNameProperty().addListener((observable, oldValue, newValue) -> updateDescription(uiProperty, userItem));
            userItem.lastNameProperty().addListener((observable, oldValue, newValue) -> updateDescription(uiProperty, userItem));
        }

        editModel.userModelProperty().addListener((observable, oldItem, newItem) -> {
            if (newItem != null) {
                updateDescription(uiProperty, newItem);
            }
        });
    }

    private void updateDescription(Property<String> uiProperty, UserModel userItem) {
        String fullName = String.format("Redigerer: %s %s", userItem.firstNameProperty().get(), userItem.lastNameProperty().get());
        uiProperty.setValue(fullName);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateBindings();
        populateComboBox();
        validate();
    }

    private void updateBindings() {
        UserEditModel userEditModel = MainModel.getInstance().getUserEditModel();
        bindProperty(userEditModel, txtFieldName.textProperty(), UserModel::firstNameProperty);
        bindProperty(userEditModel, txtFieldSurname.textProperty(), UserModel::lastNameProperty);
        bindProperty(userEditModel, txtFieldLocation.textProperty(), UserModel::locationProperty);
        bindProperty(userEditModel, txtFieldEmail.textProperty(), UserModel::emailProperty);
        bindProperty(userEditModel, comboBoxType.valueProperty(), UserModel::roleProperty);

        bindFullNameDescription(userEditModel, lblDescription.textProperty());
    }

    private void validate() {
        BooleanBinding emptyFields = txtFieldName.textProperty().isEmpty()
                .or(txtFieldSurname.textProperty().isEmpty())
                .or(txtFieldLocation.textProperty().isEmpty())
                .or(txtFieldEmail.textProperty().isEmpty())
                .or(comboBoxType.valueProperty().isNull())
                .or(Bindings.createBooleanBinding(() -> !isValidEmail(txtFieldEmail.getText()), txtFieldEmail.textProperty()));

        btnEditUser.disableProperty().bind(emptyFields);
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
    private void btnCancelEditUser(ActionEvent actionEvent) {
        ModalHandler.getInstance().hideModal();
    }

    @FXML
    private void btnActionEditUser(ActionEvent actionEvent) {
        UserModel userModel = MainModel.getInstance().getUserEditModel().userModelProperty().get();

        userModel.firstNameProperty().set(txtFieldName.getText());
        userModel.lastNameProperty().set(txtFieldSurname.getText());
        userModel.locationProperty().set(txtFieldLocation.getText());
        userModel.emailProperty().set(txtFieldEmail.getText());
        userModel.roleProperty().set(comboBoxType.getValue());

        ModalHandler.getInstance().hideModal();
    }
}

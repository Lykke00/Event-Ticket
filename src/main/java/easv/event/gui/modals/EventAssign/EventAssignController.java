package easv.event.gui.modals.EventAssign;

import easv.event.gui.MainModel;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.common.UserModel;
import easv.event.gui.utils.ModalHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class EventAssignController implements Initializable {
    private EventAssignModel eventAssignModel;

    @FXML
    private VBox vBoxEvent;

    @FXML
    private Button btnAssign;

    @FXML
    private Label labelDescription;

    @FXML
    private CheckComboBox<UserModel> comboBoxUsers = new CheckComboBox<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventAssignModel = MainModel.getInstance().getEventAssignModel();

        eventAssignModel.eventModelProperty().addListener((observable, oldItem, newItem) -> {
            updateEventComboBox(newItem);
        });

        updateEventComboBox(eventAssignModel.eventModelProperty().get());

        vBoxEvent.getChildren().add(comboBoxUsers);
    }

    private void updateEventComboBox(EventItemModel eventItemModel) {
        ObservableList<UserModel> allCoordinators = MainModel.getInstance().getUsersModel().usersModelObservableList();
        ObservableList<UserModel> eventCoordinators = eventItemModel != null ? eventItemModel.coordinatorsProperty() : FXCollections.observableArrayList();

        comboBoxUsers.getCheckModel().clearChecks();
        comboBoxUsers.getItems().setAll(allCoordinators);

        for (UserModel coordinator : eventCoordinators) {
            UserModel user = allCoordinators.stream()
                    .filter(e -> e.idProperty().get() == coordinator.idProperty().get())
                    .findFirst()
                    .orElse(null);
            if (user != null) {
                comboBoxUsers.getCheckModel().check(user);
            }
        }

        labelDescription.setText("Tildel til " + eventItemModel.nameProperty().get());
    }

    @FXML
    private void btnCancelAssign(ActionEvent actionEvent) {
        ModalHandler.getInstance().hideModal();
    }

    @FXML
    private void btnActionAssign(ActionEvent actionEvent) {
        ObservableList<UserModel> selectedUsers = comboBoxUsers.getCheckModel().getCheckedItems();

        ObservableList<UserModel> currentCoordinators = eventAssignModel.eventModelProperty().get().coordinatorsProperty();

        currentCoordinators.clear();

        currentCoordinators.addAll(selectedUsers);

        selectedUsers.removeIf(userModel ->
                currentCoordinators.stream().noneMatch(user -> user.idProperty().get() == userModel.idProperty().get())
        );
        ModalHandler.getInstance().hideModal();
    }

}

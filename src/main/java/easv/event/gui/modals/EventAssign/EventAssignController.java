package easv.event.gui.modals.EventAssign;

import easv.event.gui.MainModel;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.common.UserModel;
import easv.event.gui.interactors.EventInteractor;
import easv.event.gui.interactors.UserInteractor;
import easv.event.gui.modals.IModalController;
import easv.event.gui.utils.ModalHandler;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EventAssignController implements Initializable, IModalController {
    private final EventInteractor eventInteractor = MainModel.getInstance().getEventInteractor();
    private final UserInteractor userInteractor = MainModel.getInstance().getUserInteractor();
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

        vBoxEvent.getChildren().add(comboBoxUsers);

        comboBoxUsers.disableProperty().bind(eventAssignModel.loadingFromDatabaseProperty().and(userInteractor.loadingCoordinatorsFromDbProperty()));
    }

    @Override
    public void load() {
        eventAssignModel = MainModel.getInstance().getEventAssignModel();

        userInteractor.initializeAllCoordinators();
        eventInteractor.getCoordinatorsForAssign(eventAssignModel);

        BooleanProperty loadingFromDatabase = eventAssignModel.loadingFromDatabaseProperty();
        BooleanProperty loadingCoordinatorsFromDb = userInteractor.loadingCoordinatorsFromDbProperty();

        BooleanBinding bothLoaded = loadingFromDatabase.not().and(loadingCoordinatorsFromDb.not());

        bothLoaded.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                updateEventComboBox(eventAssignModel.eventItemModel());
            }
        });

    }

    private void updateEventComboBox(EventItemModel eventItemModel) {
        ObservableList<UserModel> allCoordinators = userInteractor.getUsersModel().coordinatorListModelObservableList();
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
        ObservableList<UserModel> currentCoordinators = eventAssignModel.eventItemModel().coordinatorsProperty();

        EventItemModel currentEvent = eventAssignModel.eventItemModel();
        List<UserModel> removedUsers = getRemovedUsers(currentCoordinators, selectedUsers);
        List<UserModel> addedUsers = selectedUsers.stream().toList();

        eventInteractor.changeCoordinatorsForEvent(currentEvent, addedUsers, removedUsers);

        ModalHandler.getInstance().hideModal();
    }

    public List<UserModel> getRemovedUsers(ObservableList<UserModel> currentCoordinators, ObservableList<UserModel> selectedUsers) {
        List<UserModel> removedUsers = new ArrayList<>();

        for (UserModel coordinator : currentCoordinators) {
            if (!selectedUsers.contains(coordinator)) {
                removedUsers.add(coordinator);
            }
        }

        return removedUsers;
    }
}

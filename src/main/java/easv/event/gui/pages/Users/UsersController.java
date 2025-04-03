package easv.event.gui.pages.Users;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import easv.event.gui.MainModel;
import easv.event.gui.common.UserModel;
import easv.event.enums.UserRole;
import easv.event.gui.interactors.UserInteractor;
import easv.event.gui.modals.Modal;
import easv.event.gui.pages.IPageController;
import easv.event.gui.utils.ButtonStyle;
import easv.event.gui.utils.DialogHandler;
import easv.event.gui.utils.ModalHandler;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class UsersController implements Initializable, IPageController {
    private final UserInteractor userInteractor = MainModel.getInstance().getUserInteractor();
    private final UsersModel model = userInteractor.getUsersModel();
    private SortedList<UserModel> sortedUserList;
    private FilteredList<UserModel> filteredUserList;

    @FXML
    private TableView<UserModel> tblViewUsers;

    @FXML
    private TableColumn<UserModel, String> tblColName;

    @FXML
    private TableColumn<UserModel, String> tblColEmail;

    @FXML
    private TableColumn<UserModel, UserRole> tblColPosition;

    @FXML
    private TableColumn<UserModel, String> tblColLocation;

    @FXML
    private CustomTextField txtFieldSearch;

    @FXML
    private Button btnAddNewUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Tooltip addToolTip = new Tooltip("Tilføj ny bruger");
        btnAddNewUser.setTooltip(addToolTip);

        txtFieldSearch.setLeft(new FontIcon(Feather.SEARCH));
        ButtonStyle.setPrimary(btnAddNewUser, new FontIcon(Feather.PLUS));

        btnAddNewUser.setOnAction(event -> ModalHandler.getInstance().getModalOverlay().showFXML(Modal.USER_ADD_NEW));

        initializeFilteredList();
        setupTextFieldSearch();
        setTableData();
    }

    @Override
    public void load() {
        userInteractor.initialize();
    }

    private void setupTextFieldSearch() {
        txtFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredUserList.setPredicate(eventItemModel -> {
                if (newValue == null || newValue.isEmpty())
                    return true;

                String lowerCaseFilter = newValue.toLowerCase();
                return eventItemModel.firstNameProperty().get().toLowerCase().contains(lowerCaseFilter) ||
                        eventItemModel.lastNameProperty().get().toLowerCase().contains(lowerCaseFilter) ||
                        eventItemModel.emailProperty().get().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void initializeFilteredList() {
        filteredUserList = new FilteredList<>(model.getSortedUserModelList(), p -> true);
        sortedUserList = new SortedList<>(filteredUserList);

        sortedUserList.comparatorProperty().bind(tblViewUsers.comparatorProperty());

        tblViewUsers.setItems(sortedUserList);
    }

    private void setTableData() {
        Label lblNoUsers = new Label("Ingen brugere fundet");
        lblNoUsers.getStyleClass().add(Styles.TEXT_SUBTLE);

        tblViewUsers.setPlaceholder(lblNoUsers);

        tblColName.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty().concat(" ").concat(cellData.getValue().lastNameProperty()));
         tblColEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
         tblColPosition.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
         tblColLocation.setCellValueFactory(cellData -> cellData.getValue().locationProperty());

        TableColumn<UserModel, Void> tblColActions = new TableColumn<>("");

        tblColActions.setCellFactory(col -> {
            TableCell<UserModel, Void> cell = new TableCell<>() {
                private final Button btnEdit = new Button(null, new FontIcon(Feather.EDIT));
                private final Button btnDelete = new Button(null, new FontIcon(Feather.TRASH));

                private final HBox hBox = new HBox(10, btnEdit, btnDelete);

                {
                    Tooltip editToolTip = new Tooltip("Rediger");
                    Tooltip deleteToolTip = new Tooltip("Slet");

                    btnEdit.setTooltip(editToolTip);
                    btnDelete.setTooltip(deleteToolTip);

                    btnEdit.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ACCENT, Styles.FLAT);
                    btnDelete.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.DANGER, Styles.FLAT);

                    btnEdit.setOnAction(event -> {
                        UserModel item = getTableRow().getItem();
                        if (item != null) {
                            MainModel.getInstance().getUserEditModel().userModelProperty().set(item);
                            ModalHandler.getInstance().getModalOverlay().showFXML(Modal.USER_EDIT);
                        }
                    });

                    btnDelete.setOnAction(event -> {
                        UserModel item = getTableRow().getItem();
                        if (item != null) {
                            if (item.roleProperty().get() == UserRole.COORDINATOR) {
                                // For koordinatorer
                                DialogHandler.showConfirmationDialog(
                                        "Slet koordinator",
                                        "Bekræft slettelse af koordinator " + item.firstNameProperty().get() + " " + item.lastNameProperty().get(),
                                        "Bemærk, hvis du fjerner denne koordinator, vil alle data fra koordinatoren blive slettet.\n\nEr du sikker på at du vil fortsætte?",
                                        () -> userInteractor.deleteCoordinator(item));
                            } else {
                                // For almindelige brugere
                                // forstår ikke lige den her, så skal i have en ny metode??
                                DialogHandler.showConfirmationDialog(
                                        "Slet bruger",
                                        "Bekræft slettelse af konti " + item.firstNameProperty().get() + " " + item.lastNameProperty().get(),
                                        "Bemærk, hvis du fjerner denne konti, vil alle data fra brugeren blive slettet.\n\nEr du sikker på at du vil fortsætte?",
                                        () -> MainModel.getInstance().getUsersModel().deleteUser(item));
                            }
                        }
                    });

                    HBox.setHgrow(hBox, Priority.ALWAYS);
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(hBox);
                    }
                }
            };
            return cell;
        });

        tblViewUsers.getColumns().add(tblColActions);
    }
}

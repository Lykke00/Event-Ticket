package easv.event.gui.pages.Event;

import atlantafx.base.controls.Card;
import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import easv.event.enums.UserRole;
import easv.event.gui.MainModel;
import easv.event.gui.common.AuthModel;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.common.UserModel;
import easv.event.gui.interactors.AuthInteractor;
import easv.event.gui.interactors.EventInteractor;
import easv.event.gui.modals.Modal;
import easv.event.gui.pages.IPageController;
import easv.event.gui.pages.Pages;
import easv.event.gui.utils.*;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class EventController implements Initializable, IPageController {
    private final EventInteractor eventInteractor = MainModel.getInstance().getEventInteractor();
    private final AuthModel authModel = MainModel.getInstance().getAuthInteractor().getAuthModel();

    private final EventModel model = eventInteractor.getEventModel();

    private SortedList<EventItemModel> sortedEventsList;
    private FilteredList<EventItemModel> filteredEventsList;

    private int totalEvents = 0;
    private int finishedEvents = 0;
    private int upcomingEvents = 0;

    private final static String ALL_EVENTS = "Alle events";
    private final static String ACTIVE_EVENTS = "Aktive events";
    private final static String INACTIVE_EVENTS = "Inaktive events";

    @FXML
    private TableView<EventItemModel> tblViewEvents;

    @FXML
    private TableColumn<EventItemModel, String> tblColName;

    @FXML
    private TableColumn<EventItemModel, String> tblColLocation;

    @FXML
    private TableColumn<EventItemModel, String> tblColTime;

    @FXML
    private TableColumn<EventItemModel, String> tblColSoldTickets;

    @FXML
    private Card cardTotalTickets, cardUpcomingEvents, cardCompletedEvents;

    @FXML
    private CustomTextField txtFieldSearch;

    @FXML
    private Button btnAddNewEvent;

    @FXML
    private ComboBox<String> cmbBoxEvents;

    public EventController() {}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ButtonStyle.setPrimary(btnAddNewEvent, new FontIcon(Feather.PLUS));
        txtFieldSearch.setLeft(new FontIcon(Feather.SEARCH));

        Tooltip createEventToolTip = new Tooltip("Opret nyt event");
        btnAddNewEvent.setTooltip(createEventToolTip);

        loadSortEventBox();

        //TODO: User permission, tilføj tilbage når alt er lavet
        userPermissionView();

        setCardDetails();
        updateTableView();
        createTableRowClick();
        initializeFilteredList();
        setupTextFieldSearch();

        authModel.userProperty().addListener(new ChangeListener<UserModel>() {
            @Override
            public void changed(ObservableValue<? extends UserModel> observable, UserModel oldValue, UserModel newValue) {
                if (newValue != null) {
                    eventInteractor.initialize();
                    updateTableView();
                    btnAddNewEvent.visibleProperty().bind(
                            authModel.userProperty().get().roleProperty().isEqualTo(UserRole.COORDINATOR)
                    );
                }
            }
        });
    }

    @Override
    public void load() {
        eventInteractor.initialize();

      //  btnAddNewEvent.visibleProperty().bind(
     //           authModel.userProperty().get().roleProperty().isEqualTo(UserRole.COORDINATOR)
     //   );
    }

    private void loadSortEventBox() {
        cmbBoxEvents.getItems().addAll(ALL_EVENTS, ACTIVE_EVENTS, INACTIVE_EVENTS);
        cmbBoxEvents.setValue(ACTIVE_EVENTS);

        eventInteractor.getEventModel().currentListProperty().bind(cmbBoxEvents.valueProperty());
    }

    private void userPermissionView() {
        btnAddNewEvent.visibleProperty().bind(
                authModel.userProperty().get().roleProperty().isEqualTo(UserRole.COORDINATOR)
        );
    }

    private void userPermissionViewControls(HBox hBox, Button btnEdit) {
        boolean isAdmin = authModel.userProperty().get().roleProperty().get().equals(UserRole.ADMIN);
        if (isAdmin)
            hBox.getChildren().remove(btnEdit);
    }

    private void setupTextFieldSearch() {
        txtFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredEventsList.setPredicate(eventItemModel -> {
                if (newValue == null || newValue.isEmpty())
                    return true;

                String lowerCaseFilter = newValue.toLowerCase();
                return eventItemModel.nameProperty().get().toLowerCase().contains(lowerCaseFilter) ||
                        eventItemModel.locationProperty().get().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void initializeFilteredList() {
        filteredEventsList = new FilteredList<>(model.getSortedEventsList(), p -> true);
        sortedEventsList = new SortedList<>(filteredEventsList);

        sortedEventsList.comparatorProperty().bind(tblViewEvents.comparatorProperty());

        tblViewEvents.setItems(sortedEventsList);
    }


    private void goToEventItemPage(EventItemModel eventItemModel) {
        eventInteractor.getCoordinatorsForEvent(eventItemModel);
        PageHandler.getInstance().setCurrentPage(Pages.EVENT_ITEM_PAGE);
        MainModel.getInstance().getEventItemModel().eventItemModelProperty().set(eventItemModel);
    }

    private void editEventItem(EventItemModel eventItemModel) {
        MainModel.getInstance().getEditEventModel().eventItemModelProperty().set(eventItemModel);
        ModalHandler.getInstance().getModalOverlay().showFXML(Modal.EVENT_EDIT);
    }

    private void updateRowStyle(TableRow<EventItemModel> row, EventItemModel item, boolean red) {
        if (item.activeProperty().get()) {
            row.setStyle(""); // Standard styling
        } else {
            row.setStyle("-fx-background-color: rgba(255, 0, 0, 0.2);"); // Svag rød baggrund
        }
    }

    private void updateTableView() {
        Label lblNoEvents = new Label("Ingen events fundet");
        lblNoEvents.getStyleClass().add(Styles.TEXT_SUBTLE);

        tblViewEvents.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(EventItemModel item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle(""); // Nulstil styling
                } else {
                    // Dynamisk binding til activeProperty()
                    eventInteractor.getEventModel().currentListProperty().addListener((observable, oldValue, newValue) -> {
                        updateRowStyle(this, item, newValue.equals("Alle events"));
                    });
                    //   item.activeProperty().addListener((obs, oldVal, newVal) -> {
                    //      updateRowStyle(this, item);
                    //  });
                    updateRowStyle(this, item, false); // Opdater første gang
                }
            }
        });


        tblViewEvents.setPlaceholder(lblNoEvents);

        tblColName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tblColLocation.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        tblColTime.setCellValueFactory(cellData ->
                Bindings.createStringBinding(
                        () -> DateFormatter.createDateTimeString(
                                cellData.getValue().dateProperty().get(),
                                cellData.getValue().timeProperty().get()
                        ),
                        cellData.getValue().dateProperty(),
                        cellData.getValue().timeProperty()
                )
        );

        tblColSoldTickets.setCellValueFactory(cellData ->
                Bindings.createStringBinding(
                        () -> cellData.getValue().soldTicketsProperty().get() + " billetter",
                        cellData.getValue().soldTicketsProperty()
                )
        );

        TableColumn<EventItemModel, Void> tblColActions = new TableColumn<>("");

        tblColActions.setCellFactory(col -> {
            TableCell<EventItemModel, Void> cell = new TableCell<>() {
                private final Button btnView = new Button(null, new FontIcon(Feather.EYE));
                private final Button btnAssign = new Button(null, new FontIcon(Feather.USER_PLUS));
                private final Button btnEdit = new Button(null, new FontIcon(Feather.EDIT));
                private final Button btnDelete = new Button(null, new FontIcon(Feather.TRASH));

                private final HBox hBox = new HBox(10, btnView, btnAssign, btnEdit, btnDelete);
                {
                    Tooltip showToolTip = new Tooltip("Vis");
                    Tooltip assignToolTip = new Tooltip("Tildel");
                    Tooltip editToolTip = new Tooltip("Rediger");
                    Tooltip deleteToolTip = new Tooltip("Slet");

                    btnView.setTooltip(showToolTip);
                    btnAssign.setTooltip(assignToolTip);
                    btnEdit.setTooltip(editToolTip);
                    btnDelete.setTooltip(deleteToolTip);

                    btnView.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ACCENT, Styles.FLAT);
                    btnAssign.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ACCENT, Styles.FLAT);
                    btnEdit.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ACCENT, Styles.FLAT);

                    btnView.setOnAction(event -> {
                        EventItemModel item = getTableRow().getItem();
                        if (item != null)
                            goToEventItemPage(item);
                    });

                    btnAssign.setOnAction(event -> {
                        EventItemModel item = getTableRow().getItem();
                        if (item != null) {
                            MainModel.getInstance().getEventAssignModel().setEventModelProperty(item);
                            ModalHandler.getInstance().getModalOverlay().showFXML(Modal.EVENT_ASSIGN);
                        }
                    });

                    btnEdit.setOnAction(event -> {
                        EventItemModel item = getTableRow().getItem();
                        if (item != null)
                            editEventItem(item);
                    });

                    btnDelete.setOnAction(event -> {
                        EventItemModel item = getTableRow().getItem();
                        if (item != null) {
                            boolean active = item.activeProperty().get();
                            String eventName = item.nameProperty().get();
                            String title = active ? "Bekræft slet af Event" : "Bekræft genaktivering af Event";
                            String headerText = active ? "Bekræft slet af " + eventName : "Bekræft genaktivering af " + eventName;
                            String message = active ? "Bemærk, hvis du sletter dette, er eventet \"" + item.nameProperty().get() + "\" væk for altid. \n\nEr du sikker på at du vil fortsætte?" : "Bemærk, hvis du genaktiverer dette, vil eventet \"" + item.nameProperty().get() + "\" blive aktiv igen. \n\nEr du sikker på at du vil fortsætte?";
                            DialogHandler.showConfirmationDialog(
                                            title,
                                            headerText,
                                            message,
                                            () -> {
                                                eventInteractor.changeEventStatus(item, wasChanged -> {
                                                    if (wasChanged) {
                                                        tblViewEvents.refresh();
                                                    }
                                                });
                                            }
                            );
                        }});

                    HBox.setHgrow(hBox, Priority.ALWAYS);
                    hBox.setAlignment(Pos.CENTER_RIGHT);

                    //TODO: User permission, tilføj tilbage når alt er lavet
                    userPermissionViewControls(hBox, btnEdit);
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                        return;
                    }

                    EventItemModel eventItem = getTableRow().getItem();

                    btnDelete.getStyleClass().removeIf(style ->
                            style.equals(Styles.DANGER) ||
                                    style.equals(Styles.SUCCESS) ||
                                    style.equals(Styles.BUTTON_ICON) ||
                                    style.equals(Styles.FLAT)
                    );

                    // Update button based on activeProperty
                    if (!eventItem.activeProperty().get()) {
                        btnDelete.setGraphic(new FontIcon(Feather.PLUS));
                        btnDelete.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.SUCCESS, Styles.FLAT);
                    } else {
                        btnDelete.setGraphic(new FontIcon(Feather.TRASH));
                        btnDelete.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.DANGER, Styles.FLAT);
                    }

                    setGraphic(hBox);
                }
            };
            return cell;
        });

        tblViewEvents.getColumns().add(tblColActions);

        tblColName.setSortable(true);
        tblColLocation.setSortable(true);
        tblColTime.setSortable(true);
    }

    private void createTableRowClick() {
        tblViewEvents.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                EventItemModel selectedItem = tblViewEvents.getSelectionModel().getSelectedItem();

                if (selectedItem != null)
                    goToEventItemPage(selectedItem);
            }
        });
    }

    private Tile createDetailsBox(String title, String value, FontIcon icon, Color color) {
        var stack = new StackPane();
        var circle = new Circle(20, 20, 20);
        circle.setFill(color);
        stack.getChildren().add(circle);
        stack.getChildren().add(icon);

        return new Tile(title, value, stack);
    }

    private void setCardDetails() {
        model.eventsListProperty().addListener((ListChangeListener<EventItemModel>) change -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved()) {
                    updateCounts();
                    updateCardDetails();
                }
            }
        });

        updateCounts();
        updateCardDetails();
    }

    private void updateCounts() {
        totalEvents = model.getSortedEventsList().size();
        finishedEvents = model.getCompletedEventsList().size();
        upcomingEvents = model.getUpcomingEventsList().size();
    }

    private void updateCardDetails() {
        var fontIcon = new FontIcon(Feather.BOOK);
        cardTotalTickets.setHeader(createDetailsBox(
                "Events ialt",
                totalEvents + "",
                fontIcon,
                new Color(0.7, 0.7, 0.7, 1)
        ));

        var completedIcon = new FontIcon(Feather.CHECK_CIRCLE);
        cardCompletedEvents.setHeader(createDetailsBox(
                "Færdige Events",
                finishedEvents + "",
                completedIcon,
                new Color(0.2, 0.4, 0.6, 1)
        ));

        var upcomingIcon = new FontIcon(Feather.CALENDAR);
        cardUpcomingEvents.setHeader(createDetailsBox(
                "Upcoming Events",
                upcomingEvents + "",
                upcomingIcon,
                new Color(0.2, 0.6, 0.3, 1)
        ));
    }

    @FXML
    public void btnActionAddNewEvent(ActionEvent actionEvent) {
        ModalHandler.getInstance().getModalOverlay().showFXML(Modal.EVENT_ADD_NEW);
    }
}
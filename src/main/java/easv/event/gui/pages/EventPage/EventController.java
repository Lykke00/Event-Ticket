package easv.event.gui.pages.EventPage;

import atlantafx.base.controls.Card;
import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import easv.event.gui.MainModel;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.modals.Modal;
import easv.event.gui.pages.EventItemPage.EventItemPageController;
import easv.event.gui.utils.ButtonStyle;
import easv.event.gui.utils.ModalHandler;
import easv.event.gui.utils.PageHandler;
import easv.event.gui.utils.Pages;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EventController implements Initializable {
    private final EventModel model = MainModel.getInstance().getEventModel();

    @FXML
    private TableView<EventItemModel> tblViewEvents;

    @FXML
    private TableColumn<EventItemModel, String> tblColName;

    @FXML
    private TableColumn<EventItemModel, String> tblColLocation;

    @FXML
    private TableColumn<EventItemModel, String> tblColTime;

    @FXML
    private TableColumn<EventItemModel, IntegerProperty> tblColSoldTickets;

    @FXML
    private Card cardTotalTickets, cardUpcomingEvents, cardCompletedEvents;

    @FXML
    private CustomTextField txtFieldSearch;

    @FXML
    private Button btnAddNewEvent;

    public EventController() {}

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ButtonStyle.setPrimary(btnAddNewEvent, new FontIcon(Feather.PLUS));
        txtFieldSearch.setLeft(new FontIcon(Feather.SEARCH));

        Tooltip createEventToolTip = new Tooltip("Opret nyt event");
        btnAddNewEvent.setTooltip(createEventToolTip);

        setCardDetails();
        updateTableView();
        createTableRowClick();
    }

    private void goToEventItemPage(EventItemModel eventItemModel) {
        Pages page = Pages.EVENT_ITEM_PAGE;

        PageHandler.getInstance().setCurrentPage(page);
        MainModel.getInstance().getEventItemModel().eventItemModelProperty().set(eventItemModel);
    }

    private void updateTableView() {
        SortedList<EventItemModel> sortedEventsList = model.getSortedEventsList();

       // tblViewEvents.comparatorProperty().
        tblViewEvents.setItems(sortedEventsList);

        tblColName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tblColLocation.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        tblColTime.setCellValueFactory(cellData -> {
            String dateString = cellData.getValue().dateProperty().get();
            String timeString = cellData.getValue().timeProperty().get();

            LocalDate date = LocalDate.parse(dateString);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d. MMMM");
            String formattedDate = date.format(formatter);

            return new SimpleStringProperty(formattedDate + " " + timeString);
        });

        TableColumn<EventItemModel, Void> tblColActions = new TableColumn<>("");

        tblColActions.setCellFactory(col -> {
            TableCell<EventItemModel, Void> cell = new TableCell<>() {
                private final Button btnView = new Button(null, new FontIcon(Feather.EYE));
                private final Button btnEdit = new Button(null, new FontIcon(Feather.EDIT));
                private final Button btnDelete = new Button(null, new FontIcon(Feather.TRASH));

                private final HBox hBox = new HBox(10, btnView, btnEdit, btnDelete);

                {
                    Tooltip showToolTip = new Tooltip("Vis");
                    Tooltip editToolTip = new Tooltip("Rediger");
                    Tooltip deleteToolTip = new Tooltip("Slet");

                    btnView.setTooltip(showToolTip);
                    btnEdit.setTooltip(editToolTip);
                    btnDelete.setTooltip(deleteToolTip);

                    btnView.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ACCENT, Styles.FLAT);
                    btnEdit.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ACCENT, Styles.FLAT);
                    btnDelete.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.DANGER, Styles.FLAT);

                    btnView.setOnAction(event -> {
                        EventItemModel item = getTableRow().getItem();
                        if (item != null)
                            goToEventItemPage(item);
                    });

                    btnEdit.setOnAction(event -> {
                        EventItemModel item = getTableRow().getItem();
                        if (item != null) {
                            System.out.println("Edit: " + item.nameProperty().get());
                        }
                    });

                    btnDelete.setOnAction(event -> {
                        EventItemModel item = getTableRow().getItem();
                        if (item != null) {
                            System.out.println("Delete: " + item.nameProperty().get());
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

        tblViewEvents.getColumns().add(tblColActions);
    }

    private void createTableRowClick() {
        tblViewEvents.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                EventItemModel selectedItem = tblViewEvents.getSelectionModel().getSelectedItem();

                if (selectedItem != null) {
                    goToEventItemPage(selectedItem);
                }
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
        var fontIcon = new FontIcon(Feather.BOOK);
        cardTotalTickets.setHeader(createDetailsBox(
                "Total Events",
                "100",
                fontIcon,
                new Color(0.7, 0.7, 0.7, 1)
        ));

        var completedIcon = new FontIcon(Feather.CHECK_CIRCLE);
        cardCompletedEvents.setHeader(createDetailsBox(
                "Færdige Events",
                "203",
                completedIcon,
                new Color(0.2, 0.4, 0.6, 1)
        ));

        var upcomingIcon = new FontIcon(Feather.CALENDAR);
        cardUpcomingEvents.setHeader(createDetailsBox(
                "Upcoming Events",
                "2",
                upcomingIcon,
                new Color(0.2, 0.6, 0.3, 1)
        ));
    }

    @FXML
    public void btnActionAddNewEvent(ActionEvent actionEvent) {
        ModalHandler.getInstance().getModalOverlay().showFXML(Modal.ADDNEWEVENT.getPath());
    }
}

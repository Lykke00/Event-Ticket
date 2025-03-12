package easv.event.gui.pages.Ticket.ItemView;

import atlantafx.base.controls.Card;
import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import easv.event.gui.MainModel;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.common.TicketEventItemModel;
import easv.event.gui.common.TicketItemModel;
import easv.event.gui.modals.Modal;
import easv.event.gui.pages.Pages;
import easv.event.gui.utils.DialogHandler;
import easv.event.gui.utils.ModalHandler;
import easv.event.gui.utils.PageHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class TicketItemViewController implements Initializable {

    @FXML
    private Card cardTicketInfo;

    @FXML
    private VBox contentBox;

    @FXML
    private TableView<TicketEventItemModel> tblViewEvents;

    @FXML
    private TableColumn<TicketEventItemModel, String> tblColEventName;

    @FXML
    private TableColumn<TicketEventItemModel, String> tblColTicketPrice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObjectProperty<TicketItemModel> ticketItemModel = MainModel.getInstance().getTicketItemViewModel().ticketItemModelProperty();

        MainModel.getInstance().getTicketItemViewModel().ticketItemModelProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                bindUI(newValue);
            }
        });

        bindUI(ticketItemModel.get());
        addTableViewActions();
    }

    private void bindUI(TicketItemModel ticketItemModel) {
        VBox cardContent = new VBox(5);

        Label titleLabel = new Label();
        titleLabel.textProperty().bindBidirectional(ticketItemModel.nameProperty());
        titleLabel.getStyleClass().add(Styles.TITLE_3);

        Label descriptionLabel = new Label();
        StringBinding titleDescBinding = Bindings.createStringBinding(() -> {
            String type = ticketItemModel.typeProperty().get() != null ? ticketItemModel.typeProperty().get().getType() : "Ukendt dato";
            return String.format("Type: %s", type);
        }, ticketItemModel.typeProperty());

        descriptionLabel.textProperty().bind(titleDescBinding);
        descriptionLabel.getStyleClass().add(Styles.TEXT_SUBTLE);

        cardTicketInfo.setHeader(cardContent);

        Button btnEdit = new Button(null, new FontIcon(Feather.EDIT));
        Button btnAddEvent = new Button(null, new FontIcon(Feather.PLUS_SQUARE));
        Button btnDelete = new Button(null, new FontIcon(Feather.TRASH));

        Tooltip editToolTip = new Tooltip("Rediger");
        Tooltip addToolTip = new Tooltip("Tilknyt Event");
        Tooltip deleteToolTip = new Tooltip("Slet");

        btnEdit.setTooltip(editToolTip);
        btnAddEvent.setTooltip(addToolTip);
        btnDelete.setTooltip(deleteToolTip);

        btnEdit.setOnAction(event -> editTicketItem(ticketItemModel));
        btnAddEvent.setOnAction(event -> ModalHandler.getInstance().getModalOverlay().showFXML(Modal.TICKET_ADD_EVENT));
        btnDelete.setOnAction(event -> DialogHandler.showConfirmationDialog(
                "Bekræft slet Billet",
                "Bekræft slet af " + ticketItemModel.nameProperty().get(),
                "Bemærk, hvis du sletter dette, er billetten \"" + ticketItemModel.nameProperty().get() + "\" væk for altid. \n\nEr du sikker på at du vil fortsætte?",
                () -> {
                    MainModel.getInstance().getTicketModel().deleteTicket(ticketItemModel);
                    PageHandler.getInstance().setCurrentPage(Pages.TICKETS);
                })
        );

        btnAddEvent.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ACCENT, Styles.FLAT);
        btnEdit.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ACCENT, Styles.FLAT);
        btnDelete.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.DANGER, Styles.FLAT);

        HBox controlsBox = new HBox(btnAddEvent, btnEdit, btnDelete);
        controlsBox.setAlignment(Pos.CENTER_RIGHT);
        controlsBox.translateYProperty().setValue(-5);

        HBox hBox = new HBox(0);
        hBox.getChildren().addAll(titleLabel, new Spacer(), controlsBox);

        cardContent.getChildren().addAll(hBox, descriptionLabel);

        cardTicketInfo.setMaxHeight(90);
        cardTicketInfo.setMinHeight(90);
        cardTicketInfo.setPrefHeight(90);

        contentBox.setPrefHeight(Control.USE_COMPUTED_SIZE);
        contentBox.setPrefWidth(Control.USE_COMPUTED_SIZE);

        setTableData(ticketItemModel);
    }

    private void setTableData(TicketItemModel ticketItemModel) {
        Label lblNoEvents = new Label("Ingen events fundet");
        lblNoEvents.getStyleClass().add(Styles.TEXT_SUBTLE);

        tblViewEvents.setPlaceholder(lblNoEvents);

        tblViewEvents.setItems(ticketItemModel.getTicketEventItemModels());
        tblColEventName.setCellValueFactory(cellData -> cellData.getValue().eventNameProperty());
        tblColTicketPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asString());

        tblViewEvents.setRowFactory(tv -> new TableRow<TicketEventItemModel>() {
            @Override
            protected void updateItem(TicketEventItemModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setContextMenu(null);
                } else {
                    setContextMenu(createContextMenu(item));
                }
            }
        });

        createTableRowClick();
    }

    private void addTableViewActions() {
        TableColumn<TicketEventItemModel, Void> tblColActions = new TableColumn<>("");

        tblColActions.setCellFactory(col -> {
            TableCell<TicketEventItemModel, Void> cell = new TableCell<>() {
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
                        TicketEventItemModel item = getTableRow().getItem();
                        if (item != null)
                            goToEvent(item);
                    });

                    btnEdit.setOnAction(event -> {
                        TicketEventItemModel item = getTableRow().getItem();
                        if (item != null) {
                            MainModel.getInstance().getTicketEditEventModel().ticketEventItemModelProperty().set(item);
                            ModalHandler.getInstance().getModalOverlay().showFXML(Modal.TICKET_EDIT_EVENT);
                        }
                    });

                    btnDelete.setOnAction(event -> {
                        TicketEventItemModel item = getTableRow().getItem();
                        if (item != null) {
                            DialogHandler.showConfirmationDialog(
                                    "Fjern event fra billet",
                                    "Bekræft fjernelse af billet fra " + item.eventNameProperty().get(),
                                    "Bemærk, hvis du fjerner dette, vil alle købte billetter blive slettet fra \"" + item.eventNameProperty().get() + "\".\n\nEr du sikker på at du vil fortsætte?",
                                    () -> MainModel.getInstance().getTicketItemViewModel().ticketItemModelProperty().get().getTicketEventItemModels().remove(item));

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
                TicketEventItemModel selectedItem = tblViewEvents.getSelectionModel().getSelectedItem();

                if (selectedItem != null) {
                    goToEvent(selectedItem);
                }
            }
        });
    }

    private ContextMenu createContextMenu(TicketEventItemModel item) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem goToEventItem = createItem("Gå til begivenhed", Feather.EYE, KeyCombination.keyCombination("Ctrl+G"));
        goToEventItem.setOnAction(event -> goToEvent(item));

        contextMenu.getItems().add(goToEventItem);

        return contextMenu;
    }

    private void editTicketItem(TicketItemModel ticketItemModel) {
        MainModel.getInstance().getEditTicketModel().ticketItemModelProperty().set(ticketItemModel);
        ModalHandler.getInstance().getModalOverlay().showFXML(Modal.TICKET_EDIT);
    }

    private void goToEvent(TicketEventItemModel item) {
        EventItemModel eventItemModel = MainModel.getInstance().getEventFromId(item.eventIdProperty().get());

        if (eventItemModel == null)
            return;

        PageHandler.getInstance().setCurrentPage(Pages.EVENT_ITEM_PAGE);
        MainModel.getInstance().getEventItemModel().eventItemModelProperty().set(eventItemModel);
    }

    private MenuItem createItem(String text,
                                Ikon graphic,
                                KeyCombination accelearator) {
        var item = new MenuItem(text);

        if (graphic != null)
            item.setGraphic(new FontIcon(graphic));

        if (accelearator != null)
            item.setAccelerator(accelearator);

        return item;
    }
}

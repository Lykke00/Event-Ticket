package easv.event.gui.pages.Ticket;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import easv.event.gui.MainModel;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.common.TicketItemModel;
import easv.event.gui.common.TicketTypeItemModel;
import easv.event.gui.interactors.TicketInteractor;
import easv.event.gui.modals.Modal;
import easv.event.gui.pages.Pages;
import easv.event.gui.utils.ButtonStyle;
import easv.event.gui.utils.DialogHandler;
import easv.event.gui.utils.ModalHandler;
import easv.event.gui.utils.PageHandler;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
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

public class TicketController implements Initializable {
    private final TicketInteractor ticketInteractor = MainModel.getInstance().getTicketInteractor();

    private final static String TICKETS = "Billetter";
    private final static String TICKET_TYPES = "Billet typer";

    private SortedList<TicketItemModel> sortedTicketList;
    private FilteredList<TicketItemModel> filteredTicketList;

    private SortedList<TicketTypeItemModel> sortedTicketListTypes;
    private FilteredList<TicketTypeItemModel> filteredTicketListTypes;

    @FXML
    private ComboBox<String> comboBoxSort;

    @FXML
    private TableView<TicketTypeItemModel> tblViewTicketTypes;

    @FXML
    private TableColumn<TicketTypeItemModel, String> tblColTicketTypeName;

    @FXML
    private TableView<TicketItemModel> tblViewTickets;

    @FXML
    private TableColumn<TicketItemModel, String> tblColName;

    @FXML
    private TableColumn<TicketItemModel, String> tblColType;

    @FXML
    private CustomTextField txtFieldSearch;

    @FXML
    private MenuButton menuBtnNewTicket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupAddMenuButton();
        setupComboBoxSort();
        initializeFilteredList();
        initializeFilteredListTicketTypes();
        setupTextFieldSearch();
        setTableData();
        setTableDataTicketType();
    }

    private void setupAddMenuButton() {
        Tooltip addToolTip = new Tooltip("Tilføj ny Billet eller Billet Type");

        menuBtnNewTicket.setTooltip(addToolTip);
        menuBtnNewTicket.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ACCENT, Tweaks.NO_ARROW);
        menuBtnNewTicket.setGraphic(new FontIcon(Feather.PLUS));

        menuBtnNewTicket.getItems().clear();
        menuBtnNewTicket.getItems().setAll(
                new MenuItem("Ny Billet") {{
                    setOnAction(event -> ModalHandler.getInstance().getModalOverlay().showFXML(Modal.TICKET_ADD_NEW));
                }},
                new MenuItem("Ny Billet Type") {{
                    setOnAction(event -> ModalHandler.getInstance().getModalOverlay().showFXML(Modal.TICKET_ADD_NEW_TYPE));
                }}
        );
    }

    private void setupComboBoxSort() {
        comboBoxSort.getItems().addAll(TICKETS, TICKET_TYPES);
        comboBoxSort.setValue(TICKETS);

        switchTableView(); //opsæt på start fat det

        comboBoxSort.setOnAction(event -> {
            switchTableView();
        });
    }

    private void switchTableView() {
        boolean tickets = comboBoxSort.getValue().equals(TICKETS);
        boolean ticketTypes = comboBoxSort.getValue().equals(TICKET_TYPES);

        tblViewTickets.setVisible(tickets && !ticketTypes);
        tblViewTicketTypes.setVisible(!tickets && ticketTypes);

        if (ticketTypes)
            ticketInteractor.loadAllTicketTypes();

        if (tickets)
            ticketInteractor.loadAllTickets();
    }

    private void setupTextFieldSearch() {
        txtFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredTicketList.setPredicate(ticketItemModel -> {
                if (newValue == null || newValue.isEmpty())
                    return true;

                String lowerCaseFilter = newValue.toLowerCase();
                return ticketItemModel.nameProperty().get().toLowerCase().contains(lowerCaseFilter);
            });

            filteredTicketListTypes.setPredicate(ticketTypeItemModel -> {
                if (newValue == null || newValue.isEmpty())
                    return true;

                String lowerCaseFilter = newValue.toLowerCase();
                return ticketTypeItemModel.nameProperty().get().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void initializeFilteredList() {
        filteredTicketList = new FilteredList<>(ticketInteractor.getTicketModel().getSortedTicketItemModelsList(), p -> true);
        sortedTicketList = new SortedList<>(filteredTicketList);

        sortedTicketList.comparatorProperty().bind(tblViewTickets.comparatorProperty());

        tblViewTickets.setItems(sortedTicketList);
    }

    private void initializeFilteredListTicketTypes() {
        filteredTicketListTypes = new FilteredList<>(ticketInteractor.getTicketTypeModel().getSortedTicketItemModelsList(), p -> true);
        sortedTicketListTypes = new SortedList<>(filteredTicketListTypes);

        sortedTicketListTypes.comparatorProperty().bind(tblViewTicketTypes.comparatorProperty());

        tblViewTicketTypes.setItems(sortedTicketListTypes);
    }

    private void setTableData() {
        Label lblNoTickets = new Label("Ingen billetter fundet");
        lblNoTickets.getStyleClass().add(Styles.TEXT_SUBTLE);

        tblViewTickets.setPlaceholder(lblNoTickets);

        tblColName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tblColType.setCellValueFactory(cellData ->
                Bindings.createStringBinding(
                        () -> cellData.getValue().typeProperty().get().nameProperty().get(),
                        cellData.getValue().typeProperty()
                )
        );

        TableColumn<TicketItemModel, Void> tblColActions = new TableColumn<>("");

        tblColActions.setCellFactory(col -> {
            TableCell<TicketItemModel, Void> cell = new TableCell<>() {
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
                        TicketItemModel item = getTableRow().getItem();
                        if (item != null)
                            goToTicketPage(item);
                    });

                    btnEdit.setOnAction(event -> {
                        TicketItemModel item = getTableRow().getItem();
                        if (item != null)
                            editTicketItem(item);
                    });

                    btnDelete.setOnAction(event -> {
                        TicketItemModel item = getTableRow().getItem();
                        if (item != null)
                            ticketInteractor.getEventsForTicket(item, eventList -> {
                                StringBuilder deleteTicketFromEvent = new StringBuilder();

                                if (eventList != null && !eventList.isEmpty()) {
                                    deleteTicketFromEvent.append("Billetten vil blive fjernet fra følgende events:\n");
                                    for (EventItemModel eventItemModel : eventList)
                                        deleteTicketFromEvent
                                                .append("- ")
                                                .append(eventItemModel.nameProperty().get())
                                                .append("\n");
                                } else {
                                    deleteTicketFromEvent.append("Billetten vil ikke blive fjernet fra nogle events.\n");
                                }

                                DialogHandler.showConfirmationDialog(
                                        "Bekræft slet Billet",
                                        "Bekræft slet af " + item.nameProperty().get(),
                                        "Bemærk, hvis du sletter dette, er billetten \"" + item.nameProperty().get() +
                                                "\" væk for altid.\n\n" +
                                                deleteTicketFromEvent.toString() +
                                                "\nEr du sikker på at du vil fortsætte?",
                                        () -> ticketInteractor.deleteTicket(item));
                            });
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

        tblViewTickets.getColumns().add(tblColActions);
        createTableRowClick();
    }

    private void createTableRowClick() {
        tblViewTickets.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TicketItemModel selectedItem = tblViewTickets.getSelectionModel().getSelectedItem();

                if (selectedItem != null) {
                    goToTicketPage(selectedItem);
                }
            }
        });
    }

    private void goToTicketPage(TicketItemModel ticketItemModel) {
        ticketInteractor.getTicketItemViewModel().setTicketItemModel(ticketItemModel);
        PageHandler.getInstance().setCurrentPage(Pages.TICKET_ITEM_PAGE);
    }

    private void editTicketItem(TicketItemModel ticketItemModel) {
        ticketInteractor.getEditTicketModel().ticketItemModelProperty().set(ticketItemModel);
        ModalHandler.getInstance().getModalOverlay().showFXML(Modal.TICKET_EDIT);
    }

    private void editTicketTypeItem(TicketTypeItemModel ticketTypeItemModel) {
        ticketInteractor.getEditTicketTypeModel().setTicketType(ticketTypeItemModel);
        ModalHandler.getInstance().getModalOverlay().showFXML(Modal.TICKET_EDIT_TYPE);
    }

    private void setTableDataTicketType() {
        Label lblNoTicketTypes = new Label();
        lblNoTicketTypes.textProperty().bind(Bindings.when(ticketInteractor.getTicketTypeModel().loadingProperty())
                .then("Indlæser...")
                .otherwise("Ingen billet typer fundet"));

        lblNoTicketTypes.getStyleClass().add(Styles.TEXT_SUBTLE);
        tblViewTicketTypes.setPlaceholder(lblNoTicketTypes);

        tblColTicketTypeName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<TicketTypeItemModel, Void> tblColActions = new TableColumn<>("");

        tblColActions.setCellFactory(col -> {
            TableCell<TicketTypeItemModel, Void> cell = new TableCell<>() {
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
                        TicketTypeItemModel item = getTableRow().getItem();
                        if (item != null)
                            editTicketTypeItem(item);
                    });

                    btnDelete.setOnAction(event -> {
                        TicketTypeItemModel item = getTableRow().getItem();
                        if (item != null) {
                            ticketInteractor.getTicketsByType(item, ticketList -> {
                                StringBuilder ticketsToDeleteMessage = new StringBuilder();

                                if (ticketList != null && !ticketList.isEmpty()) {
                                    ticketsToDeleteMessage.append("Følgende underliggende billetter vil blive slettet:\n");
                                    for (TicketItemModel ticket : ticketList)
                                        ticketsToDeleteMessage
                                                .append("- ")
                                                .append(ticket.nameProperty().get())
                                                .append("\n");
                                } else {
                                    ticketsToDeleteMessage.append("Ingen underliggende billetter vil blive slettet.\n");
                                }

                                DialogHandler.showConfirmationDialog(
                                        "Bekræft slet Billet type",
                                        "Bekræft slet af " + item.nameProperty().get(),
                                        "Bemærk, hvis du sletter dette, er billet typen \"" + item.nameProperty().get() +
                                                "\" væk for altid.\n\n" +
                                                ticketsToDeleteMessage.toString() +
                                                "\nEr du sikker på at du vil fortsætte?",
                                        () -> ticketInteractor.deleteTicketType(item, ticketList));
                            });
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

        tblViewTicketTypes.getColumns().add(tblColActions);
    }

    // test
    @FXML
    private void btnActionAddNewTicket(ActionEvent actionEvent) {
        ModalHandler.getInstance().getModalOverlay().showFXML(Modal.TICKET_ADD_NEW);
    }
}
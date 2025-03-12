package easv.event.gui.pages.Ticket;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import easv.event.gui.MainModel;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.common.TicketItemModel;
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
    private final TicketModel model = MainModel.getInstance().getTicketModel();
    private SortedList<TicketItemModel> sortedTicketList;
    private FilteredList<TicketItemModel> filteredTicketList;

    @FXML
    private TableView<TicketItemModel> tblViewTickets;

    @FXML
    private TableColumn<TicketItemModel, String> tblColName;

    @FXML
    private TableColumn<TicketItemModel, String> tblColType;

    @FXML
    private TableColumn<TicketItemModel, Number> tblColEvents;

    @FXML
    private CustomTextField txtFieldSearch;

    @FXML
    private Button btnAddNewTicket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Tooltip addToolTip = new Tooltip("Tilføj ny Billet");
        btnAddNewTicket.setTooltip(addToolTip);

        ButtonStyle.setPrimary(btnAddNewTicket, new FontIcon(Feather.PLUS));
        txtFieldSearch.setLeft(new FontIcon(Feather.SEARCH));

        initializeFilteredList();
        setupTextFieldSearch();
        setTableData();
    }

    private void setupTextFieldSearch() {
        txtFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredTicketList.setPredicate(eventItemModel -> {
                if (newValue == null || newValue.isEmpty())
                    return true;

                String lowerCaseFilter = newValue.toLowerCase();
                return eventItemModel.nameProperty().get().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void initializeFilteredList() {
        filteredTicketList = new FilteredList<>(model.getSortedTicketItemModelsList(), p -> true);
        sortedTicketList = new SortedList<>(filteredTicketList);

        sortedTicketList.comparatorProperty().bind(tblViewTickets.comparatorProperty());

        tblViewTickets.setItems(sortedTicketList);
    }

    private void setTableData() {
        Label lblNoTickets = new Label("Ingen tickets fundet");
        lblNoTickets.getStyleClass().add(Styles.TEXT_SUBTLE);

        tblViewTickets.setPlaceholder(lblNoTickets);

        tblColName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tblColType.setCellValueFactory(cellData ->
                Bindings.createStringBinding(
                        () -> cellData.getValue().typeProperty().get().getType(),
                        cellData.getValue().typeProperty()
                )
        );

        tblColEvents.setCellValueFactory(cellData -> {
            return Bindings.size(cellData.getValue().getTicketEventItemModels());
        });

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
                            DialogHandler.showConfirmationDialog(
                                    "Bekræft slet Billet",
                                    "Bekræft slet af " + item.nameProperty().get(),
                                    "Bemærk, hvis du sletter dette, er billetten \"" + item.nameProperty().get() + "\" væk for altid. \n\nEr du sikker på at du vil fortsætte?",
                                    () -> MainModel.getInstance().getTicketModel().deleteTicket(item));
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
        MainModel.getInstance().getTicketItemViewModel().ticketItemModelProperty().set(ticketItemModel);
        PageHandler.getInstance().setCurrentPage(Pages.TICKET_ITEM_PAGE);
    }

    private void editTicketItem(TicketItemModel ticketItemModel) {
        MainModel.getInstance().getEditTicketModel().ticketItemModelProperty().set(ticketItemModel);
        ModalHandler.getInstance().getModalOverlay().showFXML(Modal.TICKET_EDIT);
    }

    @FXML
    private void btnActionAddNewTicket(ActionEvent actionEvent) {
        ModalHandler.getInstance().getModalOverlay().showFXML(Modal.TICKET_ADD_NEW);
    }
}

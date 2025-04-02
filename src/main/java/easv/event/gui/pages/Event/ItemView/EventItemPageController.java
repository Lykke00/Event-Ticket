package easv.event.gui.pages.Event.ItemView;

import atlantafx.base.controls.Card;
import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import easv.event.enums.UserRole;
import easv.event.gui.MainModel;
import easv.event.gui.common.AuthModel;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.common.UserModel;
import easv.event.gui.interactors.EventInteractor;
import easv.event.gui.modals.Modal;
import easv.event.gui.pages.Pages;
import easv.event.gui.utils.DateFormatter;
import easv.event.gui.utils.DialogHandler;
import easv.event.gui.utils.ModalHandler;
import easv.event.gui.utils.PageHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class EventItemPageController implements Initializable {
    private final EventInteractor eventInteractor = MainModel.getInstance().getEventInteractor();
    private final AuthModel authModel = MainModel.getInstance().getAuthInteractor().getAuthModel();

    private final EventItemPageModel eventItemPageModel;

    @FXML
    private ScrollPane usersScrollPane;

    @FXML
    private VBox contentBox;

    @FXML
    private Card cardEventInfo;

    @FXML
    private TextArea txtAreaDescription;

    private final StackPane stackPane = new StackPane();

    public EventItemPageController() {
        eventItemPageModel = MainModel.getInstance().getEventItemModel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Circle circle = new Circle();
        circle.setRadius(20);
        circle.setFill(new Color(0.7, 0.7, 0.7, 1));

        FontIcon fontIcon = new FontIcon(Feather.BOOK);

        eventItemPageModel.eventItemModelProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                stackPane.getChildren().clear();
                stackPane.getChildren().addAll(circle, fontIcon);

                updateUI(newValue);
            }
        });
    }

    private void userPermissionView(HBox controlsBox, Button btnEdit) {
        // hvis admin så fjern btnEdit
        boolean isAdmin = authModel.userProperty().get().roleProperty().get().equals(UserRole.ADMIN);
        if (isAdmin)
            controlsBox.getChildren().remove(btnEdit);
    }


    private void updateUI(EventItemModel eventItemModel) {
        VBox cardContent = new VBox(5);

        Label titleLabel = new Label();
        titleLabel.textProperty().bindBidirectional(eventItemModel.nameProperty());
        titleLabel.getStyleClass().add(Styles.TITLE_3);

        Label descriptionLabel = new Label();
        StringBinding titleDescBinding = Bindings.createStringBinding(() -> {
            String date = eventItemModel.dateProperty().get() != null ? DateFormatter.format(eventItemModel.dateProperty().get()) : "Ukendt dato";
            String time = eventItemModel.timeProperty().get() != null ? eventItemModel.timeProperty().get() : "Ukendt tidspunkt";
            String location = eventItemModel.locationProperty().get() != null ? eventItemModel.locationProperty().get() : "Ukendt sted";
            int soldTickets = eventItemModel.soldTicketsProperty().get();

            return String.format("Tidspunkt: %s %s%nSted: %s%nDeltagere: %d", date, time, location, soldTickets);
        }, eventItemModel.nameProperty(), eventItemModel.timeProperty(), eventItemModel.dateProperty(), eventItemModel.locationProperty(), eventItemModel.soldTicketsProperty());

        descriptionLabel.textProperty().bind(titleDescBinding);
        descriptionLabel.getStyleClass().add(Styles.TEXT_SUBTLE);

        cardEventInfo.setHeader(cardContent);
        txtAreaDescription.textProperty().bindBidirectional(eventItemModel.descriptionProperty());

        Button btnEdit = new Button(null, new FontIcon(Feather.EDIT));
        Button btnDelete = new Button(null, new FontIcon(Feather.TRASH));

        Tooltip editTooltip = new Tooltip("Rediger event");
        Tooltip deleteTooltip = new Tooltip("Slet event");

        btnEdit.setTooltip(editTooltip);
        btnDelete.setTooltip(deleteTooltip);

        btnEdit.setOnAction(event -> editEventItem(eventItemModel));
        btnDelete.setOnAction(event -> DialogHandler.showConfirmationDialog(
                "Bekræft slet Event",
                "Bekræft slet af " + eventItemModel.nameProperty().get(),
                "Bemærk, hvis du sletter dette, er eventet \"" + eventItemModel.nameProperty().get() + "\" væk for altid. \n\nEr du sikker på at du vil fortsætte?",
                () -> {
                       // eventInteractor.deleteEvent(eventItemModel);
                        PageHandler.getInstance().setCurrentPage(Pages.EVENT);
                    })
                );

        btnEdit.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.ACCENT, Styles.FLAT);
        btnDelete.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.DANGER, Styles.FLAT);

        HBox controlsBox = new HBox(btnEdit, btnDelete);

        controlsBox.setAlignment(Pos.CENTER_RIGHT);
        controlsBox.translateYProperty().setValue(-5);

        HBox hBox = new HBox(0);
        hBox.getChildren().addAll(titleLabel, new Spacer(), controlsBox);

        cardContent.getChildren().addAll(hBox, descriptionLabel);

        cardEventInfo.setMaxHeight(135);
        cardEventInfo.setMinHeight(135);
        cardEventInfo.setPrefHeight(135);

        contentBox.setPrefHeight(Control.USE_COMPUTED_SIZE);
        contentBox.setPrefWidth(Control.USE_COMPUTED_SIZE);

        eventItemModel.coordinatorsProperty().addListener((ListChangeListener<UserModel>) change -> {
            while (change.next())
                addCoordinators(eventItemModel);
        });

        //TODO: User permission, tilføj tilbage når alt er lavet
        //userPermissionView(controlsBox, btnEdit);
        addCoordinators(eventItemModel);
    }

    private void editEventItem(EventItemModel eventItemModel) {
        MainModel.getInstance().getEditEventModel().eventItemModelProperty().set(eventItemModel);
        ModalHandler.getInstance().getModalOverlay().showFXML(Modal.EVENT_EDIT);
    }

    private void addCoordinators(EventItemModel eventItemModel) {
        ObservableList<UserModel> assigned = eventItemModel.coordinatorsProperty();

        HBox usersBox = new HBox();
        usersBox.setSpacing(10);

        for (UserModel userModel : assigned) {
            FontIcon userIcon = new FontIcon(Feather.USER);

            var successOutBtn = new Button(userModel.firstNameProperty().get() + " " + userModel.lastNameProperty().get(), userIcon);
            successOutBtn.getStyleClass().addAll(
                    Styles.BUTTON_OUTLINED, Styles.BG_SUBTLE
            );
            successOutBtn.setMnemonicParsing(true);

            usersBox.getChildren().add(successOutBtn);
        }

        usersScrollPane.setContent(usersBox);

        usersScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        usersScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
}
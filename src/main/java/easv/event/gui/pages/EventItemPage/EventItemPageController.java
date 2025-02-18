package easv.event.gui.pages.EventItemPage;

import atlantafx.base.controls.Card;
import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import easv.event.gui.MainModel;
import easv.event.gui.common.EventItemModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class EventItemPageController implements Initializable {
    private final EventItemPageModel eventItemPageModel;

    @FXML
    private VBox contentBox;

    @FXML
    private Card cardEventInfo;

    @FXML
    private TextArea txtAreaDescription;

    private final StackPane stackPane = new StackPane();
    private final MenuButton accentMenuBtn = new MenuButton();
    private final MenuItem menuItemEdit = new MenuItem("Rediger");
    private final MenuItem menuItemDelete = new MenuItem("Slet");

    private final FontIcon ICON_INFO = new FontIcon(Feather.INFO);
    private final FontIcon ICON_MENU = new FontIcon(Feather.MENU);

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

        createMenuButton();
    }

    private void createMenuButton() {
        accentMenuBtn.setGraphic(ICON_MENU);
        accentMenuBtn.getItems().setAll(
                menuItemEdit,
                menuItemDelete);

        accentMenuBtn.getStyleClass().addAll(
                Styles.FLAT
        );
    }

    private void updateUI(EventItemModel eventItemModel) {
        String date = eventItemModel.dateProperty().get() != null ? eventItemModel.dateProperty().get() : "Ukendt dato";
        String time = eventItemModel.timeProperty().get() != null ? eventItemModel.timeProperty().get() : "Ukendt tidspunkt";
        String location = eventItemModel.locationProperty().get() != null ? eventItemModel.locationProperty().get() : "Ukendt sted";
        int soldTickets = eventItemModel.soldTicketsProperty().get();

        String titleDesc = String.format("Tidspunkt: %s %s%nSted: %s%nDeltagere: %d%n", date, time, location, soldTickets);

        Tile tile = new Tile(
                eventItemModel.nameProperty().get(),
                titleDesc,
                ICON_INFO);

        cardEventInfo.setHeader(tile);
        txtAreaDescription.textProperty().set("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla vitae eros in enim condimentum gravida. Aliquam erat volutpat. Donec vel risus non urna fringilla dictum. Integer vulputate nisl ac justo malesuada, sed vehicula eros ullamcorper. Proin in ligula a ante convallis condimentum. Vivamus a suscipit sapien. Vivamus pretium pharetra diam, non interdum ante tempus in. Suspendisse potenti. Integer auctor orci non diam lacinia, at fermentum leo tempor. Sed pretium urna et risus venenatis viverra.");

        HBox hBox = new HBox(15);
        FontIcon fontIcon = new FontIcon(Feather.EDIT_3);

        Label label = new Label("Sidst redigeret 02-05-2023 20:29");
        label.getStyleClass().add(Styles.TEXT_SUBTLE);

        hBox.setAlignment(Pos.CENTER_LEFT);

        hBox.getChildren().addAll(fontIcon, label, new Spacer(), accentMenuBtn);
        cardEventInfo.setFooter(hBox);

        menuItemDelete.setOnAction(event -> {
            System.out.println("deleting: " + eventItemModel.nameProperty().get());
        });

        menuItemEdit.setOnAction(event -> {
            System.out.println("editing: " + eventItemModel.nameProperty().get());
        });

        contentBox.setPrefHeight(Control.USE_COMPUTED_SIZE);
        contentBox.setPrefWidth(Control.USE_COMPUTED_SIZE);
    }
}
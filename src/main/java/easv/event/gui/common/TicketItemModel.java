package easv.event.gui.common;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TicketItemModel {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final ObjectProperty<TicketType> type = new SimpleObjectProperty<>();

    private final ObservableList<TicketEventItemModel> ticketEventItemModels = FXCollections.observableArrayList();

    public TicketItemModel(int id, String name, TicketType type, ObservableList<TicketEventItemModel> ticketEventItemModels) {
        this.id.set(id);
        this.name.set(name);
        this.type.set(type);
        this.ticketEventItemModels.addAll(ticketEventItemModels);
    }

    public TicketItemModel(int id, String name, TicketType type) {
        this.id.set(id);
        this.name.set(name);
        this.type.set(type);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<TicketType> typeProperty() {
        return type;
    }


    public ObservableList<TicketEventItemModel> getTicketEventItemModels() {
        return ticketEventItemModels;
    }

    public static boolean updateTicketItemModel(TicketItemModel ticketItemModel, String name, TicketType type) {
        ticketItemModel.nameProperty().set(name);
        ticketItemModel.typeProperty().set(type);
        return true;
    }
}

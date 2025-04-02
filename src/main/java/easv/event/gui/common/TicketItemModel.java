package easv.event.gui.common;

import easv.event.be.Ticket;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TicketItemModel {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final ObjectProperty<TicketTypeItemModel> type = new SimpleObjectProperty<>();

    private final ObservableList<TicketEventItemModel> ticketEventItemModels = FXCollections.observableArrayList();

    public TicketItemModel(int id, String name, TicketTypeItemModel type, ObservableList<TicketEventItemModel> ticketEventItemModels) {
        this.id.set(id);
        this.name.set(name);
        this.type.set(type);
        this.ticketEventItemModels.addAll(ticketEventItemModels);
    }

    public TicketItemModel(int id, String name, TicketTypeItemModel type) {
        this.id.set(id);
        this.name.set(name);
        this.type.set(type);
    }

    public TicketItemModel(String name, TicketTypeItemModel type) {
        this.name.set(name);
        this.type.set(type);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<TicketTypeItemModel> typeProperty() {
        return type;
    }


    public ObservableList<TicketEventItemModel> getTicketEventItemModels() {
        return ticketEventItemModels;
    }

    public static TicketItemModel copy(TicketItemModel ticketItemModel) {
        return new TicketItemModel(ticketItemModel.idProperty().get(), ticketItemModel.nameProperty().get(), ticketItemModel.typeProperty().get());
    }

    public static TicketItemModel fromEntity(Ticket ticket) {
        return new TicketItemModel(ticket.getId(), ticket.getName(), TicketTypeItemModel.fromEntity(ticket.getTicketType()));
    }

    public Ticket toEntity() {
        return new Ticket(idProperty().get(), nameProperty().get(), TicketTypeItemModel.toEntity(typeProperty().get()));
    }

    public void updateModel(TicketItemModel updatedModel) {
        this.name.set(updatedModel.name.get());
        this.type.set(updatedModel.type.get());
    }
}
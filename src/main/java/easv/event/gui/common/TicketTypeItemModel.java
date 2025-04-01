package easv.event.gui.common;

import easv.event.be.TicketType;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TicketTypeItemModel {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final SimpleStringProperty name = new SimpleStringProperty();

    public TicketTypeItemModel(int id, String name) {
        this.id.set(id);
        this.name.set(name);
    }

    public TicketTypeItemModel(String name) {
        this.name.set(name);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public static TicketTypeItemModel fromEntity(TicketType ticketType) {
        return new TicketTypeItemModel(ticketType.getId(), ticketType.getName());
    }

    public static TicketType toEntity(TicketTypeItemModel ticketTypeItemModel) {
        return new TicketType(ticketTypeItemModel.id.get(), ticketTypeItemModel.nameProperty().get());
    }

    public static TicketTypeItemModel copy(TicketTypeItemModel ticketTypeItemModel) {
        return new TicketTypeItemModel(ticketTypeItemModel.id.get(), ticketTypeItemModel.nameProperty().get());
    }

    public void updateModel(TicketTypeItemModel updatedModel) {
        this.id.set(updatedModel.id.get());
        this.name.set(updatedModel.name.get());
    }
}
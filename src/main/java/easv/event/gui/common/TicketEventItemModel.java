package easv.event.gui.common;

import easv.event.be.Event;
import easv.event.be.Ticket;
import easv.event.be.TicketEvent;
import javafx.beans.property.*;

public class TicketEventItemModel {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty();
    private final ObjectProperty<EventItemModel> event = new SimpleObjectProperty<>();
    private final ObjectProperty<TicketItemModel> ticket = new SimpleObjectProperty<>();
    private final SimpleDoubleProperty price = new SimpleDoubleProperty();

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public ObjectProperty<EventItemModel> eventProperty() {
        return event;
    }

    public ObjectProperty<TicketItemModel> ticketProperty() {
        return ticket;
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public static TicketEventItemModel fromEntity(TicketEvent ticketEvent) {
        TicketEventItemModel item = new TicketEventItemModel();
        if (ticketEvent.getEvent() != null)
            item.eventProperty().set(EventItemModel.fromEntity(ticketEvent.getEvent()));

        if (ticketEvent.getTicket() != null) {
            item.ticketProperty().set(TicketItemModel.fromEntity(ticketEvent.getTicket()));
        }

        item.idProperty().set(ticketEvent.getId());
        item.priceProperty().set(ticketEvent.getPrice());
        return item;
    }

    public TicketEvent toEntity() {
        Event event = EventItemModel.toEntity(eventProperty().get());
        int id = idProperty().get();
        double price = priceProperty().get();
        Ticket ticket = ticketProperty().get().toEntity();

        return new TicketEvent(id, ticket, event, price);
    }

    public void updateModel(TicketEventItemModel updatedModel) {
        this.id.set(updatedModel.id.get());
        this.event.set(updatedModel.event.get());
        this.ticket.set(updatedModel.ticket.get());
        this.price.set(updatedModel.price.get());
    }

    public static TicketEventItemModel copy(TicketEventItemModel original) {
        TicketEventItemModel copy = new TicketEventItemModel();
        copy.id.set(original.id.get());
        copy.event.set(original.event.get());
        copy.ticket.set(original.ticket.get());
        copy.price.set(original.price.get());
        return copy;
    }

    @Override
    public String toString() {
        return ticket.get().nameProperty().get() + " - " + price.get() + " DKK";
    }
}
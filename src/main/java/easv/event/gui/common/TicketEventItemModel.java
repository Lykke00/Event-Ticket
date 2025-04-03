package easv.event.gui.common;

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

        if (ticketEvent.getTicket() != null)
            item.ticketProperty().set(TicketItemModel.fromEntity(ticketEvent.getTicket()));

        item.priceProperty().set(ticketEvent.getPrice());
        return item;
    }
}

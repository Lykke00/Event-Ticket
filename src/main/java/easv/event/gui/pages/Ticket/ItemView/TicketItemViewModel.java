package easv.event.gui.pages.Ticket.ItemView;

import easv.event.gui.common.TicketItemModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class TicketItemViewModel {
    private final ObjectProperty<TicketItemModel> ticketItemModel = new SimpleObjectProperty<>();

    public ObjectProperty<TicketItemModel> ticketItemModelProperty() {
        return ticketItemModel;
    }
}

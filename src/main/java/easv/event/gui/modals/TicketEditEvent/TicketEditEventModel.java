package easv.event.gui.modals.TicketEditEvent;

import easv.event.gui.common.TicketEventItemModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class TicketEditEventModel {
    private final ObjectProperty<TicketEventItemModel> ticketEventItemModel = new SimpleObjectProperty<>();

    public ObjectProperty<TicketEventItemModel> ticketEventItemModelProperty() {
        return ticketEventItemModel;
    }
}

package easv.event.gui.modals.EditTicket;

import easv.event.gui.common.TicketItemModel;
import javafx.beans.property.SimpleObjectProperty;

public class EditTicketModel {
    private final SimpleObjectProperty<TicketItemModel> ticketItemModel = new SimpleObjectProperty<>();

    public SimpleObjectProperty<TicketItemModel> ticketItemModelProperty() {
        return ticketItemModel;
    }
}

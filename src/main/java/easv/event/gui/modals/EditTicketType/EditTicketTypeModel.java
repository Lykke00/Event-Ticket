package easv.event.gui.modals.EditTicketType;

import easv.event.gui.common.TicketTypeItemModel;

public class EditTicketTypeModel {
    private TicketTypeItemModel ticketType;

    public void setTicketType(TicketTypeItemModel ticketType) {
        this.ticketType = ticketType;
    }

    public TicketTypeItemModel getTicketType() {
        return ticketType;
    }
}

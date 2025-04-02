package easv.event.gui.modals.EditTicket;

import easv.event.gui.common.TicketItemModel;
import easv.event.gui.common.TicketTypeItemModel;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EditTicketModel {
    private final SimpleObjectProperty<TicketItemModel> ticketItemModel = new SimpleObjectProperty<>();

    public SimpleObjectProperty<TicketItemModel> ticketItemModelProperty() {
        return ticketItemModel;
    }
}

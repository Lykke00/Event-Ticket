package easv.event.gui.pages.Ticket.ItemView;

import easv.event.gui.common.EventItemModel;
import easv.event.gui.common.TicketEventItemModel;
import easv.event.gui.common.TicketItemModel;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TicketItemViewModel {
    private TicketItemModel ticketItemModel;
    private final ObservableList<TicketEventItemModel> ticketEvents = FXCollections.observableArrayList();

    private final SimpleBooleanProperty databaseLoading = new SimpleBooleanProperty();

    public TicketItemModel ticketItemModel() {
        return ticketItemModel;
    }

    public ObservableList<TicketEventItemModel> ticketEventsProperty() {
        return ticketEvents;
    }

    public BooleanProperty databaseLoadingProperty() {
        return databaseLoading;
    }

    public void setTicketItemModel(TicketItemModel ticketItemModel) {
        this.ticketItemModel = ticketItemModel;
    }
}

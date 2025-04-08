package easv.event.gui.modals.SellTicket;

import easv.event.gui.common.EventItemModel;
import easv.event.gui.common.TicketEventItemModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SellTicketModel {
    private final SimpleObjectProperty<EventItemModel> event = new SimpleObjectProperty<>();
    private final ObservableList<TicketEventItemModel> ticketEventItemModelList = FXCollections.observableArrayList();

    private final SimpleBooleanProperty databaseLoading = new SimpleBooleanProperty(true);

    public SimpleObjectProperty<EventItemModel> eventModelProperty() {
        return event;
    }

    public ObservableList<TicketEventItemModel> ticketEventItemModelListProperty() {
        return ticketEventItemModelList;
    }

    public SimpleBooleanProperty databaseLoadingProperty() {
        return databaseLoading;
    }
}

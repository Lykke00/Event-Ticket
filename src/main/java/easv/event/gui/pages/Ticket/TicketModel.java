package easv.event.gui.pages.Ticket;

import easv.event.gui.common.TicketItemModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

public class TicketModel {
    private final ObservableList<TicketItemModel> ticketItemModels = FXCollections.observableArrayList();
    private final SimpleBooleanProperty databaseLoading = new SimpleBooleanProperty(false);

    public ObservableList<TicketItemModel> ticketItemModelsListProperty() {
        return ticketItemModels;
    }

    public SortedList<TicketItemModel> getSortedTicketItemModelsList() {
        return new SortedList<>(ticketItemModels, (e1, e2) -> Integer.compare(e2.idProperty().get(), e1.idProperty().get()));
    }

    public SimpleBooleanProperty databaseLoadingProperty() {
        return databaseLoading;
    }

    public void deleteTicket(TicketItemModel item) {
        this.ticketItemModels.remove(item);
    }
}
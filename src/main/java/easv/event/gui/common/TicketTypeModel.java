package easv.event.gui.common;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

public class TicketTypeModel {
    private final SimpleBooleanProperty loadingProperty = new SimpleBooleanProperty(false);
    private final ObservableList<TicketTypeItemModel> ticketTypeItemModelProperty = FXCollections.observableArrayList();

    private final SortedList<TicketTypeItemModel> sortedTicketTypeList = new SortedList<>(ticketTypeItemModelProperty,
            (a, b) -> Integer.compare(b.idProperty().get(), a.idProperty().get()));

    public ObservableList<TicketTypeItemModel> ticketTypeItemModelProperty() {
        return ticketTypeItemModelProperty;
    }

    public SimpleBooleanProperty loadingProperty() {
        return loadingProperty;
    }

    public SortedList<TicketTypeItemModel> getSortedTicketItemModelsList() {
        return new SortedList<>(ticketTypeItemModelProperty, (e1, e2) -> Integer.compare(e2.idProperty().get(), e1.idProperty().get()));
    }

    public SortedList<TicketTypeItemModel> sortedTicketTypeList() {
        return sortedTicketTypeList;
    }

}
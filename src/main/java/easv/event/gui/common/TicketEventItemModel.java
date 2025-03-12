package easv.event.gui.common;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TicketEventItemModel {
    private final IntegerProperty eventId = new SimpleIntegerProperty();
    private final StringProperty eventName = new SimpleStringProperty();
    private final SimpleIntegerProperty price = new SimpleIntegerProperty();

    public TicketEventItemModel(int eventId, String eventName, int price) {
        this.eventId.set(eventId);
        this.eventName.set(eventName);
        this.price.set(price);
    }

    public IntegerProperty eventIdProperty() {
        return eventId;
    }

    public StringProperty eventNameProperty() {
        return eventName;
    }

    public SimpleIntegerProperty priceProperty() {
        return price;
    }
}

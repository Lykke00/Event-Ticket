package easv.event.gui.common;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EventItemModel {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();

    private final StringProperty date = new SimpleStringProperty();
    private final StringProperty time = new SimpleStringProperty();

    private final StringProperty location = new SimpleStringProperty();
    private final IntegerProperty soldTickets = new SimpleIntegerProperty();

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty locationProperty() {
        return location;
    }

    public IntegerProperty soldTicketsProperty() {
        return soldTickets;
    }

    public StringProperty timeProperty() {
        return time;
    }

    public StringProperty dateProperty() {
        return date;
    }

    public static EventItemModel create(int id, String name, String location, int soldTickets, String time, String Date) {
        EventItemModel eventItemModel = new EventItemModel();
        eventItemModel.id.set(id);
        eventItemModel.name.set(name);
        eventItemModel.location.set(location);
        eventItemModel.soldTickets.set(soldTickets);
        eventItemModel.time.set(time);
        eventItemModel.date.set(Date);

        return eventItemModel;
    }
}

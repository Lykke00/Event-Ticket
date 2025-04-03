package easv.event.gui.common;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserDeleteModel {
    private final ObservableList<EventItemModel> events = FXCollections.observableArrayList();
    private final SimpleBooleanProperty databaseLoading = new SimpleBooleanProperty(false);

    public ObservableList<EventItemModel> eventsProperties() {
        return events;
    }

    public SimpleBooleanProperty databaseLoadingProperty() {
        return databaseLoading;
    }
}

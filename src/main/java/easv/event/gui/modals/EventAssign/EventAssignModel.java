package easv.event.gui.modals.EventAssign;

import easv.event.gui.common.EventItemModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class EventAssignModel {
    private EventItemModel eventItemModel;
    private BooleanProperty loadingFromDatabase = new SimpleBooleanProperty(false);

    public EventItemModel eventItemModel() {
        return eventItemModel;
    }

    public BooleanProperty loadingFromDatabaseProperty() {
        return loadingFromDatabase;
    }

    public void setEventModelProperty(EventItemModel model) {
        eventItemModel = model;
    }
}

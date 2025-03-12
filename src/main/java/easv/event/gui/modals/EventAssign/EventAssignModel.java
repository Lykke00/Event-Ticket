package easv.event.gui.modals.EventAssign;

import easv.event.gui.common.EventItemModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class EventAssignModel {
    private final ObjectProperty<EventItemModel> eventItemModel = new SimpleObjectProperty<>();

    public ObjectProperty<EventItemModel> eventModelProperty() {
        return eventItemModel;
    }
}

package easv.event.gui.pages.Event.ItemView;

import easv.event.gui.common.EventItemModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class EventItemPageModel {
    private final SimpleObjectProperty<EventItemModel> eventItemModel = new SimpleObjectProperty<>();

    public ObjectProperty<EventItemModel> eventItemModelProperty() {
        return eventItemModel;
    }
}

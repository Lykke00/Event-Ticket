package easv.event.gui.pages.EventItemPage;

import easv.event.be.Event;
import easv.event.gui.common.EventItemModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class EventItemPageModel {
    private final SimpleObjectProperty<EventItemModel> eventItemModel = new SimpleObjectProperty<>(new EventItemModel());

    public ObjectProperty<EventItemModel> eventItemModelProperty() {
        return eventItemModel;
    }
}

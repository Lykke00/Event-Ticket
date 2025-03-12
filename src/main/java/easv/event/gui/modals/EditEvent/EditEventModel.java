package easv.event.gui.modals.EditEvent;

import easv.event.gui.common.EventItemModel;
import javafx.beans.property.SimpleObjectProperty;

public class EditEventModel {
    private final SimpleObjectProperty<EventItemModel> eventItemModel = new SimpleObjectProperty<>();

    public SimpleObjectProperty<EventItemModel> eventItemModelProperty() {
        return eventItemModel;
    }
}

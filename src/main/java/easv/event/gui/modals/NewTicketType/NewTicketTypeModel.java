package easv.event.gui.modals.NewTicketType;

import javafx.beans.property.SimpleBooleanProperty;

public class NewTicketTypeModel {
    private final SimpleBooleanProperty databaseLoading = new SimpleBooleanProperty(false);

    public SimpleBooleanProperty databaseLoadingProperty() {
        return databaseLoading;
    }
}
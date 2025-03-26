package easv.event.gui.common;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AuthModel {
    private final ObjectProperty<UserModel> user = new SimpleObjectProperty<>();

    private final BooleanProperty databaseLoad =  new SimpleBooleanProperty(false);
    private final BooleanProperty loggedIn = new SimpleBooleanProperty(false);
    private final BooleanProperty loginFailed = new SimpleBooleanProperty(false);

    public ObjectProperty<UserModel> userProperty() { return user; }

    public BooleanProperty databaseLoadProperty() { return databaseLoad; }

    public BooleanProperty loggedInProperty() { return loggedIn; }

    public BooleanProperty loginFailedProperty() { return loginFailed; }

    public void logout() {
        userProperty().setValue(null);
        loggedInProperty().setValue(false);
    }
}

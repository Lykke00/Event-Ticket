package easv.event.gui.modals.UserEdit;

import easv.event.gui.common.UserModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class UserEditModel {
    private final ObjectProperty<UserModel> userModel = new SimpleObjectProperty<>();

    public ObjectProperty<UserModel> userModelProperty() {
        return userModel;
    }
}

package easv.event.gui.pages.Users;

import easv.event.gui.common.UserModel;
import easv.event.gui.common.UserRole;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

public class UsersModel {
    private final ObservableList<UserModel> usersList = FXCollections.observableArrayList();

    public ObservableList<UserModel> usersModelObservableList() {
        return usersList;
    }

    public UserModel createUser(String firstName, String surName, String location, String email, UserRole value) {
        UserModel userModel = new UserModel(usersList.size() + 1, firstName, surName, location, email, value);
        usersList.add(userModel);
        return userModel;
    }

    public void deleteUser(UserModel item) {
        usersList.remove(item);
    }

    public SortedList<UserModel> getSortedUserModelList() {
        return new SortedList<>(usersList, (e1, e2) -> Integer.compare(e2.idProperty().get(), e1.idProperty().get()));
    }
}

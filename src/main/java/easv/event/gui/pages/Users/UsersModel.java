package easv.event.gui.pages.Users;

import easv.event.be.User;
import easv.event.gui.MainModel;
import easv.event.gui.common.UserModel;
import easv.event.gui.interactors.UserInteractor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

public class UsersModel {
    private final ObservableList<UserModel> usersList = FXCollections.observableArrayList();
    private final ObservableList<UserModel> coordinatorList = FXCollections.observableArrayList();

    public ObservableList<UserModel> usersModelObservableList() {
        return usersList;
    }

    public ObservableList<UserModel> coordinatorListModelObservableList() {
        return coordinatorList;
    }

    public void deleteUser(UserModel item) {
        usersList.remove(item);
    }

    public SortedList<UserModel> getSortedUserModelList() {
        return new SortedList<>(usersList, (e1, e2) -> Integer.compare(e2.idProperty().get(), e1.idProperty().get()));
    }
    public void deleteCoordinator(UserModel coordinatorModel) {
        UserInteractor userInteractor = MainModel.getInstance().getUserInteractor();
        userInteractor.deleteCoordinator(coordinatorModel);
    }
    public static User toEntity(UserModel model) {
        return new User(
                model.idProperty().get(),
                model.firstNameProperty().get(),
                model.lastNameProperty().get(),
                model.emailProperty().get(),
                model.roleProperty().get(),
                model.locationProperty().get()
        );
    }
}

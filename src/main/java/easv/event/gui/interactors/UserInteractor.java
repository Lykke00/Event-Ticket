package easv.event.gui.interactors;

import easv.event.be.User;
import easv.event.bll.UserManager;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.common.UserModel;
import easv.event.gui.pages.Users.UsersModel;
import easv.event.gui.utils.BackgroundTaskExecutor;
import easv.event.gui.utils.DialogHandler;

import java.io.IOException;
import java.util.List;

public class UserInteractor {
    private UserManager userManager;
    private final UsersModel usersModel;

    public UserInteractor(UsersModel usersModel) {
        this.usersModel = usersModel;

        try {
            this.userManager = new UserManager();
        } catch (IOException e) {
            DialogHandler.showExceptionError("Database fejl", "UserManager kunne ikke oprette forbindelse til databasen", e);
        }

        initialize();
    }

    private void initialize() {
        getAllUsers();
        getAllCoordinators();
    }

    private void getAllUsers() {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        return userManager.getAll();
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på at få fat i alle brugere", e);
                    }
                },
                users -> {
                    List<UserModel> userModels = users.stream()
                            .map(UserModel::fromEntity)
                            .toList();

                    usersModel.usersModelObservableList().addAll(userModels);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "Kunne ikke få fat i alle brugere", exception);
                }
        );
    }

    private void getAllCoordinators() {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        return userManager.getAllCoordinators();
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på at få fat i alle koordinatore", e);
                    }
                },
                coordinators -> {
                    List<UserModel> userModels = coordinators.stream()
                            .map(UserModel::fromEntity)
                            .toList();

                    usersModel.coordinatorListModelObservableList().addAll(userModels);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "Kunne ikke få fat i alle koordinatore", exception);
                }
        );
    }

    public UsersModel getUsersModel() {
        return usersModel;
    }
}

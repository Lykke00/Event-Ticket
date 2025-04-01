package easv.event.gui.interactors;

import easv.event.be.User;
import easv.event.bll.UserManager;
import easv.event.gui.common.AuthModel;
import easv.event.gui.common.UserModel;
import easv.event.gui.pages.Users.UsersModel;
import easv.event.gui.utils.BackgroundTaskExecutor;
import easv.event.gui.utils.DialogHandler;

import java.io.IOException;

public class AuthInteractor {
    private UserManager userManager;

    private final AuthModel authModel;
    private final UsersModel usersModel;

    public AuthInteractor(UsersModel usersModel) {
        this.authModel = new AuthModel();
        this.usersModel = usersModel;

        try {
            this.userManager = new UserManager();
        } catch (IOException e) {
            DialogHandler.showExceptionError("Database fejl", "UserManager kunne ikke oprette forbindelse til databasen", e);
        }
    }

    public void logIn(String email, String password) {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        authModel.loginFailedProperty().set(false);
                        return userManager.authenticateUser(email, password);
                    } catch (Exception e) {
                        if (e.getMessage().equals("DOESNT_EXIST") || e.getMessage().equals("PASSWORD_INCORRECT")) {
                            authModel.loginFailedProperty().set(true);
                        }

                        throw new RuntimeException("Database fejl ved login forsøg", e);
                    }
                },
                user -> {
                    UserModel loggedinModel = UserModel.fromEntity(user);
                    authModel.userProperty().set(loggedinModel);
                    authModel.loggedInProperty().set(true);
                },
                exception -> {
                    if (authModel.loginFailedProperty().get())
                        return;

                    DialogHandler.showExceptionError("Database fejl", "Kunne ikke logge bruger ind", exception);
                },
                loading -> {
                    authModel.databaseLoadProperty().set(loading);
                }
        );
    }

    public void registerUser(User user) {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        return userManager.registerUser(user);
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på oprettelse af bruger", e);
                    }
                },
                createdUser -> {
                    UserModel createdModel = UserModel.fromEntity(createdUser);
                    usersModel.usersModelObservableList().add(createdModel);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "Kunne ikke oprette bruger", exception);
                }
        );
    }

    public AuthModel getAuthModel() {
        return authModel;
    }
}
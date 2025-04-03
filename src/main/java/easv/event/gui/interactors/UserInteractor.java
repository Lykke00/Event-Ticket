package easv.event.gui.interactors;

import easv.event.be.User;
import easv.event.bll.UserManager;
import easv.event.gui.common.EventItemModel;
import easv.event.gui.common.TicketItemModel;
import easv.event.gui.common.UserDeleteModel;
import easv.event.gui.common.UserModel;
import easv.event.gui.modals.UserEdit.UserEditModel;
import easv.event.gui.pages.Users.UsersModel;
import easv.event.gui.utils.BackgroundTaskExecutor;
import easv.event.gui.utils.DialogHandler;
import easv.event.gui.utils.NotificationHandler;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class UserInteractor {
    private UserManager userManager;

    private final UsersModel usersModel;
    private final UserDeleteModel userDeleteModel;
    private final UserEditModel userEditModel;

    private BooleanProperty loadingCoordinatorsFromDbProperty = new SimpleBooleanProperty(false);

    public UserInteractor(UsersModel usersModel) {
        this.usersModel = usersModel;
        this.userDeleteModel = new UserDeleteModel();
        this.userEditModel = new UserEditModel();

        try {
            this.userManager = new UserManager();
        } catch (IOException e) {
            DialogHandler.showExceptionError("Database fejl", "UserManager kunne ikke oprette forbindelse til databasen", e);
        }
    }

    public void initialize() {
        getAllUsers();
    }

    public void initializeAllCoordinators() {
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

                    usersModel.usersModelObservableList().setAll(userModels);
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
                        loadingCoordinatorsFromDbProperty.set(true);
                        return userManager.getAllCoordinators();
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på at få fat i alle koordinatore", e);
                    }
                },
                coordinators -> {
                    List<UserModel> userModels = coordinators.stream()
                            .map(UserModel::fromEntity)
                            .toList();

                    usersModel.coordinatorListModelObservableList().setAll(userModels);
                    loadingCoordinatorsFromDbProperty.set(false);
                },
                exception -> {
                    loadingCoordinatorsFromDbProperty.set(false);
                    DialogHandler.showExceptionError("Database fejl", "Kunne ikke få fat i alle koordinatore", exception);
                }
        );
    }

    public void deleteCoordinator(UserModel coordinatorModel) {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        return userManager.deleteCoordinator(UserModel.toEntity(coordinatorModel));
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på slet af koordinator", e);
                    }
                },
                didDelete -> {
                    usersModel.usersModelObservableList().remove(coordinatorModel);
                    usersModel.coordinatorListModelObservableList().remove(coordinatorModel);
                    NotificationHandler.getInstance().showNotification(
                            "Koordinator " + coordinatorModel.firstNameProperty().get() + " " +
                                    coordinatorModel.lastNameProperty().get() + " er blevet slettet",
                            NotificationHandler.NotificationType.SUCCESS);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "Kunne ikke slette koordinator", exception);
                }
        );
    }

    public void editCoordinator(UserModel original, UserModel updated) {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        return userManager.editCoordinator(UserModel.toEntity(updated));
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på at redigere koordinator", e);
                    }
                },
                didUpdate -> {
                    original.updateModel(updated);
                    NotificationHandler.getInstance().showNotification(
                            "Koordinator " + original.firstNameProperty().get() + " " +
                                    original.lastNameProperty().get() + " er blevet redigeret",
                            NotificationHandler.NotificationType.SUCCESS);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "Kunne ikke redigere koordinator", exception);
                }
        );
    }

    public void getEventsByCoordinator(UserModel coordinatorModel, Consumer<List<EventItemModel>> callback) {
        String err = "Database fejl ved forsøg på at få fat i events fra bruger";
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        return userManager.getEventsByCoordinator(UserModel.toEntity(coordinatorModel));
                    } catch (Exception e) {
                        throw new RuntimeException(err, e);
                    }
                },
                exists -> {
                    List<EventItemModel> eventItemModels = exists.stream()
                            .map(EventItemModel::fromEntity)
                            .toList();

                    callback.accept(eventItemModels);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", err, exception);
                    callback.accept(null);
                }
        );
    }

    public BooleanProperty loadingCoordinatorsFromDbProperty() {
        return loadingCoordinatorsFromDbProperty;
    }

    public UsersModel getUsersModel() {
        return usersModel;
    }

    public UserEditModel getUserEditModel() {
        return userEditModel;
    }
}

package easv.event.gui.interactors;

import easv.event.be.Event;
import easv.event.be.Ticket;
import easv.event.be.TicketEvent;
import easv.event.be.TicketType;
import easv.event.bll.TicketManager;
import easv.event.gui.common.*;
import easv.event.gui.modals.EditTicket.EditTicketModel;
import easv.event.gui.modals.EditTicketType.EditTicketTypeModel;
import easv.event.gui.modals.NewTicketType.NewTicketTypeModel;
import easv.event.gui.pages.Ticket.ItemView.TicketItemViewModel;
import easv.event.gui.pages.Ticket.TicketModel;
import easv.event.gui.utils.BackgroundTaskExecutor;
import easv.event.gui.utils.DialogHandler;
import easv.event.gui.utils.NotificationHandler;
import javafx.beans.Observable;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.function.Consumer;

public class TicketInteractor {
    private TicketManager ticketManager;

    private final TicketModel ticketModel;
    private final TicketTypeModel ticketTypeModel;

    private final NewTicketTypeModel newTicketTypeModel;
    private final EditTicketTypeModel editTicketTypeModel;
    private final EditTicketModel editTicketModel;
    private final TicketItemViewModel ticketItemViewModel;

    public TicketInteractor() {
        this.ticketModel = new TicketModel();
        this.ticketTypeModel = new TicketTypeModel();
        this.newTicketTypeModel = new NewTicketTypeModel();
        this.editTicketTypeModel = new EditTicketTypeModel();
        this.editTicketModel = new EditTicketModel();
        this.ticketItemViewModel = new TicketItemViewModel();

        try {
            this.ticketManager = new TicketManager();
        } catch (Exception e) {
            DialogHandler.showExceptionError("Database fejl", "TicketManager kunne ikke oprette forbindelse til databasen", e);
        }
    }

    public void createTicket(TicketItemModel ticketItemModel) {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        return ticketManager.createTicket(ticketItemModel.toEntity());
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på oprettelse af ny billet", e);
                    }
                },
                createdTicket -> {
                    TicketItemModel createdTicketModel = TicketItemModel.fromEntity(createdTicket);
                    ticketModel.ticketItemModelsListProperty().add(createdTicketModel);
                    NotificationHandler.getInstance().showNotification( "Billetten " + createdTicketModel.nameProperty().get() + " er blevet oprettet", NotificationHandler.NotificationType.SUCCESS);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "Kunne ikke oprette billet", exception);
                }
        );
    }

    public void createTicketType(TicketTypeItemModel ticketTypeItemModel) {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        return ticketManager.createTicketType(TicketTypeItemModel.toEntity(ticketTypeItemModel));
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på oprettelse af ny billet type", e);
                    }
                },
                createdTicketType -> {
                    TicketTypeItemModel createdTicketTypeModel = TicketTypeItemModel.fromEntity(createdTicketType);
                    ticketTypeModel.ticketTypeItemModelProperty().add(createdTicketTypeModel);
                    NotificationHandler.getInstance().showNotification( "Billet typen " + createdTicketTypeModel.nameProperty().get() + " er blevet oprettet", NotificationHandler.NotificationType.SUCCESS);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "Kunne ikke oprette billet", exception);
                },
                isLoading -> {
                    newTicketTypeModel.databaseLoadingProperty().set(isLoading);
                }
        );
    }

    public void deleteTicketType(TicketTypeItemModel ticketTypeItemModel, List<TicketItemModel> ticketItemModels) {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        List<Ticket> tickets = ticketItemModels.stream()
                                .map(TicketItemModel::toEntity)
                                .toList();

                        return ticketManager.deleteTicketType(TicketTypeItemModel.toEntity(ticketTypeItemModel), tickets);
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på slet af billet type", e);
                    }
                },
                didDelete -> {
                    ticketTypeModel.ticketTypeItemModelProperty().remove(ticketTypeItemModel);
                    NotificationHandler.getInstance().showNotification( "Billet typen " + ticketTypeItemModel.nameProperty().get() + " er blevet slettet", NotificationHandler.NotificationType.SUCCESS);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "Kunne ikke slette billet type", exception);
                }
        );
    }

    public void loadAllTickets() {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        return ticketManager.getAllTickets();
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på at få fat i alle billetter", e);
                    }
                },
                allTicketTypes -> {
                    List<TicketItemModel> ticketItemModels = allTicketTypes.stream()
                            .map(TicketItemModel::fromEntity)
                            .toList();

                    ticketModel.ticketItemModelsListProperty().setAll(ticketItemModels);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "Kunne ikke få fat i alle billetter", exception);
                },
                isLoading -> {
                    ticketModel.databaseLoadingProperty().set(isLoading);
                }
        );
    }


    public void loadAllTicketTypes() {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        return ticketManager.getAllTicketTypes();
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på at få fat i alle billet typer", e);
                    }
                },
                allTicketTypes -> {
                    List<TicketTypeItemModel> ticketTypeItemModels = allTicketTypes.stream()
                            .map(TicketTypeItemModel::fromEntity)
                            .toList();

                    ticketTypeModel.ticketTypeItemModelProperty().setAll(ticketTypeItemModels);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "Kunne ikke få fat i alle billet typer", exception);
                },
                isLoading -> {
                    ticketTypeModel.loadingProperty().set(isLoading);
                }
        );
    }

    public void doesTicketExist(String name, Consumer<Boolean> callback) {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        return ticketManager.doesTicketNameExist(name);
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på at tjekke billet navn", e);
                    }
                },
                exists -> {
                    callback.accept(exists);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "Kunne ikke tjekke om billet navn eksisterer", exception);
                    callback.accept(false);
                }
        );
    }

    public void doesTicketTypeExist(String name, Consumer<Boolean> callback) {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        return ticketManager.doesTicketTypeExist(name);
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på at tjekke billet type", e);
                    }
                },
                exists -> {
                    callback.accept(exists);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "Kunne ikke tjekke om billet type eksisterer", exception);
                    callback.accept(false);
                }
        );
    }

    public void getTicketsByType(TicketTypeItemModel type, Consumer<List<TicketItemModel>> callback) {
        String err = "Database fejl ved forsøg på at få fat i billetter for type";
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        return ticketManager.getTicketsByType(TicketTypeItemModel.toEntity(type));
                    } catch (Exception e) {
                        throw new RuntimeException(err, e);
                    }
                },
                exists -> {
                    List<TicketItemModel> ticketItemModels = exists.stream()
                            .map(TicketItemModel::fromEntity)
                            .toList();

                    callback.accept(ticketItemModels);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", err, exception);
                    callback.accept(null);
                }
        );
    }

    public void editTicketType(TicketTypeItemModel original, TicketTypeItemModel updatedModel, Consumer<Boolean> callback) {
        String errorMsg = "En fejl skete ved at prøve at redigere billet typen";

        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        TicketType ticketType = TicketTypeItemModel.toEntity(updatedModel);
                        return ticketManager.editTicketType(ticketType);
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på at tjekke billet type", e);
                    }
                },
                exists -> {
                    callback.accept(exists);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", errorMsg, exception);
                    callback.accept(false);
                }
        );
    }

    public void editTicket(TicketItemModel updatedModel, Consumer<Boolean> callback) {
        String errorMsg = "En fejl skete ved at prøve at redigere billetten";

        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        Ticket ticket = updatedModel.toEntity();
                        return ticketManager.editTicket(ticket);
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på at tjekke billetten", e);
                    }
                },
                exists -> {
                    callback.accept(exists);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", errorMsg, exception);
                    callback.accept(false);
                }
        );
    }

    public void getEventsForTicket(TicketItemModel item, Consumer<List<EventItemModel>> callback) {
        String err = "Database fejl ved forsøg på at få fat i events fra billet";
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        return ticketManager.getEventsByTicket(item.toEntity());
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

    public void deleteTicket(TicketItemModel ticket) {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        return ticketManager.deleteTicket(ticket.toEntity());
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på slet af billetten", e);
                    }
                },
                didDelete -> {
                    ticketModel.ticketItemModelsListProperty().remove(ticket);
                    NotificationHandler.getInstance().showNotification( "Billetten " + ticket.nameProperty().get() + " er blevet slettet", NotificationHandler.NotificationType.SUCCESS);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "Kunne ikke slette billetten", exception);
                }
        );
    }

    public void addTicketToEvent(TicketItemModel ticket, double price, ObservableList<EventItemModel> eventsToAdd, ObservableList<EventItemModel> eventsToRemove) {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        List<Event> eventsToAddEntities = eventsToAdd.stream()
                                .map(EventItemModel::toEntity)
                                .toList();

                        List<Event> eventsToRemoveEntities = eventsToRemove.stream()
                                .map(EventItemModel::toEntity)
                                .toList();

                        Ticket ticketEntity = ticket.toEntity();
                        TicketEvent ticketEvent = new TicketEvent(ticketEntity, price);

                        return ticketManager.addTicketToEvent(ticketEvent, eventsToAddEntities, eventsToRemoveEntities);
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på at tilføje billet til event", e);
                    }
                },
                tickets -> {
                    List<TicketEventItemModel> eventToModel = tickets.stream()
                            .map(TicketEventItemModel::fromEntity)
                            .toList();

                    ticket.getTicketEventItemModels().removeIf(ticketEvent ->
                            eventsToRemove.stream().anyMatch(event -> event.idProperty().get() == ticketEvent.eventProperty().get().idProperty().get())
                    );
                    ticket.getTicketEventItemModels().addAll(eventToModel);

                    if (!tickets.isEmpty() && !eventsToRemove.isEmpty())
                        NotificationHandler.getInstance().showNotification( "Billetten er blevet tilføjet til " + tickets.size() + " event(s) og fjernet fra " + eventsToRemove.size() + " event(s)", NotificationHandler.NotificationType.SUCCESS);
                    else if (!tickets.isEmpty())
                        NotificationHandler.getInstance().showNotification("Billetten er blevet tilføjet til " + tickets.size() + " event(s)", NotificationHandler.NotificationType.SUCCESS);
                    else if (!eventsToRemove.isEmpty())
                        NotificationHandler.getInstance().showNotification("Billetten er blevet fjernet fra " + eventsToRemove.size() + " event(s)", NotificationHandler.NotificationType.SUCCESS);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "Kunne ikke tilføje billet", exception);
                }
        );
    }

    public void getEventTicketsByEvent(TicketItemModel ticketItemModel) {
        BackgroundTaskExecutor.execute(
                () -> {
                    try {
                        Ticket ticketEntity = ticketItemModel.toEntity();
                        return ticketManager.getEventTicketsByTicket(ticketEntity);
                    } catch (Exception e) {
                        throw new RuntimeException("Database fejl ved forsøg på at få fat i alle event billetter for billet", e);
                    }
                },
                ticketEvents -> {
                    List<TicketEventItemModel> ticketEventsModel = ticketEvents.stream()
                            .map(TicketEventItemModel::fromEntity)
                            .toList();

                    ticketItemModel.getTicketEventItemModels().setAll(ticketEventsModel);
                    ticketItemViewModel.ticketEventsProperty().setAll(ticketEventsModel);
                },
                exception -> {
                    DialogHandler.showExceptionError("Database fejl", "Kunne ikke få fat i event billetter for billet", exception);
                },
                loading -> {
                    ticketItemViewModel.databaseLoadingProperty().set(loading);
                }
        );
    }

    public TicketModel getTicketModel() {
        return ticketModel;
    }

    public TicketTypeModel getTicketTypeModel() {
        return ticketTypeModel;
    }

    public NewTicketTypeModel getNewTicketTypeModel() {
        return newTicketTypeModel;
    }

    public EditTicketTypeModel getEditTicketTypeModel() {
        return editTicketTypeModel;
    }

    public EditTicketModel getEditTicketModel() {
        return editTicketModel;
    }

    public TicketItemViewModel getTicketItemViewModel() {
        return ticketItemViewModel;
    }
}
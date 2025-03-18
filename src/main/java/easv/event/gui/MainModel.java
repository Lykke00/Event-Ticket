package easv.event.gui;

import easv.event.gui.common.EventItemModel;
import easv.event.gui.interactors.EventInteractor;
import easv.event.gui.modals.EditEvent.EditEventModel;
import easv.event.gui.modals.EditTicket.EditTicketModel;
import easv.event.gui.modals.EventAssign.EventAssignModel;
import easv.event.gui.modals.TicketEditEvent.TicketEditEventModel;
import easv.event.gui.modals.UserEdit.UserEditModel;
import easv.event.gui.pages.Event.ItemView.EventItemPageModel;
import easv.event.gui.pages.Event.EventModel;
import easv.event.gui.pages.Ticket.ItemView.TicketItemViewModel;
import easv.event.gui.pages.Ticket.TicketModel;
import easv.event.gui.pages.Users.UsersModel;
import easv.event.gui.utils.DialogHandler;

public class MainModel {
    private final static MainModel instance = new MainModel();

    private EventInteractor eventInteractor;
    private final EventItemPageModel eventItemModel;

    private final EditEventModel editEventModel;

    private final TicketModel ticketModel;

    private final UsersModel usersModel;
    private final TicketItemViewModel ticketItemViewModel;

    private final EditTicketModel editTicketModel;
    private final TicketEditEventModel ticketEditEventModel;

    private final UserEditModel userEditModel;

    private final EventAssignModel eventAssignModel;

    private MainModel() {
        try {
            eventInteractor = new EventInteractor();
        } catch (Exception e) {
            DialogHandler.showExceptionError("Database fejl", "EventDAO kunne ikke oprette forbindelse til databasen", e);
        }

        editEventModel = new EditEventModel();
        eventItemModel = new EventItemPageModel();

        ticketModel = new TicketModel();
        ticketItemViewModel = new TicketItemViewModel();

        editTicketModel = new EditTicketModel();
        ticketEditEventModel = new TicketEditEventModel();

        usersModel = new UsersModel();
        userEditModel = new UserEditModel();

        eventAssignModel = new EventAssignModel();

        // test
        ticketModel.ticketItemModelsListProperty().addAll(TestData.ticketTestData());
        usersModel.usersModelObservableList().addAll(TestData.userTestData());
    }

    public static MainModel getInstance() {
        return instance;
    }

    public EventInteractor getEventInteractor() {
        return eventInteractor;
    }

    public EventItemPageModel getEventItemModel() {
        return eventItemModel;
    }

    public EditEventModel getEditEventModel() {
        return editEventModel;
    }

    public TicketModel getTicketModel() {
        return ticketModel;
    }

    public UsersModel getUsersModel() {
        return usersModel;
    }

    public TicketItemViewModel getTicketItemViewModel() {
        return ticketItemViewModel;
    }

    public EditTicketModel getEditTicketModel() {
        return editTicketModel;
    }

    public TicketEditEventModel getTicketEditEventModel() {
        return ticketEditEventModel;
    }

    public UserEditModel getUserEditModel() {
        return userEditModel;
    }

    public EventAssignModel getEventAssignModel() {
        return eventAssignModel;
    }
}

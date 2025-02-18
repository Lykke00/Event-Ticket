package easv.event.gui;

import easv.event.gui.common.EventItemModel;
import easv.event.gui.pages.EventItemPage.EventItemPageModel;
import easv.event.gui.pages.EventPage.EventModel;

public class MainModel {
    private final static MainModel instance = new MainModel();

    private final EventModel eventModel;
    private final EventItemPageModel eventItemModel;

    private MainModel() {
        eventModel = new EventModel();
        eventItemModel = new EventItemPageModel();
    }

    public static MainModel getInstance() {
        return instance;
    }

    public EventModel getEventModel() {
        return eventModel;
    }

    public EventItemPageModel getEventItemModel() {
        return eventItemModel;
    }
}

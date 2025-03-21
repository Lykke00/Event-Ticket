package easv.event.gui.pages.Event;

import easv.event.gui.common.EventItemModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.time.LocalDate;

public class EventModel {
    private final ObservableList<EventItemModel> events = FXCollections.observableArrayList();

    public ObservableList<EventItemModel> eventsListProperty() {
        return events;
    }

    public SortedList<EventItemModel> getSortedEventsList() {
        return new SortedList<>(events, (event1, event2) -> Integer.compare(event2.idProperty().get(), event1.idProperty().get()));
    }

    public FilteredList<EventItemModel> getCompletedEventsList() {
        return new FilteredList<>(events, event -> event.dateProperty().get().isBefore(LocalDate.now()));
    }

    public FilteredList<EventItemModel> getUpcomingEventsList() {
        return new FilteredList<>(events, event -> !event.dateProperty().get().isBefore(LocalDate.now()));
    }

    public boolean deleteEvent(EventItemModel item) {
        return this.events.remove(item);
    }
}

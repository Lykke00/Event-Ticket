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
        return new SortedList<>(events, (event1, event2) -> event2.dateProperty().get().compareTo(event1.dateProperty().get()));
    }

    public FilteredList<EventItemModel> getCompletedEventsList() {
        return new FilteredList<>(events, event -> event.dateProperty().get().isBefore(LocalDate.now()));
    }

    public FilteredList<EventItemModel> getUpcomingEventsList() {
        return new FilteredList<>(events, event -> !event.dateProperty().get().isBefore(LocalDate.now()));
    }

    public void addEvent(int id, String name, String location, String time, String date) {
        this.events.add(new EventItemModel(this.events.size() + 1, name, location, 200, time, date));
    }

    public boolean deleteEvent(EventItemModel item) {
        return this.events.remove(item);
    }
}

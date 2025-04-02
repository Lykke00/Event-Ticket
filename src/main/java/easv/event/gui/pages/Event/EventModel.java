package easv.event.gui.pages.Event;

import easv.event.gui.common.EventItemModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class EventModel {
    private final ObservableList<EventItemModel> events = FXCollections.observableArrayList();
    private final StringProperty currentListSort = new SimpleStringProperty();
    private FilteredList<EventItemModel> filteredEvents;

    public EventModel() {
        filteredEvents = new FilteredList<>(events);  // Bind filteredEvents til events listen
        initListListener();
    }

    public ObservableList<EventItemModel> eventsListProperty() {
        return filteredEvents;  // Returner den filtrerede liste
    }

    public void updateEvents(List<EventItemModel> newEvents) {
        events.setAll(newEvents);  // Opdater den oprindelige events liste
    }

    public void addEvent(EventItemModel model) {
        events.add(model);
    }

    public SortedList<EventItemModel> getSortedEventsList() {
        return new SortedList<>(filteredEvents, (event1, event2) -> {
            LocalDate date1 = event1.dateProperty().get();
            LocalDate date2 = event2.dateProperty().get();
            LocalDate today = LocalDate.now();

            if (date1 == null) return 1;
            if (date2 == null) return -1;

            boolean isFuture1 = !date1.isBefore(today);
            boolean isFuture2 = !date2.isBefore(today);

            if (isFuture1 && !isFuture2) return -1;
            if (!isFuture1 && isFuture2) return 1;

            return date1.compareTo(date2);
        });
    }

    public FilteredList<EventItemModel> getCompletedEventsList() {
        return new FilteredList<>(filteredEvents, event -> event.dateProperty().get().isBefore(LocalDate.now()));
    }

    public FilteredList<EventItemModel> getUpcomingEventsList() {
        return new FilteredList<>(filteredEvents, event -> !event.dateProperty().get().isBefore(LocalDate.now()));
    }

    public StringProperty currentListProperty() {
        return currentListSort;
    }

    // Lytter på ændringer i currentListSort og opdaterer eventlisten
    public void initListListener() {
        currentListSort.addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Aktive events":
                    filterActiveEvents();
                    break;
                case "Inaktive events":
                    filterInactiveEvents();
                    break;
                case "Alle events":
                    resetFilter();
                    break;
                default:
                    resetFilter();
                    break;
            }
        });
    }

    // Filtrerer kun aktive events
    private void filterActiveEvents() {
        filteredEvents.setPredicate(event -> event.activeProperty().get());
    }

    // Filtrerer kun inaktive events
    private void filterInactiveEvents() {
        filteredEvents.setPredicate(event -> !event.activeProperty().get());
    }

    // Nulstil filteret og viser alle events
    private void resetFilter() {
        filteredEvents.setPredicate(event -> true);  // Alle events vises
    }

    public boolean deleteEvent(EventItemModel item) {
        return this.events.remove(item);
    }
}

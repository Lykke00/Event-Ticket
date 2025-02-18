package easv.event.gui.pages.EventPage;

import easv.event.gui.common.EventItemModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;

public class EventModel {
    private final ObservableList<EventItemModel> events = FXCollections.observableArrayList();
    public EventModel() {
        testData();
    }

    public ObservableList<EventItemModel> eventsListProperty() {
        return events;
    }

    public SortedList<EventItemModel> getSortedEventsList() {
        return new SortedList<>(events, (e1, e2) -> Integer.compare(e2.idProperty().get(), e1.idProperty().get()));
    }

    private void testData() {
        this.events.add(EventItemModel.create(1, "Concert A", "Esbjerg", 120, "17:00", "2025-02-13"));
        this.events.add(EventItemModel.create(2, "Festival B", "Aarhus", 300, "18:00", "2025-02-14"));
        this.events.add(EventItemModel.create(3, "Theater C", "Copenhagen", 50, "19:30", "2025-02-15"));
        this.events.add(EventItemModel.create(4, "Conference D", "Odense", 200, "09:00", "2025-02-16"));
        this.events.add(EventItemModel.create(5, "Sports Event E", "Aalborg", 150, "16:30", "2025-02-17"));
        this.events.add(EventItemModel.create(6, "Music Festival F", "Esbjerg", 500, "12:00", "2025-02-18"));
        this.events.add(EventItemModel.create(7, "Exhibition G", "Aarhus", 70, "14:00", "2025-02-19"));
        this.events.add(EventItemModel.create(8, "Comedian H", "Copenhagen", 80, "21:00", "2025-02-20"));
        this.events.add(EventItemModel.create(9, "Workshop I", "Odense", 40, "10:00", "2025-02-21"));
        this.events.add(EventItemModel.create(10, "Talk J", "Aalborg", 90, "11:30", "2025-02-22"));
        this.events.add(EventItemModel.create(11, "Concert K", "Esbjerg", 200, "20:00", "2025-02-23"));
        this.events.add(EventItemModel.create(12, "Festival L", "Aarhus", 350, "18:00", "2025-02-24"));
        this.events.add(EventItemModel.create(13, "Play M", "Copenhagen", 60, "19:00", "2025-02-25"));
        this.events.add(EventItemModel.create(14, "Summit N", "Odense", 180, "09:30", "2025-02-26"));
        this.events.add(EventItemModel.create(15, "Football Match O", "Aalborg", 220, "16:00", "2025-02-27"));
        this.events.add(EventItemModel.create(16, "Rock Concert P", "Esbjerg", 150, "21:30", "2025-02-28"));
        this.events.add(EventItemModel.create(17, "Dance Show Q", "Aarhus", 100, "18:30", "2025-03-01"));
        this.events.add(EventItemModel.create(18, "Product Launch R", "Copenhagen", 60, "13:00", "2025-03-02"));
        this.events.add(EventItemModel.create(19, "Tech Conference S", "Odense", 250, "09:00", "2025-03-03"));
        this.events.add(EventItemModel.create(20, "Art Exhibition T", "Aalborg", 110, "15:30", "2025-03-04"));
    }

    public void addEvent(int id, String name, String location, String time, String date) {
        this.events.add(EventItemModel.create(this.events.size() + 1, name, location, 200, time, date));
    }
}

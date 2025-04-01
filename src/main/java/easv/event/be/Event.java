package easv.event.be;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Event {
    private int id;
    private String title;
    private String description;
    private LocalDate date;
    private String startsAt;
    private String location;
    private boolean active;

    private int soldTickets = 0;
    private List<User> coordinators = new ArrayList<>();


    public Event(int id, String title, String description, LocalDate date, String startsAt, String location, boolean active) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.startsAt = startsAt;
        this.location = location;
        this.active = active;
    }

    public Event(String title, String description, LocalDate date, String startsAt, String location) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.startsAt = startsAt;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(String startsAt) {
        this.startsAt = startsAt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(int soldTickets) {
        this.soldTickets = soldTickets;
    }

    public List<User> getCoordinators() {
        return coordinators;
    }

    public void addCoordinator(User coordinator) {
        this.coordinators.add(coordinator);
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return "Events{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", startsAt='" + startsAt + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}

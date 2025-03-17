package easv.event.be;

import java.time.LocalDate;

public class Event {
    private int id;
    private String title;
    private String description;
    private LocalDate date;
    private String startsAt;
    private String location;

    public Event(int id, String title, String description, LocalDate date, String startsAt, String location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.startsAt = startsAt;
        this.location = location;
    }

    public Event(String location, String startsAt, LocalDate date, String description, String title) {
        this.location = location;
        this.startsAt = startsAt;
        this.date = date;
        this.description = description;
        this.title = title;
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

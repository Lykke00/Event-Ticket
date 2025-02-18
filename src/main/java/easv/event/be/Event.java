package easv.event.be;

public class Event {
    private int id;
    private String name;
    private String description;
    private int soldTickets;

    public Event(int id, String name, String description, int soldTickets) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.soldTickets = soldTickets;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSoldTickets() {
        return soldTickets;
    }

    public void setSoldTickets(int soldTickets) {
        this.soldTickets = soldTickets;
    }
}

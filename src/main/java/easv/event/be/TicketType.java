package easv.event.be;

public class TicketType {
    private int id;
    private String name;

    public TicketType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public TicketType(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "TicketType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
package easv.event.be;

public class Ticket {
    private int id;
    private String name;
    private int type;

    public Ticket(int id, String name, int type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Ticket(int type, String name) {
        this.type = type;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Tickets{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}

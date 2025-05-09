package easv.event.be;

public class Ticket {
    private int id;
    private String name;
    private TicketType ticketType;

    public Ticket(int id, String name, TicketType ticketType) {
        this.id = id;
        this.name = name;
        this.ticketType = ticketType;
    }

    public Ticket(String name, TicketType ticketType) {
        this.name = name;
        this.ticketType = ticketType;
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

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    @Override
    public String toString() {
        return "Tickets{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + ticketType.toString() +
                '}';
    }
}
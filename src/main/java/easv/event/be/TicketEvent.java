package easv.event.be;

public class TicketEvent {
    private int id;
    private Ticket ticket;
    private Event event;
    private double price;

    public TicketEvent(int id, Ticket ticket, Event event, double price) {
        this.id = id;
        this.ticket = ticket;
        this.event = event;
        this.price = price;
    }

    public TicketEvent(int id, Event event, Ticket ticket, double ticketPrice) {
        this.id = id;
        this.event = event;
        this.ticket = ticket;
        this.price = ticketPrice;
    }

    public TicketEvent(Ticket ticket, Event event, double price) {
        this.ticket = ticket;
        this.event = event;
        this.price = price;
    }

    public TicketEvent(Ticket ticket, double price) {
        this.ticket = ticket;
        this.price = price;
    }

    public TicketEvent(int id, TicketEvent ticket, Event event) {
        this.id = id;
        this.price = ticket.getPrice();
        this.ticket = ticket.getTicket();
        this.event = event;
    }

    public TicketEvent(int id, Event event, double ticketPrice) {
        this.id = id;
        this.event = event;
        this.price = ticketPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "TicketEvent{" +
                "id=" + id +
                ", ticket=" + ticket +
                ", event=" + event +
                ", price=" + price +
                '}';
    }
}
package easv.event.gui.common;

public enum TicketType {
    PAID("Betalt"),
    PROMOTIONAL("Reklame"),
    FREE("Gratis");

    private final String type;

    TicketType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static TicketType fromString(String text) {
        for (TicketType b : TicketType.values()) {
            if (b.type.equalsIgnoreCase(text))
                return b;
        }
        return null;
    }
}

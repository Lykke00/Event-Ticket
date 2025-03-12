package easv.event.gui;

import easv.event.gui.common.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TestData {
    private static final ObservableList<UserModel> users = FXCollections.observableArrayList();
    private static final ObservableList<EventItemModel> events = FXCollections.observableArrayList();

    private static final ObservableList<TicketItemModel> tickets = FXCollections.observableArrayList();
    private static final ObservableList<TicketEventItemModel> ticketsEvents = FXCollections.observableArrayList();

    public static ObservableList<EventItemModel> eventItems() {
        ObservableList<UserModel> coordinators = userTestData();
        Random random = new Random();

        if (events.isEmpty()) {
            events.add(createEvent(1, "Concert A", "Esbjerg", 120, "17:00", "2025-02-13", coordinators, random));
            events.add(createEvent(2, "Festival B", "Aarhus", 300, "18:00", "2025-02-14", coordinators, random));
            events.add(createEvent(3, "Theater C", "Copenhagen", 50, "19:30", "2025-02-15", coordinators, random));
            events.add(createEvent(4, "Conference D", "Odense", 200, "09:00", "2025-02-16", coordinators, random));
            events.add(createEvent(5, "Sports Event E", "Aalborg", 150, "16:30", "2025-02-17", coordinators, random));
            events.add(createEvent(6, "Music Festival F", "Esbjerg", 500, "12:00", "2025-02-18", coordinators, random));
            events.add(createEvent(7, "Exhibition G", "Aarhus", 70, "14:00", "2025-02-19", coordinators, random));
            events.add(createEvent(8, "Comedian H", "Copenhagen", 80, "21:00", "2025-02-20", coordinators, random));
            events.add(createEvent(9, "Workshop I", "Odense", 40, "10:00", "2025-02-21", coordinators, random));
            events.add(createEvent(10, "Talk J", "Aalborg", 90, "11:30", "2025-02-22", coordinators, random));
            events.add(createEvent(11, "Concert K", "Esbjerg", 200, "20:00", "2025-02-23", coordinators, random));
            events.add(createEvent(12, "Festival L", "Aarhus", 350, "18:00", "2025-02-24", coordinators, random));
            events.add(createEvent(13, "Play M", "Copenhagen", 60, "19:00", "2025-02-25", coordinators, random));
            events.add(createEvent(14, "Summit N", "Odense", 180, "09:30", "2025-02-26", coordinators, random));
            events.add(createEvent(15, "Football Match O", "Aalborg", 220, "16:00", "2025-02-27", coordinators, random));
            events.add(createEvent(16, "Rock Concert P", "Esbjerg", 150, "21:30", "2025-02-28", coordinators, random));
            events.add(createEvent(17, "Dance Show Q", "Aarhus", 100, "18:30", "2025-03-01", coordinators, random));
            events.add(createEvent(18, "Product Launch R", "Copenhagen", 60, "13:00", "2025-03-02", coordinators, random));
            events.add(createEvent(19, "Tech Conference S", "Odense", 250, "09:00", "2025-03-03", coordinators, random));
            events.add(createEvent(20, "Art Exhibition T", "Aalborg", 110, "15:30", "2025-03-04", coordinators, random));
        }
        return events;
    }

    private static EventItemModel createEvent(int id, String name, String location, int duration, String time, String date, ObservableList<UserModel> coordinators, Random random) {
        List<UserModel> shuffledCoordinators = new ArrayList<>(coordinators);
        Collections.shuffle(shuffledCoordinators);

        int numberOfCoordinators = random.nextInt(6) + 1;
        List<UserModel> selectedCoordinators = shuffledCoordinators.subList(0, Math.min(numberOfCoordinators, shuffledCoordinators.size()));

        ObservableList<UserModel> observableSelectedCoordinators = FXCollections.observableArrayList(selectedCoordinators);

        return new EventItemModel(id, name, location, duration, time, date, observableSelectedCoordinators);
    }

    public static ObservableList<TicketItemModel> ticketTestData() {
        if (ticketsEvents.isEmpty()) {
            ticketsEvents.add(new TicketEventItemModel(1, "Concert A", 100));
            ticketsEvents.add(new TicketEventItemModel(1, "Concert A", 200));
            ticketsEvents.add(new TicketEventItemModel(1, "Concert A", 300));
            ticketsEvents.add(new TicketEventItemModel(4, "Conference D", 400));
            ticketsEvents.add(new TicketEventItemModel(5, "Sports Event E", 500));
            ticketsEvents.add(new TicketEventItemModel(6, "Music Festival F", 600));
            ticketsEvents.add(new TicketEventItemModel(7, "Exhibition G", 700));
            ticketsEvents.add(new TicketEventItemModel(8, "Comedian H", 800));
            ticketsEvents.add(new TicketEventItemModel(9, "Workshop I", 900));
            ticketsEvents.add(new TicketEventItemModel(10, "Talk J", 1000));
        }

        if (tickets.isEmpty()) {
            Random random = new Random();

            String[] ticketNames = {"Ticket A", "Ticket B", "Ticket C", "Ticket D", "Ticket E", "Ticket F", "Ticket G"};
            TicketType[] ticketTypes = {TicketType.FREE, TicketType.PAID, TicketType.PROMOTIONAL};

            for (String ticketName : ticketNames) {
                int numberOfTicketsToAdd = random.nextInt(5) + 1;
                ObservableList<TicketEventItemModel> selectedTickets = FXCollections.observableArrayList();

                while (selectedTickets.size() < numberOfTicketsToAdd) {
                    int randomIndex = random.nextInt(ticketsEvents.size());
                    TicketEventItemModel randomTicketEvent = ticketsEvents.get(randomIndex);

                    if (!selectedTickets.contains(randomTicketEvent))
                        selectedTickets.add(randomTicketEvent);
                }

                tickets.add(new TicketItemModel(tickets.size() + 1, ticketName, ticketTypes[random.nextInt(ticketTypes.length)], selectedTickets));
            }
        }

        return tickets;
    }

    public static ObservableList<UserModel> userTestData() {
        if (users.isEmpty()) {
            users.add(new UserModel(1, "Jakob", "Hansen", "Aarhus", "test@test.com", UserRole.COORDINATOR));
            users.add(new UserModel(2, "Thomas", "Mikkelsen", "Aarhus","test2@test.com", UserRole.COORDINATOR));
            users.add(new UserModel(3, "Lykke", "Bernberg", "Esbjerg","test3@test.com", UserRole.ADMIN));
            users.add(new UserModel(4, "Thor", "Farlov", "Esbjerg","test4@test.com", UserRole.COORDINATOR));
            users.add(new UserModel(5, "Kasper", "Jakobsen", "Sønderborg","test5@test.com", UserRole.COORDINATOR));
        }

        return users;
    }
}

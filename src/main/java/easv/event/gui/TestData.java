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

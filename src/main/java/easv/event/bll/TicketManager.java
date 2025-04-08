package easv.event.bll;

import easv.event.be.Event;
import easv.event.be.Ticket;
import easv.event.be.TicketEvent;
import easv.event.be.TicketType;
import easv.event.dal.dao.ITicketDAO;
import easv.event.dal.dao.TicketDAO;

import java.io.IOException;
import java.util.List;

public class TicketManager {
    private final ITicketDAO ticketDAO;

    public TicketManager(ITicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
    }

    public TicketManager() throws Exception {
        try {
            this.ticketDAO = new TicketDAO();
        } catch (IOException e) {
            throw new Exception("TicketManager: Kunne ikke oprette forbindelse til databasen");
        }
    }

    public Ticket createTicket(Ticket ticket) throws Exception {
        return ticketDAO.createTicket(ticket);
    }

    public TicketType createTicketType(TicketType ticketType) throws Exception {
        return ticketDAO.createTicketType(ticketType);
    }

    public List<Ticket> getAllTickets() throws Exception {
        return ticketDAO.getAllTickets();
    }

    public List<TicketType> getAllTicketTypes() throws Exception {
        return ticketDAO.getAllTicketTypes();
    }

    public boolean deleteTicketType(TicketType ticketType, List<Ticket> tickets) throws Exception {
        return ticketDAO.deleteTicketType(ticketType, tickets);
    }

    public boolean doesTicketNameExist(String name) throws Exception {
        return ticketDAO.doesTicketNameExist(name);
    }

    public boolean doesTicketTypeExist(String name) throws Exception {
        return ticketDAO.doesTicketTypeExist(name);
    }

    public List<Ticket> getTicketsByType(TicketType ticketType) throws Exception {
        return ticketDAO.getTicketsForType(ticketType);
    }

    public boolean editTicketType(TicketType ticketType) throws Exception {
        return ticketDAO.editTicketType(ticketType);
    }

    public boolean editTicket(Ticket ticket) throws Exception {
        return ticketDAO.editTicket(ticket);
    }

    public List<Event> getEventsByTicket(Ticket ticket) throws Exception {
        return ticketDAO.getEventsByTicket(ticket);
    }

    public boolean deleteTicket(Ticket ticket) throws Exception {
        return ticketDAO.deleteTicket(ticket);
    }

    public List<TicketEvent> addTicketToEvent(TicketEvent ticket, List<Event> eventsToAdd, List<Event> eventsToRemove) throws Exception {
        return ticketDAO.addTicketToEvent(ticket, eventsToAdd, eventsToRemove);
    }

    public List<TicketEvent> getEventTicketsByTicket(Ticket ticket) throws Exception {
        return ticketDAO.getTicketEventByTicket(ticket);
    }
}
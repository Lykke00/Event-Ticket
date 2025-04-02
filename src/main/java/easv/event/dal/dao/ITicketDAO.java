package easv.event.dal.dao;

import easv.event.be.Event;
import easv.event.be.Ticket;
import easv.event.be.TicketType;

import java.util.List;

public interface ITicketDAO {
    Ticket createTicket(Ticket ticket) throws Exception;

    List<Ticket> getAllTickets() throws Exception;

    List<TicketType> getAllTicketTypes() throws Exception;

    TicketType createTicketType(TicketType ticketType) throws Exception;

    boolean deleteTicketType(TicketType ticketType, List<Ticket> tickets) throws Exception;

    boolean doesTicketNameExist(String name) throws Exception;

    boolean doesTicketTypeExist(String name) throws Exception;

    List<Ticket> getTicketsForType(TicketType type) throws Exception;

    boolean editTicketType(TicketType ticketType) throws Exception;

    boolean editTicket(Ticket ticket) throws Exception;

    List<Event> getEventsByTicket(Ticket ticket) throws Exception;

    boolean deleteTicket(Ticket ticket) throws Exception;
}
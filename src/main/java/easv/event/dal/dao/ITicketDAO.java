package easv.event.dal.dao;

import easv.event.be.Event;
import easv.event.be.Ticket;
import easv.event.be.TicketEvent;
import easv.event.be.TicketType;

import java.util.List;
import java.util.UUID;

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

    List<TicketEvent> addTicketToEvent(TicketEvent ticket, List<Event> eventsToAdd, List<Event> eventsToRemove) throws Exception;

    List<TicketEvent> getTicketEventByTicket(Ticket ticket) throws Exception;

    boolean editTicketEvent(TicketEvent entity) throws Exception;

    boolean removeTicketEvent(TicketEvent entity) throws Exception;

    List<TicketEvent> getTicketEventByEvent(Event event) throws Exception;

    boolean sellTicket(TicketEvent ticket, UUID uuid, int amount, String email) throws Exception;
}
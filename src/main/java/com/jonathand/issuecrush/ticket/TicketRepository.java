package com.jonathand.issuecrush.ticket;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Delete tickets created by the specified publisher ID
     *
     * @param id the ID of the publisher
     */
    void deleteByPublisher_Id(Long id);

    /**
     * Retrieves a page of tickets that match the specified
     * organization ID, publisher ID, and ticket body title (case-insensitive)
     *
     * @param id       the organization ID to match
     * @param id1      the publisher ID to match
     * @param title    the title of the ticket body to search for (case-insensitive)
     * @param pageable pagination information
     * @return a page of tickets that match the specified criteria
     */
    Page<Ticket> findByOrganization_IdAndPublisher_IdAndTicketBody_TitleContainsIgnoreCase(
        Long id, Long id1, String title, Pageable pageable);

    /**
     * Retrieves a page of tickets that match the specified organization ID and publisher ID.
     *
     * @param id       the organization ID to match
     * @param id1      the publisher ID to match
     * @param pageable pagination information
     * @return a page of tickets that match the specified organization ID and publisher ID
     */
    Page<Ticket> findByOrganization_IdAndPublisher_Id(Long id, Long id1, Pageable pageable);

    /**
     * Counts the number of tickets in the specified organization that are marked as open.
     *
     * @param id the organization ID to match
     * @return the count of open tickets in the organization
     */
    long countByOrganization_IdAndOpenTrue(Long id);

    /**
     * Counts the number of tickets in the specified organization that are marked as closed.
     *
     * @param id the organization ID to match
     * @return the count of closed tickets in the organization
     */
    long countByOrganization_IdAndOpenFalse(Long id);

    /**
     * Counts the number of tickets in the specified organization created by the publisher that are marked as open.
     *
     * @param id  the organization ID to match
     * @param id1 the publisher ID to match
     * @return the count of open tickets in the organization created by the publisher
     */
    long countByOrganization_IdAndPublisher_IdAndOpenTrue(Long id, Long id1);


    /**
     * Counts the number of tickets in the specified organization created by the publisher that are marked as closed.
     *
     * @param id  the organization ID to match
     * @param id1 the publisher ID to match
     * @return the count of closed tickets in the organization created by the publisher
     */
    long countByOrganization_IdAndPublisher_IdAndOpenFalse(Long id, Long id1);


    /**
     * Counts the number of tickets in the specified organization created by the publisher.
     *
     * @param id  the organization ID to match
     * @param id1 the publisher ID to match
     * @return the count of tickets in the organization created by the publisher
     */
    long countByOrganization_IdAndPublisher_Id(Long id, Long id1);

    /**
     * Retrieves a page of tickets in the specified organization where the ticket body title contains the specified
     * value (case-insensitive).
     *
     * @param id       the organization ID to match
     * @param title    the value to search for in the ticket body title
     * @param pageable pagination information
     * @return a page of tickets in the organization that match the specified title
     */
    Page<Ticket> findByOrganization_IdAndTicketBody_TitleContainsIgnoreCase(
        Long id, String title, Pageable pageable);

    /**
     * Counts the number of tickets in the specified organization.
     *
     * @param id the organization ID to match
     * @return the count of tickets in the organization
     */
    long countByOrganization_Id(Long id);

    /**
     * Retrieves a page of tickets in the specified organization.
     *
     * @param organizationId the organization ID to match
     * @param pageable       pagination information
     * @return a page of tickets in the organization
     */
    @Query("SELECT t "
           + "FROM Ticket t "
           + "WHERE t.organization.id = :organizationId")
    Page<Ticket> findAllTicketsForOrganization(
        @Param("organizationId") Long organizationId, Pageable pageable);

    /**
     * Checks if a user belongs to the organization of a ticket.
     *
     * @param email    the email of the user
     * @param ticketId the ID of the ticket
     * @return true if the user belongs to the ticket's organization, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(ou) > 0 THEN true ELSE false END "
           + "FROM OrganizationUser ou "
           + "INNER JOIN ou.organization o "
           + "INNER JOIN o.tickets t "
           + "WHERE ou.user.email = :email "
           + "AND t.id = :ticketId")
    boolean doesUserBelongToTicketOrganization(
        @Param("email") String email, @Param("ticketId") Long ticketId);

    /**
     * Retrieves a page of tickets published by the specified organization user.
     *
     * @param orgUserId the ID of the organization user
     * @param pageable  pagination information
     * @return a page of tickets published by the organization user
     */
    @Query("SELECT t FROM Ticket t WHERE t.publisher.id = :orgUserId")
    Page<Ticket> getTicketsByOrganizationUser(@Param("orgUserId") Long orgUserId, Pageable pageable);

    /**
     * Deletes tickets published by the specified organization user.
     *
     * @param orgUserId the ID of the organization user
     */
    @Modifying
    @Query("DELETE FROM Ticket " + "t WHERE t.publisher.id = :orgUserId")
    void deleteTicketsByOrganizationUser(@Param("orgUserId") Long orgUserId);

}

package com.jonathand.issuecrush.ticket.comment;

import java.util.List;
import java.util.stream.Collectors;

import com.jonathand.issuecrush.organization.OrganizationAction;
import com.jonathand.issuecrush.organization.OrganizationUtil;
import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.organization.user.OrganizationUserUtil;
import com.jonathand.issuecrush.ticket.Ticket;
import com.jonathand.issuecrush.ticket.TicketUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketCommentService {

    private final TicketCommentRepository ticketCommentRepository;

    private final TicketCommentDTOMapper ticketCommentDTOMapper;

    private final TicketUtil ticketUtil;

    private final TicketCommentUtil ticketCommentUtil;

    private final OrganizationUtil organizationUtil;

    private final OrganizationUserUtil organizationUserUtil;

    /**
     * Retrieves all ticket comments for an organization.
     *
     * @param organizationId the ID of the organization
     * @param pageable       pagination information
     * @return the list of ticket comments for the organization
     */
    // TODO TEST
    public List<TicketCommentDTO> getAllCommentsForOrganization(Long organizationId, Pageable pageable) {
        // Validate organization exists
        organizationUtil.validateOrganizationExistsById(organizationId);

        Page<TicketComment> commentPage = ticketCommentRepository.findByPublisher_Organization_Id(organizationId,
            pageable);

        return commentPage.stream()
            .map(ticketCommentDTOMapper)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all ticket comments for an organization and a specific user.
     *
     * @param organizationId     the ID of the organization
     * @param organizationUserId the ID of the organization user
     * @param pageable           pagination information
     * @return the list of ticket comments for the organization and user
     */
    // TODO TEST
    public List<TicketCommentDTO> getAllCommentsForOrganizationByUser(Long organizationId, Long organizationUserId,
                                                                      Pageable pageable) {
        // Validate organization exists
        organizationUtil.validateOrganizationExistsById(organizationId);

        // Validate organization user exists
        organizationUserUtil.validateOrganizationUserDoesExists(organizationUserId);

        Page<TicketComment> commentPage = ticketCommentRepository.findByPublisher_Id(organizationUserId, pageable);

        return commentPage.stream()
            .map(ticketCommentDTOMapper)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all ticket comments for an organization filtered by ticket comment content.
     *
     * @param organizationId the ID of the organization
     * @param content        the content to filter by
     * @param pageable       pagination information
     * @return the list of ticket comments for the organization filtered by content
     */
    // TODO TEST
    public List<TicketCommentDTO> getAllCommentsForOrganizationByCommentFilter(Long organizationId, String content,
                                                                               Pageable pageable) {
        // Validate organization exists
        organizationUtil.validateOrganizationExistsById(organizationId);

        Page<TicketComment> commentPage =
            ticketCommentRepository.findByPublisher_Organization_IdAndContentContainsIgnoreCase(
            organizationId, content, pageable);

        return commentPage.stream()
            .map(ticketCommentDTOMapper)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all ticket comments for an organization filtered by ticket comment content.
     *
     * @param organizationId the ID of the organization
     * @param content        the content to filter by
     * @param pageable       pagination information
     * @return the list of ticket comments for the organization filtered by ticket comment content
     */
    // TODO TEST
    public List<TicketCommentDTO> getAllCommentsForOrganizationByCommentFilterAndByUser(Long organizationId,
                                                                                        Long organizationUserId,
                                                                                        String content,
                                                                                        Pageable pageable) {
        // Validate organization exists
        organizationUtil.validateOrganizationExistsById(organizationId);

        // Validate organization user exists
        organizationUserUtil.validateOrganizationUserDoesExists(organizationUserId);

        Page<TicketComment> commentPage =
            ticketCommentRepository.findByPublisher_Organization_IdAndPublisher_IdAndContentContainsIgnoreCase(
            organizationId, organizationUserId, content, pageable);

        return commentPage.stream()
            .map(ticketCommentDTOMapper)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all ticket comments for a ticket.
     *
     * @param ticketId the ID of the ticket
     * @param pageable pagination information
     * @return the list of ticket comments for the ticket
     */
    // TODO TEST
    public List<TicketCommentDTO> getCommentsForTicket(Long ticketId, Pageable pageable) {
        // Validate ticket exists
        ticketUtil.validateTicketExists(ticketId);

        return ticketCommentRepository.getCommentsForTicketId(ticketId, pageable)
            .stream()
            .map(ticketCommentDTOMapper)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves all ticket comments for a ticket filtered by ticket comment content.
     *
     * @param ticketId the ID of the ticket
     * @param content  the content to filter by
     * @param pageable pagination information
     * @return the list of ticket comments for the ticket filtered by content
     */
    public List<TicketCommentDTO> getCommentsForTicketByComment(Long ticketId, String content, Pageable pageable) {
        // Validate tickets exists
        ticketUtil.validateTicketExists(ticketId);

        return ticketCommentRepository.findByTicketIdAndContentContainsIgnoreCase(ticketId, content, pageable)
            .stream()
            .map(ticketCommentDTOMapper)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves the count of comments for a ticket.
     *
     * @param ticketId the ID of the ticket
     * @return the count of comments for the ticket
     */
    // TODO TEST
    public Long getCommentCountForTicket(Long ticketId) {
        // Validate ticket exists
        ticketUtil.validateTicketExists(ticketId);

        return ticketCommentRepository.countByTicketId(ticketId);
    }

    /**
     * Creates a ticket comment for a ticket.
     *
     * @param ticketId      the ID of the ticket
     * @param createRequest the request containing the ticket comment details
     * @param email         the email of the user creating the ticket comment
     * @return the created ticket comment DTO
     */
    public TicketCommentDTO createCommentForTicket(Long ticketId, TicketCommentCreateRequest createRequest,
                                                   String email) {
        // Retrieve ticket
        Ticket ticket = ticketUtil.getTicketById(ticketId);

        // Retrieve organization user
        OrganizationUser organizationUser = organizationUserUtil.getOrganizationUserByEmailAndOrganization(
            email, ticket.getOrganization()
                .getId());

        // Create ticket comment
        TicketComment ticketCommentToCreate = TicketComment.builder()
            .ticket(ticket)
            .content(createRequest.content())
            .publisher(organizationUser)
            .build();

        // Save ticket comment
        ticketCommentRepository.save(ticketCommentToCreate);

        return ticketCommentDTOMapper.apply(ticketCommentToCreate);
    }

    /**
     * Retrieves a ticket comment by its ID.
     *
     * @param ticketCommentId the ID of the ticket content
     * @return the ticket content DTO
     */
    public TicketCommentDTO getTicketCommentFromId(Long ticketCommentId) {
        // Retrieve ticket content
        TicketComment ticketComment = ticketCommentUtil.getTicketComment(ticketCommentId);

        return ticketCommentDTOMapper.apply(ticketComment);
    }

    /**
     * Updates a ticket content.
     *
     * @param ticketCommentId the ID of the ticket content
     * @param updateRequest   the request containing the updated content details
     * @param email           the email of the user updating the content
     * @return the updated ticket content DTO
     */
    // TODO CHANGE email to userEmail
    public TicketCommentDTO updateTicketComment(Long ticketCommentId, TicketCommentUpdateRequest updateRequest) {
        // Validate if user is authorized for ticket
        ticketCommentUtil.validateUserAuthorizationForTicketCommentAction(OrganizationAction.UPDATE_TICKET_COMMENT,
            updateRequest.organizationUserId(), ticketCommentId);

        TicketComment ticketComment = ticketCommentUtil.getTicketComment(ticketCommentId);

        // Set the new content from update request
        ticketComment.setContent(updateRequest.content());

        // Save ticket comment
        ticketCommentRepository.save(ticketComment);

        return ticketCommentDTOMapper.apply(ticketComment);
    }

    /**
     * Deletes a ticket content.
     *
     * @param ticketCommentId    the ID of the ticket content
     * @param organizationUserId the ID of the organization user deleting ticket
     * @return the ID of the deleted ticket content
     */
    public Long deleteTicketComment(Long ticketCommentId, Long organizationUserId) {
        // Validate if user is authorized for ticket
        ticketCommentUtil.validateUserAuthorizationForTicketCommentAction(OrganizationAction.DELETE_TICKET_COMMENT,
            organizationUserId, ticketCommentId);

        // Delete ticket comment
        ticketCommentRepository.deleteById(ticketCommentId);
        return ticketCommentId;
    }

}

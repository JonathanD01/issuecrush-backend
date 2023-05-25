package com.jonathand.issuecrush.ticket;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.jonathand.issuecrush.organization.Organization;
import com.jonathand.issuecrush.organization.OrganizationAction;
import com.jonathand.issuecrush.organization.OrganizationUtil;
import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.organization.user.OrganizationUserRepository;
import com.jonathand.issuecrush.organization.user.OrganizationUserUtil;
import com.jonathand.issuecrush.ticket.body.TicketBody;
import com.jonathand.issuecrush.ticket.body.TicketBodyRepository;
import com.jonathand.issuecrush.ticket.property.TicketPriority;
import com.jonathand.issuecrush.ticket.property.TicketProperty;
import com.jonathand.issuecrush.ticket.property.TicketPropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final OrganizationUserRepository organizationUserRepository;

    private final TicketRepository ticketRepository;

    private final TicketPropertyRepository ticketPropertyRepository;

    private final TicketBodyRepository ticketBodyRepository;

    private final TicketDTOMapper ticketDTOMapper;

    private final OrganizationUtil organizationUtil;

    private final OrganizationUserUtil organizationUserUtil;

    private final TicketUtil ticketUtil;

    /**
     * Retrieves a page of tickets for the specified organization.
     *
     * @param organizationId the ID of the organization to retrieve tickets for
     * @param pageable       pagination information
     * @return a list of {@link TicketDTO} objects representing the tickets for the organization
     */
    // TODO TEST
    public List<TicketDTO> getAllTicketsForOrganization(Long organizationId, Pageable pageable) {
        // Validate organization exists
        organizationUtil.validateOrganizationExistsById(organizationId);

        return ticketRepository.findAllTicketsForOrganization(organizationId, pageable)
            .stream()
            .map(ticketDTOMapper)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves a page of tickets for the specified organization and organization user.
     *
     * @param organizationId     the ID of the organization to retrieve tickets for
     * @param organizationUserId the ID of the organization user to filter tickets by
     * @param pageable           pagination information
     * @return a list of TicketDTO objects representing the tickets for the organization and organization user
     */
    // TODO TEST
    public List<TicketDTO> getAllTicketsForOrganizationByOrganizationUser(Long organizationId, Long organizationUserId,
                                                                          Pageable pageable) {
        // Validate organization exists
        organizationUtil.validateOrganizationExistsById(organizationId);

        // Validate organization user exists
        organizationUserUtil.validateOrganizationUserDoesExists(organizationUserId);

        Page<Ticket> ticketPage = ticketRepository.findByOrganization_IdAndPublisher_Id(organizationId,
            organizationUserId, pageable);

        return ticketPage.stream()
            .map(ticketDTOMapper)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves a page of tickets for the specified organization filtered by title.
     *
     * @param organizationId the ID of the organization to retrieve tickets for
     * @param title          the title to filter tickets by
     * @param pageable       pagination information
     * @return a list of TicketDTO objects representing the tickets for the organization filtered by title
     */
    // TODO TEST
    public List<TicketDTO> getAllTicketsForOrganizationByTitle(Long organizationId, String title, Pageable pageable) {
        // Validate organization exists
        organizationUtil.validateOrganizationExistsById(organizationId);

        Page<Ticket> ticketPage = ticketRepository.findByOrganization_IdAndTicketBody_TitleContainsIgnoreCase(
            organizationId, title, pageable);

        return ticketPage.stream()
            .map(ticketDTOMapper)
            .collect(Collectors.toList());
    }

    /**
     * Retrieves a page of tickets for the specified organization and organization user filtered by title.
     *
     * @param organizationId     the ID of the organization to retrieve tickets for
     * @param organizationUserId the ID of the organization user to retrieve tickets for
     * @param title              the title to filter tickets by
     * @param pageable           pagination information
     * @return a list of TicketDTO objects representing the tickets for the organization and organization user
     * filtered by title
     */
    // TODO TEST
    public List<TicketDTO> getAllTicketsForOrganizationByTitleAndByOrganizationUser(Long organizationId,
                                                                                    Long organizationUserId,
                                                                                    String title, Pageable pageable) {
        // Validate organization exists
        organizationUtil.validateOrganizationExistsById(organizationId);

        // Validate organization user exists
        organizationUserUtil.validateOrganizationUserDoesExists(organizationUserId);

        Page<Ticket> ticketPage =
            ticketRepository.findByOrganization_IdAndPublisher_IdAndTicketBody_TitleContainsIgnoreCase(
                organizationId, organizationUserId, title, pageable);

        return ticketPage.stream()
            .map(ticketDTOMapper)
            .collect(Collectors.toList());
    }

    /**
     * Creates a new ticket for the specified organization and user.
     *
     * @param organizationId      the ID of the organization to create the ticket for
     * @param email               the email of the user creating the ticket
     * @param ticketCreateRequest the TicketCreateRequest object containing the ticket information
     * @return the created TicketDTO object
     */
    public TicketDTO createTicket(Long organizationId, String email, TicketCreateRequest ticketCreateRequest) {
        // Retrieve organization
        Organization organization = organizationUtil.getOrganizationById(organizationId);

        // The user trying to create the ticket
        OrganizationUser organizationUser = organizationUserUtil.getOrganizationUserByEmailAndOrganization(
            email,
            organizationId);

        // Create ticket body
        TicketBody ticketBody = TicketBody.builder()
            .title(ticketCreateRequest.title())
            .content(ticketCreateRequest.content())
            .build();

        // Save ticket body
        ticketBodyRepository.save(ticketBody);

        // Construct the ticket priority from create request
        TicketPriority ticketPriority = TicketPriority.fromString(ticketCreateRequest.priority());

        // Construct the ticket department from create request
        TicketDepartment ticketDepartment = TicketDepartment.fromString(ticketCreateRequest.department());

        // Create ticket property
        TicketProperty ticketProperty = TicketProperty.builder()
            .priority(ticketPriority)
            .department(ticketDepartment)
            .build();

        // Save ticket property
        ticketPropertyRepository.save(ticketProperty);

        // Crate ticket
        Ticket ticket = Ticket.builder()
            .organization(organization)
            .publisher(organizationUser)
            .open(ticketCreateRequest.open())
            .ticketBody(ticketBody)
            .ticketProperty(ticketProperty)
            .build();

        // Save ticket
        ticketRepository.save(ticket);
        return ticketDTOMapper.apply(ticket);
    }

    /**
     * Retrieves the ticket with the specified ID.
     *
     * @param ticketId the ID of the ticket to retrieve
     * @return the TicketDTO object representing the retrieved ticket
     */
    public TicketDTO getTicket(Long ticketId) {
        // Retrieve ticket
        Ticket ticket = ticketUtil.getTicketById(ticketId);
        return ticketDTOMapper.apply(ticket);
    }

    /**
     * Updates the ticket with the specified ID based on the provided update request.
     *
     * @param ticketId            the ID of the ticket to update
     * @param ticketUpdateRequest the TicketUpdateRequest containing the update information
     * @return the TicketDTO object representing the updated ticket
     */
    public TicketDTO updateTicket(Long ticketId, TicketUpdateRequest ticketUpdateRequest) {
        // Validate user have permission to update ticket
        ticketUtil.validateUserAuthorizationForTicketAction(OrganizationAction.UPDATE_TICKET,
            ticketUpdateRequest.organizationUserId(), ticketId);

        Ticket ticket = ticketUtil.getTicketById(ticketId);

        // Update title if present
        if (ticketUpdateRequest.title() != null) {
            ticket.getTicketBody()
                .setTitle(ticketUpdateRequest.title());
        }

        // Update content if present
        if (ticketUpdateRequest.content() != null) {
            ticket.getTicketBody()
                .setContent(ticketUpdateRequest.content());
        }

        // Update priority if present
        if (ticketUpdateRequest.priority() != null) {
            TicketPriority ticketPriority = TicketPriority.fromString(ticketUpdateRequest.priority());
            ticket.getTicketProperty()
                .setPriority(ticketPriority);
        }

        // Update department if present
        if (ticketUpdateRequest.department() != null) {
            TicketDepartment ticketDepartment = TicketDepartment.fromString(ticketUpdateRequest.department());
            ticket.getTicketProperty()
                .setDepartment(ticketDepartment);
        }

        // Set open status
        ticket.setOpen(ticketUpdateRequest.open());

        Long assignedAgentId = ticketUpdateRequest.assigned_agent();

        // If assigned agent id is null, then set it to null
        // TODO IMPROVE THIS
        Optional<OrganizationUser> assignedUserAgent;
        if (assignedAgentId != null) {
            assignedUserAgent = organizationUserRepository.findById(assignedAgentId);
            assignedUserAgent.ifPresent(user -> ticket.getTicketProperty()
                .setAssigned_agent(user));
        } else {
            ticket.getTicketProperty()
                .setAssigned_agent(null);
        }

        // Save ticket
        ticketRepository.save(ticket);
        return ticketDTOMapper.apply(ticket);
    }

    /**
     * Deletes the ticket with the specified ID if the user has permission to do so.
     *
     * @param ticketId           the ID of the ticket to delete
     * @param organizationUserId the ID of the organization user attempting to delete the ticket
     * @return the ID of the deleted ticket
     */
    public Long deleteTicket(Long organizationUserId, Long ticketId) {
        // Validate user have permission to delete ticket
        ticketUtil.validateUserAuthorizationForTicketAction(OrganizationAction.DELETE_TICKET, organizationUserId,
            ticketId);

        // Delete ticket
        ticketRepository.deleteById(ticketId);
        return ticketId;
    }

}

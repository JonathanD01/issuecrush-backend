package com.jonathand.issuecrush.ticket.comment;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.organization.Organization;
import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.organization.user.OrganizationUserRepository;
import com.jonathand.issuecrush.organization.user.OrganizationUserUtil;
import com.jonathand.issuecrush.ticket.Ticket;
import com.jonathand.issuecrush.ticket.TicketRepository;
import com.jonathand.issuecrush.ticket.TicketUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TicketCommentServiceTest {

    private final Faker faker = new Faker();

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketCommentRepository ticketCommentRepository;

    @Mock
    private OrganizationUserRepository organizationUserRepository;

    @Mock
    private TicketCommentDTOMapper ticketCommentDTOMapper;

    @Mock
    private OrganizationUserUtil organizationUserUtil;

    @Mock
    private TicketUtil ticketUtil;

    @Mock
    private TicketCommentUtil ticketCommentUtil;

    @InjectMocks
    private TicketCommentService underTest;

    @Test
    void canCreateTicketCommentForExistingTicket() {
        // given
        Long ticketId = faker.random()
            .nextLong(50L);
        String email = faker.internet()
            .emailAddress();
        Long organizationId = faker.random()
            .nextLong(50L);

        TicketCommentCreateRequest createRequest = new TicketCommentCreateRequest(faker.lorem()
            .sentence(30));

        Organization organization = mock(Organization.class);
        OrganizationUser organizationUser = mock(OrganizationUser.class);
        Ticket ticket = mock(Ticket.class);
        TicketComment ticketComment = mock(TicketComment.class);

        when(ticketUtil.getTicketById(ticketId)).thenReturn(ticket);

        when(ticket.getOrganization()).thenReturn(organization);

        when(ticket.getOrganization()
            .getId()).thenReturn(organizationId);

        when(organizationUserUtil.getOrganizationUserByEmailAndOrganization(email, organizationId)).thenReturn(
            organizationUser);

        when(ticketCommentRepository.save(any())).thenReturn(ticketComment);


        // when
        underTest.createCommentForTicket(ticketId, createRequest, email);

        // then
        verify(ticketCommentRepository).save(any());
        verify(ticketCommentDTOMapper).apply(any());
    }

    @Test
    void canGetTicketCommentFromExistingId() {
        // given
        Long ticketCommentId = faker.random()
            .nextLong(50L);

        TicketComment ticketComment = mock(TicketComment.class);

        when(ticketCommentUtil.getTicketComment(ticketCommentId)).thenReturn(ticketComment);

        // when
        underTest.getTicketCommentFromId(ticketCommentId);

        // then
        verify(ticketCommentDTOMapper).apply(ticketComment);
    }

    @Test
    void canDeleteTicketComment() {
        // given
        Long organizationUserId = faker.random()
            .nextLong(50L);
        Long ticketCommentId = faker.random()
            .nextLong(50L);

        // when
        underTest.deleteTicketComment(ticketCommentId, organizationUserId);

        // then
        verify(ticketCommentRepository).deleteById(ticketCommentId);
    }

}
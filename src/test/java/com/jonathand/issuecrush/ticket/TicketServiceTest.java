package com.jonathand.issuecrush.ticket;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.organization.Organization;
import com.jonathand.issuecrush.organization.OrganizationRepository;
import com.jonathand.issuecrush.organization.OrganizationUtil;
import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.organization.user.OrganizationUserRepository;
import com.jonathand.issuecrush.organization.user.OrganizationUserUtil;
import com.jonathand.issuecrush.ticket.body.TicketBody;
import com.jonathand.issuecrush.ticket.body.TicketBodyDTOMapper;
import com.jonathand.issuecrush.ticket.body.TicketBodyRepository;
import com.jonathand.issuecrush.ticket.property.TicketPriority;
import com.jonathand.issuecrush.ticket.property.TicketProperty;
import com.jonathand.issuecrush.ticket.property.TicketPropertyDTOMapper;
import com.jonathand.issuecrush.ticket.property.TicketPropertyRepository;
import com.jonathand.issuecrush.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TicketServiceTest {

    private final Faker faker = new Faker();

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private OrganizationUserRepository organizationUserRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketPropertyRepository ticketPropertyRepository;

    @Mock
    private TicketBodyRepository ticketBodyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketDTOMapper ticketDTOMapper;

    @Mock
    private TicketBodyDTOMapper ticketBodyDTOMapper;

    @Mock
    private TicketPropertyDTOMapper ticketPropertyDTOMapper;

    @Mock
    private OrganizationUtil organizationUtil;

    @Mock
    private OrganizationUserUtil organizationUserUtil;

    @Mock
    private TicketUtil ticketUtil;

    @InjectMocks
    private TicketService underTest;

    @Test
    void canGetAllTicketsForOrganization() {
        // given
        Long organizationId = faker.random()
            .nextLong(50L);
        Pageable pageable = PageRequest.of(0, 10);

        List<Ticket> ticketList = List.of(mock(Ticket.class));

        Page<Ticket> tickets = new PageImpl<>(ticketList);

        when(ticketRepository.findAllTicketsForOrganization(organizationId, pageable)).thenReturn(tickets);

        // when
        underTest.getAllTicketsForOrganization(organizationId, pageable);

        // then
        verify(ticketRepository).findAllTicketsForOrganization(organizationId, pageable);
    }

    // TODO GET TICKETS FOR USER TOO...

    /*@Test
    void itWillThrowWhenOrganizationNotFoundWhenGettingAllTickets() {
        // given
        Long organizationId = faker.random()
            .nextLong(50L);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        // then
        assertThatThrownBy(() -> underTest.getAllTicketsForOrganization(organizationId, pageable)).isInstanceOf(
                OrganizationNotFoundException.class)
            .hasMessageContaining("Organization with id " + organizationId + " does not exist");

        verify(ticketRepository, never()).findAllTicketsForOrganization(anyLong(), any());
    }*/

    @Test
    void canCreateTicket() {
        // given
        Long organizationId = faker.random()
            .nextLong(50L);
        String email = faker.internet()
            .emailAddress();
        TicketCreateRequest createRequest = new TicketCreateRequest(faker.lorem()
            .sentence(10), faker.lorem()
            .sentence(30), faker.options()
            .option(TicketPriority.values())
            .name(), faker.options()
            .option(TicketDepartment.values())
            .name(), faker.bool()
            .bool());

        Organization organization = mock(Organization.class);

        OrganizationUser organizationUser = mock(OrganizationUser.class);

        TicketBody ticketBody = mock(TicketBody.class);
        TicketProperty ticketProperty = mock(TicketProperty.class);
        Ticket ticket = mock(Ticket.class);

        when(organizationUtil.getOrganizationById(organizationId)).thenReturn(organization);

        when(organizationUserUtil.getOrganizationUserByEmailAndOrganization(email, organizationId)).thenReturn(
            organizationUser);

        when(ticketBodyRepository.save(any())).thenReturn(ticketBody);

        when(ticketPropertyRepository.save(any())).thenReturn(ticketProperty);

        when(ticketRepository.save(any())).thenReturn(ticket);

        // when
        underTest.createTicket(organizationId, email, createRequest);

        // then
        verify(ticketBodyRepository).save(any());
        verify(ticketPropertyRepository).save(any());
        verify(ticketRepository).save(any());
        verify(ticketDTOMapper).apply(any());
    }

    @Test
    void canGetTicket() {
        // given
        Long ticketId = faker.random()
            .nextLong(50L);

        Ticket ticket = mock(Ticket.class);

        when(ticketUtil.getTicketById(ticketId)).thenReturn(ticket);

        // when
        underTest.getTicket(ticketId);

        // then
        verify(ticketDTOMapper).apply(ticket);
    }

    @Test
    void canUpdateTicket() {
        // given
        Long ticketId = faker.random()
            .nextLong(50L);
        Long organizationUserId = faker.random()
            .nextLong(50L);

        Ticket ticket = mock(Ticket.class);
        TicketBody ticketBody = mock(TicketBody.class);
        TicketProperty ticketProperty = mock(TicketProperty.class);

        TicketUpdateRequest updateRequest = new TicketUpdateRequest(organizationUserId, faker.lorem()
            .sentence(10), faker.lorem()
            .sentence(30), faker.options()
            .option(TicketPriority.values())
            .name(), faker.options()
            .option(TicketDepartment.values())
            .name(), faker.bool()
            .bool(), null);

        when(ticketUtil.getTicketById(ticketId)).thenReturn(ticket);

        when(ticket.getTicketBody()).thenReturn(ticketBody);

        when(ticket.getTicketProperty()).thenReturn(ticketProperty);

        // then
        underTest.updateTicket(ticketId, updateRequest);

        // when
        verify(ticketRepository).save(any());
        verify(ticketDTOMapper).apply(any());
    }

    @Test
    void canDeleteTicket() {
        // given
        Long ticketId = faker.random()
            .nextLong(50L);
        Long organizationUserId = faker.random()
            .nextLong(50L);

        // when
        underTest.deleteTicket(organizationUserId, ticketId);

        // then
        verify(ticketRepository).deleteById(ticketId);
    }

}
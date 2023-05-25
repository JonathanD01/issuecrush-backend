package com.jonathand.issuecrush.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.organization.OrganizationAction;
import com.jonathand.issuecrush.organization.OrganizationRole;
import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.organization.user.OrganizationUserUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TicketUtilTest {

    private final Faker faker = new Faker();

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private OrganizationUserUtil organizationUserUtil;

    @InjectMocks
    private TicketUtil underTest;

    @Test
    void canGetTicketById() {
        // given
        Long ticketId = faker.random()
            .nextLong(100L);

        Optional<Ticket> optionalTicket = Optional.of(mock(Ticket.class));

        when(ticketRepository.findById(ticketId)).thenReturn(optionalTicket);

        // when
        Ticket ticket = underTest.getTicketById(ticketId);

        // then
        assertThat(ticket).isNotNull();
    }

    @Test
    void canGetTicketByIdWillThrowWhenNotFound() {
        // given
        Long ticketId = faker.random()
            .nextLong(100L);

        // when
        // then
        assertThatThrownBy(() -> underTest.getTicketById(ticketId)).isInstanceOf(TicketNotFoundException.class)
            .hasMessageContaining("Ticket with id " + ticketId + " was not found");
    }

    @Test
    void canValidateTicketExists() {
        // given
        Long ticketId = faker.random()
            .nextLong(100L);

        when(ticketRepository.existsById(ticketId)).thenReturn(true);

        // when
        // then
        assertDoesNotThrow(() -> underTest.validateTicketExists(ticketId));
    }

    @Test
    void canValidateTicketExistsWillThrowWhenNotFound() {
        // given
        Long ticketId = faker.random()
            .nextLong(100L);

        // when
        // then
        assertThatThrownBy(() -> underTest.validateTicketExists(ticketId)).isInstanceOf(TicketNotFoundException.class)
            .hasMessageContaining("Ticket with id " + ticketId + " was " + "not found");
    }

    @Test
    void validateUserAuthorizationForTicketUpdate() {
        // given
        Long ticketId = faker.random()
            .nextLong(100L);
        Long organizationUserId = faker.random()
            .nextLong(100L);

        Ticket ticket = mock(Ticket.class);
        OrganizationUser organizationUser = mock(OrganizationUser.class);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        when(organizationUserUtil.getOrganizationUserById(organizationUserId)).thenReturn(organizationUser);

        when(organizationUser.getRole()).thenReturn(OrganizationRole.ADMIN);

        when(ticket.getPublisher()).thenReturn(organizationUser);

        when(ticket.getPublisher()
            .getId()).thenReturn(organizationUserId);

        when(organizationUser.getId()).thenReturn(organizationUserId);

        // when
        // then
        assertDoesNotThrow(() -> underTest.validateUserAuthorizationForTicketAction(OrganizationAction.UPDATE_TICKET,
            organizationUserId, ticketId));
    }

    @Test
    void validateUserAuthorizationForTicketUpdateWillThrowWhenNotAuthorized() {
        // given
        Long ticketId = faker.random()
            .nextLong(100L);
        Long organizationUserId = faker.random()
            .nextLong(50L);

        OrganizationRole role = OrganizationAction.UPDATE_TICKET.getRoleRequired();

        Ticket ticket = mock(Ticket.class);
        OrganizationUser organizationUser = mock(OrganizationUser.class);
        OrganizationUser organizationUser2 = mock(OrganizationUser.class);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        when(organizationUserUtil.getOrganizationUserById(organizationUserId)).thenReturn(organizationUser);

        when(organizationUser.getRole()).thenReturn(OrganizationRole.MEMBER);

        when(ticket.getPublisher()).thenReturn(organizationUser2);

        when(ticket.getPublisher()
            .getId()).thenReturn(organizationUserId);

        when(organizationUser.getId()).thenReturn(organizationUserId + 1L);

        // when
        // then
        assertThatThrownBy(() -> underTest.validateUserAuthorizationForTicketAction(OrganizationAction.UPDATE_TICKET,
            organizationUserId, ticketId)).isInstanceOf(TicketUnauthorizedActionException.class)
            .hasMessageContaining("You need the '" + role + "' role to do this");
    }

}
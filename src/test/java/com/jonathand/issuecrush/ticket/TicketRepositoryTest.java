package com.jonathand.issuecrush.ticket;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.organization.Organization;
import com.jonathand.issuecrush.organization.OrganizationRepository;
import com.jonathand.issuecrush.organization.OrganizationRole;
import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.organization.user.OrganizationUserRepository;
import com.jonathand.issuecrush.ticket.body.TicketBody;
import com.jonathand.issuecrush.ticket.body.TicketBodyRepository;
import com.jonathand.issuecrush.ticket.property.TicketPriority;
import com.jonathand.issuecrush.ticket.property.TicketProperty;
import com.jonathand.issuecrush.ticket.property.TicketPropertyRepository;
import com.jonathand.issuecrush.user.User;
import com.jonathand.issuecrush.user.UserRepository;
import com.jonathand.issuecrush.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TicketRepositoryTest {

    private final Faker faker = new Faker();

    @Autowired
    private TicketRepository underTest;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationUserRepository organizationUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketBodyRepository ticketBodyRepository;

    @Autowired
    private TicketPropertyRepository ticketPropertyRepository;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        organizationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void itShouldFindAllTicketsForOrganization() {
        // given
        String email = faker.internet()
            .emailAddress();
        String password = faker.internet()
            .password();


        User user = User.builder()
            .email(email)
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .password(password)
            .userRole(UserRole.USER)
            .build();

        user = userRepository.save(user);

        Organization organization = Organization.builder()
            .name(faker.company()
                .name())
            .creator(user)
            .build();

        organization = organizationRepository.save(organization);

        int ticketsToCreate = faker.random()
            .nextInt(1, 5);
        for (int i = 0; i < ticketsToCreate; i++) {
            TicketBody ticketBody = TicketBody.builder()
                .title("a")
                .content("b")
                .build();

            TicketProperty ticketProperty = TicketProperty.builder()
                .department(TicketDepartment.IT)
                .priority(TicketPriority.LOW)
                .build();

            ticketBody = ticketBodyRepository.save(ticketBody);
            ticketProperty = ticketPropertyRepository.save(ticketProperty);

            Ticket ticket = Ticket.builder()
                .organization(organization)
                .ticketBody(ticketBody)
                .ticketProperty(ticketProperty)
                .build();

            underTest.save(ticket);
        }

        Pageable pageable = PageRequest.of(0, ticketsToCreate);

        // when
        Page<Ticket> expected = underTest.findAllTicketsForOrganization(organization.getId(), pageable);

        // then
        assertThat(expected.getTotalPages()).isEqualTo(1);
        assertThat(expected.getTotalElements()).isEqualTo(ticketsToCreate);
    }

    @Test
    void itShouldNotFindAnyTicketsForOrganization() {
        // given
        Long organizationId = 10L;

        Pageable pageable = PageRequest.of(0, 10);

        User user = User.builder()
            .email(faker.internet()
                .emailAddress())
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .password(faker.internet()
                .password())
            .userRole(UserRole.USER)
            .build();

        user = userRepository.save(user);

        Organization organization = Organization.builder()
            .id(organizationId)
            .name(faker.company()
                .name())
            .creator(user)
            .build();

        organizationRepository.save(organization);

        // when
        Page<Ticket> expected = underTest.findAllTicketsForOrganization(organizationId, pageable);

        // then
        assertThat(expected.getTotalPages()).isEqualTo(0);
        assertThat(expected.getTotalElements()).isEqualTo(0);
    }

    @Test
    void itShouldBeTrueIfUserBelongToTicketOrganization() {
        String email = faker.internet()
            .emailAddress();
        String password = faker.internet()
            .password();

        User user = User.builder()
            .email(email)
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .password(password)
            .userRole(UserRole.USER)
            .build();

        user = userRepository.save(user);

        Organization organization = Organization.builder()
            .name(faker.company()
                .name())
            .creator(user)
            .build();

        organization = organizationRepository.save(organization);

        OrganizationUser organizationUser = OrganizationUser.builder()
            .user(user)
            .role(OrganizationRole.OWNER)
            .organization(organization)
            .build();

        organizationUser = organizationUserRepository.save(organizationUser);

        TicketBody ticketBody = TicketBody.builder()
            .title("a")
            .content("b")
            .build();

        TicketProperty ticketProperty = TicketProperty.builder()
            .department(TicketDepartment.IT)
            .priority(TicketPriority.LOW)
            .build();

        ticketBody = ticketBodyRepository.save(ticketBody);
        ticketProperty = ticketPropertyRepository.save(ticketProperty);

        Ticket fakeTicket = Ticket.builder()
            .organization(organization)
            .publisher(organizationUser)
            .ticketBody(ticketBody)
            .ticketProperty(ticketProperty)
            .build();

        Ticket realTicket = underTest.save(fakeTicket);

        // when
        boolean expected = underTest.doesUserBelongToTicketOrganization(email, realTicket.getId());

        // then
        assertTrue(expected);
    }

    @Test
    void itShouldBeFalseIfUserDoesNotBelongToTicketOrganization() {
        // given
        User user = User.builder()
            .email(faker.internet()
                .emailAddress())
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .password(faker.internet()
                .password())
            .userRole(UserRole.USER)
            .build();

        user = userRepository.save(user);

        User user2 = User.builder()
            .email(faker.internet()
                .emailAddress())
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .password(faker.internet()
                .password())
            .userRole(UserRole.USER)
            .build();

        user2 = userRepository.save(user2);

        Organization organization = Organization.builder()
            .name(faker.company()
                .name())
            .creator(user)
            .build();

        organization = organizationRepository.save(organization);

        Organization organization2 = Organization.builder()
            .name(faker.company()
                .name())
            .creator(user2)
            .build();

        organization2 = organizationRepository.save(organization2);

        OrganizationUser organizationUser = OrganizationUser.builder()
            .user(user)
            .role(OrganizationRole.MEMBER)
            .organization(organization)
            .build();

        organizationUser = organizationUserRepository.save(organizationUser);

        OrganizationUser organizationUser2 = OrganizationUser.builder()
            .user(user2)
            .role(OrganizationRole.MODERATOR)
            .organization(organization2)
            .build();

        OrganizationUser realOrganizationUser_2 = organizationUserRepository.save(organizationUser2);

        TicketBody ticketBody = TicketBody.builder()
            .title("a")
            .content("b")
            .build();

        TicketProperty ticketProperty = TicketProperty.builder()
            .department(TicketDepartment.IT)
            .priority(TicketPriority.LOW)
            .build();

        ticketBody = ticketBodyRepository.save(ticketBody);
        ticketProperty = ticketPropertyRepository.save(ticketProperty);

        Ticket fakeTicket = Ticket.builder()
            .organization(organization)
            .publisher(organizationUser)
            .ticketBody(ticketBody)
            .ticketProperty(ticketProperty)
            .build();

        Ticket realTicket = underTest.save(fakeTicket);

        // when
        boolean expected = underTest.doesUserBelongToTicketOrganization(realOrganizationUser_2.getUser()
            .getEmail(), realTicket.getId());

        // then
        assertFalse(expected);
    }

    @Test
    void itShouldGetTicketsByOrganizationUser() {
        // given
        String email = faker.internet()
            .emailAddress();
        String password = faker.internet()
            .password();

        User user = User.builder()
            .email(email)
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .password(faker.internet()
                .password())
            .userRole(UserRole.USER)
            .build();

        user = userRepository.save(user);

        Organization organization = Organization.builder()
            .id(faker.random()
                .nextLong(50L))
            .name(faker.company()
                .name())
            .build();

        organization = organizationRepository.save(organization);

        OrganizationUser organizationUser = OrganizationUser.builder()
            .user(user)
            .role(OrganizationRole.MEMBER)
            .organization(organization)
            .build();

        organizationUser = organizationUserRepository.save(organizationUser);

        int ticketsToCreate = faker.random()
            .nextInt(1, 5);
        for (int i = 0; i < ticketsToCreate; i++) {
            TicketBody ticketBody = TicketBody.builder()
                .title("a")
                .content("b")
                .build();

            TicketProperty ticketProperty = TicketProperty.builder()
                .department(TicketDepartment.IT)
                .priority(TicketPriority.LOW)
                .build();

            ticketBody = ticketBodyRepository.save(ticketBody);
            ticketProperty = ticketPropertyRepository.save(ticketProperty);

            Ticket ticket = Ticket.builder()
                .organization(organization)
                .publisher(organizationUser)
                .ticketBody(ticketBody)
                .ticketProperty(ticketProperty)
                .build();

            underTest.save(ticket);
        }

        Pageable pageable = PageRequest.of(0, ticketsToCreate);

        // when
        Page<Ticket> ticketsByOrgUser = underTest.getTicketsByOrganizationUser(organizationUser.getId(), pageable);

        // then
        assertThat(ticketsByOrgUser).isNotNull();
        assertThat(ticketsByOrgUser.getTotalPages()).isEqualTo(1);
        assertThat(ticketsByOrgUser.getTotalElements()).isEqualTo(ticketsToCreate);
    }

    @Test
    void itShouldGetNoTicketsByOrganizationUser() {
        // given
        String email = faker.internet()
            .emailAddress();

        User user = User.builder()
            .email(email)
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .password(faker.internet()
                .password())
            .userRole(UserRole.USER)
            .build();

        user = userRepository.save(user);

        Organization organization = Organization.builder()
            .id(faker.random()
                .nextLong(50L))
            .name(faker.company()
                .name())
            .build();

        organization = organizationRepository.save(organization);

        OrganizationUser organizationUser = OrganizationUser.builder()
            .user(user)
            .role(OrganizationRole.MEMBER)
            .organization(organization)
            .build();

        organizationUser = organizationUserRepository.save(organizationUser);

        Pageable pageable = PageRequest.of(0, 5);

        // when
        Page<Ticket> ticketsByOrgUser = underTest.getTicketsByOrganizationUser(organizationUser.getId(), pageable);

        // then
        assertThat(ticketsByOrgUser).isNotNull();
        assertThat(ticketsByOrgUser.getTotalPages()).isEqualTo(0);
        assertThat(ticketsByOrgUser.getTotalElements()).isEqualTo(0);
    }

    @Test
    void itShouldDeleteTicketsByOrganizationUser() {
        // given
        String email = faker.internet()
            .emailAddress();

        User user = User.builder()
            .email(email)
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .password(faker.internet()
                .password())
            .userRole(UserRole.USER)
            .build();

        user = userRepository.save(user);

        Organization organization = Organization.builder()
            .id(faker.random()
                .nextLong(50L))
            .name(faker.company()
                .name())
            .build();

        organization = organizationRepository.save(organization);

        OrganizationUser organizationUser = OrganizationUser.builder()
            .user(user)
            .role(OrganizationRole.MEMBER)
            .organization(organization)
            .build();

        organizationUser = organizationUserRepository.save(organizationUser);

        int ticketsToCreate = faker.random()
            .nextInt(1, 5);
        for (int i = 0; i < ticketsToCreate; i++) {
            TicketBody ticketBody = TicketBody.builder()
                .title("a")
                .content("b")
                .build();

            TicketProperty ticketProperty = TicketProperty.builder()
                .department(TicketDepartment.IT)
                .priority(TicketPriority.LOW)
                .build();

            ticketBody = ticketBodyRepository.save(ticketBody);
            ticketProperty = ticketPropertyRepository.save(ticketProperty);

            Ticket ticket = Ticket.builder()
                .organization(organization)
                .publisher(organizationUser)
                .ticketBody(ticketBody)
                .ticketProperty(ticketProperty)
                .build();

            underTest.save(ticket);
        }

        Pageable pageable = PageRequest.of(0, ticketsToCreate);

        // when
        underTest.deleteTicketsByOrganizationUser(organizationUser.getId());

        Page<Ticket> ticketsByOrgUser = underTest.getTicketsByOrganizationUser(organizationUser.getId(), pageable);

        // then
        assertThat(ticketsByOrgUser).isNotNull();
        assertThat(ticketsByOrgUser.getTotalPages()).isEqualTo(0);
        assertThat(ticketsByOrgUser.getTotalElements()).isEqualTo(0);
    }

    // TODO CAN BE IMPROVED?
    @Test
    void itWillBeTrueIfUserIsInTicketOrganization() {
        // given
        String email = faker.internet()
            .emailAddress();

        User user = User.builder()
            .email(email)
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .password(faker.internet()
                .password())
            .userRole(UserRole.USER)
            .build();

        user = userRepository.save(user);

        Organization organization = Organization.builder()
            .id(faker.random()
                .nextLong(50L))
            .name(faker.company()
                .name())
            .build();

        organization = organizationRepository.save(organization);

        OrganizationUser organizationUser = OrganizationUser.builder()
            .user(user)
            .role(OrganizationRole.MEMBER)
            .organization(organization)
            .build();

        organizationUser = organizationUserRepository.save(organizationUser);

        TicketBody ticketBody = TicketBody.builder()
            .title("a")
            .content("b")
            .build();

        TicketProperty ticketProperty = TicketProperty.builder()
            .department(TicketDepartment.IT)
            .priority(TicketPriority.LOW)
            .build();

        ticketBody = ticketBodyRepository.save(ticketBody);
        ticketProperty = ticketPropertyRepository.save(ticketProperty);

        Ticket ticket = Ticket.builder()
            .organization(organization)
            .publisher(organizationUser)
            .ticketBody(ticketBody)
            .ticketProperty(ticketProperty)
            .build();

        ticket = underTest.save(ticket);

        // when
        boolean expected = underTest.doesUserBelongToTicketOrganization(organizationUser.getUser()
            .getEmail(), ticket.getId());

        // then
        assertTrue(expected);
    }

}
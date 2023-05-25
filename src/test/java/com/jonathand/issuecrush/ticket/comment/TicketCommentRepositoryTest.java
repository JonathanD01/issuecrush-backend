package com.jonathand.issuecrush.ticket.comment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.organization.Organization;
import com.jonathand.issuecrush.organization.OrganizationRepository;
import com.jonathand.issuecrush.organization.OrganizationRole;
import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.organization.user.OrganizationUserRepository;
import com.jonathand.issuecrush.ticket.Ticket;
import com.jonathand.issuecrush.ticket.TicketDepartment;
import com.jonathand.issuecrush.ticket.TicketRepository;
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
class TicketCommentRepositoryTest {

    private final Faker faker = new Faker();

    @Autowired
    private TicketCommentRepository underTest;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationUserRepository organizationUserRepository;

    @Autowired
    private TicketBodyRepository ticketBodyRepository;

    @Autowired
    private TicketPropertyRepository ticketPropertyRepository;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
        ticketRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void itShouldGetCommentsForTicketId() {
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

        Organization realOrganization = organizationRepository.save(organization);

        OrganizationUser organizationUser = OrganizationUser.builder()
            .user(user)
            .role(OrganizationRole.OWNER)
            .organization(realOrganization)
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
            .organization(realOrganization)
            .publisher(organizationUser)
            .ticketBody(ticketBody)
            .ticketProperty(ticketProperty)
            .build();

        ticket = ticketRepository.save(ticket);

        int ticketCommentsToCreate = faker.random()
            .nextInt(1, 10);
        for (int i = 0; i < ticketCommentsToCreate; i++) {
            TicketComment ticketComment = TicketComment.builder()
                .content(faker.lorem()
                    .sentence(10))
                .ticket(ticket)
                .publisher(organizationUser)
                .build();

            underTest.save(ticketComment);
        }

        Pageable pageable = PageRequest.of(0, ticketCommentsToCreate);

        // when
        Page<TicketComment> expected = underTest.getCommentsForTicketId(ticket.getId(), pageable);

        // then
        assertThat(expected.getTotalPages()).isEqualTo(1);
        assertThat(expected.getTotalElements()).isEqualTo(ticketCommentsToCreate);
    }

    @Test
    void itShouldBeTrueIfUserBelongToTicketCommentOrganization() {
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

        Organization organization = Organization.builder()
            .name(faker.company()
                .name())
            .creator(user)
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

        ticket = ticketRepository.save(ticket);

        TicketComment ticketComment = TicketComment.builder()
            .content(faker.lorem()
                .sentence(10))
            .ticket(ticket)
            .publisher(organizationUser)
            .build();

        ticketComment = underTest.save(ticketComment);

        // when
        boolean expected = underTest.doesUserBelongToTicketCommentOrganization(organizationUser.getUser()
            .getEmail(), ticketComment.getId());

        // then
        assertTrue(expected);
    }

    @Test
    void itShouldBeFalseIfUserNotBelongToTicketCommentOrganization() {
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

        Organization organization = Organization.builder()
            .name(faker.company()
                .name())
            .creator(user)
            .build();

        organization = organizationRepository.save(organization);

        OrganizationUser organizationUser = OrganizationUser.builder()
            .user(user)
            .role(OrganizationRole.MEMBER)
            .organization(organization)
            .build();

        organizationUser = organizationUserRepository.save(organizationUser);

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

        Organization organization2 = Organization.builder()
            .name(faker.company()
                .name())
            .creator(user2)
            .build();

        organization2 = organizationRepository.save(organization2);

        OrganizationUser organizationUser2 = OrganizationUser.builder()
            .user(user2)
            .role(OrganizationRole.MEMBER)
            .organization(organization2)
            .build();

        organizationUser2 = organizationUserRepository.save(organizationUser2);

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

        ticket = ticketRepository.save(ticket);

        TicketComment ticketComment = TicketComment.builder()
            .content(faker.lorem()
                .sentence(10))
            .ticket(ticket)
            .publisher(organizationUser)
            .build();

        ticketComment = underTest.save(ticketComment);

        // when
        boolean expected = underTest.doesUserBelongToTicketCommentOrganization(organizationUser2.getUser()
            .getEmail(), ticketComment.getId());

        // then
        assertFalse(expected);
    }

    // TODO TEST BOTH countByOrganization_IdAndOrganization_Tickets_OpenFalse
}
package com.jonathand.issuecrush.organizationuser;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.organization.Organization;
import com.jonathand.issuecrush.organization.OrganizationRepository;
import com.jonathand.issuecrush.organization.OrganizationRole;
import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.organization.user.OrganizationUserRepository;
import com.jonathand.issuecrush.ticket.TicketRepository;
import com.jonathand.issuecrush.ticket.body.TicketBodyRepository;
import com.jonathand.issuecrush.ticket.property.TicketPropertyRepository;
import com.jonathand.issuecrush.user.User;
import com.jonathand.issuecrush.user.UserRepository;
import com.jonathand.issuecrush.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrganizationUserRepositoryTest {

    private final Faker faker = new Faker();

    @Autowired
    private OrganizationUserRepository underTest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private TicketRepository ticketRepository;

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
    void itShouldFindOrganizationUserByEmailAndOrganization() {
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

        underTest.save(organizationUser);

        // when
        String emailToCheckFor = organizationUser.getUser()
            .getEmail();
        Long organizationId = organization.getId();
        Optional<OrganizationUser> optionalExpected = underTest.findOrganizationUserByEmailAndOrganization(
            emailToCheckFor, organizationId);

        // then
        assert optionalExpected.isPresent();

        OrganizationUser expected = optionalExpected.get();

        assertThat(expected.getId()).isNotNull();
        assertThat(expected.getOrganization()
            .getId()).isEqualTo(organization.getId());
        assertThat(expected.getOrganization()
            .getName()).isEqualTo(organization.getName());
        assertThat(expected.getOrganization()
            .getCreatedAt()).isEqualTo(organization.getCreatedAt());
    }

    @Test
    void itShouldNotFindOrganizationUserByEmailAndOrganization() {
        // given
        String emailToCheckFor = faker.internet()
            .emailAddress();
        Long organizationId = faker.random()
            .nextLong(50L);

        // when
        Optional<OrganizationUser> optionalExpected = underTest.findOrganizationUserByEmailAndOrganization(
            emailToCheckFor, organizationId);

        // then
        assertThat(optionalExpected).isNotPresent();
    }

}
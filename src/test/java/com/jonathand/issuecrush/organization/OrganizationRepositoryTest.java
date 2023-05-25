package com.jonathand.issuecrush.organization;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.organization.user.OrganizationUserRepository;
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
class OrganizationRepositoryTest {

    private final Faker faker = new Faker();

    @Autowired
    private OrganizationRepository underTest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationUserRepository organizationUserRepository;

    @AfterEach
    void tearDown() {
        organizationUserRepository.deleteAll();
        userRepository.deleteAll();
        underTest.deleteAll();
    }

    @Test
    void itShouldFindOrganizationsForEmail() {
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

        int organizationsToCreate = faker.random()
            .nextInt(1, 5);

        for (int i = 0; i < organizationsToCreate; i++) {
            String name = faker.company()
                .name();

            Organization organization = Organization.builder()
                .name(name)
                .creator(user)
                .createdAt(null)
                .updatedAt(null)
                .build();

            organization = underTest.save(organization);

            OrganizationUser organizationUser = OrganizationUser.builder()
                .user(user)
                .role(OrganizationRole.OWNER)
                .organization(organization)
                .build();

            organizationUserRepository.save(organizationUser);
        }

        Pageable pageable = PageRequest.of(0, organizationsToCreate);

        // when
        Page<Organization> expected = underTest.findOrganizationsForEmail(email, pageable);

        // then
        assertThat(expected).isNotNull();
        assertThat(expected.getTotalElements()).isEqualTo(organizationsToCreate);
        assertThat(expected.getTotalPages()).isEqualTo(1);
    }

    @Test
    void itShouldNotFindOrganizationsForEmail() {
        // given
        String email = faker.internet()
            .emailAddress();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Organization> expected = underTest.findOrganizationsForEmail(email, pageable);

        // then
        assertThat(expected).isNotNull();
        assertThat(expected.getTotalElements()).isEqualTo(0);
        assertThat(expected.getTotalPages()).isEqualTo(0);
    }

}
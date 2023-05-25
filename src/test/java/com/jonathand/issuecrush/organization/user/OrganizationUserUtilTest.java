package com.jonathand.issuecrush.organization.user;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.ticket.TicketRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrganizationUserUtilTest {

    private final Faker faker = new Faker();

    @Mock
    private OrganizationUserRepository organizationUserRepository;

    @InjectMocks
    private OrganizationUserUtil underTest;

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void canGetOrganizationUserByEmailAndOrganization() {
        // given
        String email = faker.internet()
                            .emailAddress();
        Long organizationId = faker.random()
                                   .nextLong(100L);

        Optional<OrganizationUser> organizationUserOptional = Optional.of(mock(OrganizationUser.class));

        when(organizationUserRepository.findOrganizationUserByEmailAndOrganization(email, organizationId)).thenReturn(
            organizationUserOptional);

        // when
        // then
        assertDoesNotThrow(() -> underTest.getOrganizationUserByEmailAndOrganization(email, organizationId));
    }

    @Test
    void canGetOrganizationUserByEmailAndOrganizationWillThrowWhenNotFound() {
        // given
        String email = faker.internet()
                            .emailAddress();
        Long organizationId = faker.random()
                                   .nextLong(100L);

        // when
        // then
        assertThatThrownBy(
            () -> underTest.getOrganizationUserByEmailAndOrganization(email, organizationId)).isInstanceOf(
                                                                                                 OrganizationUserNotFoundException.class)
                                                                                             .hasMessageContaining(
                                                                                                 "OrganizationUser " +
                                                                                                 "was not found");
    }

    @Test
    void canGetOrganizationUserById() {
        // given
        Long organizationUserId = faker.random()
                                       .nextLong(100L);

        Optional<OrganizationUser> optionalOrganizationUser = Optional.of(mock(OrganizationUser.class));

        when(organizationUserRepository.findById(organizationUserId)).thenReturn(optionalOrganizationUser);

        // when
        // then
        assertDoesNotThrow(() -> underTest.getOrganizationUserById(organizationUserId));
    }

    @Test
    void canGetOrganizationUserByIdWillThrowWhenNotFound() {
        // given
        Long organizationUserId = faker.random()
                                       .nextLong(100L);

        // when
        // then
        assertThatThrownBy(() -> underTest.getOrganizationUserById(organizationUserId)).isInstanceOf(
                                                                                           OrganizationUserNotFoundException.class)
                                                                                       .hasMessageContaining(
                                                                                           "OrganizationUser was not " +
                                                                                           "found");
    }

    @Test
    void validateOrganizationUserDoesExists() {
        // given
        Long organizationUserId = faker.random()
                                       .nextLong(100L);

        when(organizationUserRepository.existsById(organizationUserId)).thenReturn(true);

        // when
        // then
        assertDoesNotThrow(() -> underTest.validateOrganizationUserDoesExists(organizationUserId));
    }

    @Test
    void validateOrganizationUserDoesExistsWillThrowWhenNotFound() {
        // given
        Long organizationUserId = faker.random()
                                       .nextLong(100L);

        // when
        // then
        assertThatThrownBy(() -> underTest.validateOrganizationUserDoesExists(organizationUserId)).isInstanceOf(
                                                                                                      OrganizationUserNotFoundException.class)
                                                                                                  .hasMessageContaining(
                                                                                                      "OrganizationUser was not found");
    }

    @Test
    void validateOrganizationUserDoesNotExists() {
        // given
        Long organizationUserId = faker.random()
                                       .nextLong(100L);

        // when
        // then
        assertDoesNotThrow(() -> underTest.validateOrganizationUserDoesNotExists(organizationUserId));
    }

    @Test
    void validateOrganizationUserDoesNotExistsWillThrowWhenFound() {
        // given
        Long organizationUserId = faker.random()
                                       .nextLong(100L);

        when(organizationUserRepository.existsById(organizationUserId)).thenReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.validateOrganizationUserDoesNotExists(organizationUserId)).isInstanceOf(
                                                                                                         OrganizationUserAlreadyExistsException.class)
                                                                                                     .hasMessageContaining(
                                                                                                         "OrganizationUser already exists");
    }

}
package com.jonathand.issuecrush.organization;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrganizationUtilTest {

    private final Faker faker = new Faker();

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private OrganizationUtil underTest;

    @Test
    void canGetOrganizationById() {
        // given
        Long organizationId = faker.random()
            .nextLong(100L);

        Optional<Organization> optionalOrganization = Optional.of(mock(Organization.class));

        when(organizationRepository.findById(organizationId)).thenReturn(optionalOrganization);

        // when
        // then
        assertDoesNotThrow(() -> underTest.getOrganizationById(organizationId));
    }

    @Test
    void canGetOrganizationByIdWillThrowWhenNotFound() {
        // given
        Long organizationId = faker.random()
            .nextLong(100L);

        // when
        // then
        assertThatThrownBy(() -> underTest.getOrganizationById(organizationId)).isInstanceOf(
                OrganizationNotFoundException.class)
            .hasMessageContaining("Organization with id " + organizationId + " does not exist");
    }

    @Test
    void validateOrganizationExistsById() {
        // given
        Long organizationId = faker.random()
            .nextLong(100L);

        when(organizationRepository.existsById(organizationId)).thenReturn(true);

        // when
        // then
        assertDoesNotThrow(() -> underTest.validateOrganizationExistsById(organizationId));
    }

    @Test
    void validateOrganizationExistsByIdWillThrowWhenNotFound() {
        // given
        Long organizationId = faker.random()
            .nextLong(100L);

        // when
        // then
        assertThatThrownBy(() -> underTest.validateOrganizationExistsById(organizationId)).isInstanceOf(
                OrganizationNotFoundException.class)
            .hasMessageContaining("Organization with id " + organizationId + " does not exist");
    }

}
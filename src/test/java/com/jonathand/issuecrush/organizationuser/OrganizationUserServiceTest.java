package com.jonathand.issuecrush.organizationuser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.organization.OrganizationRepository;
import com.jonathand.issuecrush.organization.OrganizationRole;
import com.jonathand.issuecrush.organization.OrganizationUtil;
import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.organization.user.OrganizationUserDTOMapper;
import com.jonathand.issuecrush.organization.user.OrganizationUserRepository;
import com.jonathand.issuecrush.organization.user.OrganizationUserService;
import com.jonathand.issuecrush.organization.user.OrganizationUserUpdateRequest;
import com.jonathand.issuecrush.organization.user.OrganizationUserUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationUserServiceTest {

    private final Faker faker = new Faker();

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private OrganizationUserRepository organizationUserRepository;

    @Mock
    private OrganizationUserDTOMapper organizationUserDTOMapper;

    @Mock
    private OrganizationUtil organizationUtil;

    @Mock
    private OrganizationUserUtil organizationUserUtil;

    @InjectMocks
    private OrganizationUserService underTest;

    @Test
    void canGetOrganizationUser() {
        // given
        Long organizationId = faker.random()
            .nextLong(50L);
        String email = faker.internet()
            .emailAddress();

        OrganizationUser organizationUser = mock(OrganizationUser.class);

        when(organizationUserUtil.getOrganizationUserByEmailAndOrganization(email, organizationId)).thenReturn(
            organizationUser);

        // when
        underTest.getOrganizationUser(organizationId, email);

        // then
        verify(organizationUserDTOMapper).apply(organizationUser);
    }

    @Test
    void canUpdateOrganizationUser() {
        // given
        Long organizationId = faker.random()
            .nextLong(50L);
        String email = faker.internet()
            .emailAddress();
        String role = faker.options()
            .option(OrganizationRole.values())
            .name();

        OrganizationUserUpdateRequest updateRequest = new OrganizationUserUpdateRequest(organizationId, email, role);

        OrganizationUser organizationUser = mock(OrganizationUser.class);

        when(organizationUserUtil.getOrganizationUserByEmailAndOrganization(email, organizationId)).thenReturn(
            organizationUser);

        // when
        underTest.updateOrganizationUser(updateRequest);

        // then
        verify(organizationUser).setRole(OrganizationRole.valueOf(updateRequest.role()
            .toUpperCase()));
        verify(organizationUserRepository).save(organizationUser);
        verify(organizationUserDTOMapper).apply(organizationUser);
    }

}
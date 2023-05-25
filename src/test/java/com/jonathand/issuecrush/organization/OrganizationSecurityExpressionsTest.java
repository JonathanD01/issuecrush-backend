package com.jonathand.issuecrush.organization;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.organization.user.OrganizationUser;
import com.jonathand.issuecrush.organization.user.OrganizationUserRepository;
import com.jonathand.issuecrush.organization.user.OrganizationUserUtil;
import com.jonathand.issuecrush.user.User;
import com.jonathand.issuecrush.user.UserUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class OrganizationSecurityExpressionsTest {

    private final Faker faker = new Faker();

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private OrganizationUserRepository organizationUserRepository;

    @Mock
    private UserUtil userUtil;

    @Mock
    private OrganizationUtil organizationUtil;

    @Mock
    private OrganizationUserUtil organizationUserUtil;

    @InjectMocks
    private OrganizationSecurityExpressions underTest;

    @AfterEach
    void tearDown() {
        organizationRepository.deleteAll();
        organizationUserRepository.deleteAll();
    }

    @Test
    void itWillReturnTrueWhenUserIsOrganizationOwner() {
        // given
        String email = faker.internet()
            .emailAddress();
        Long organizationId = faker.random()
            .nextLong(50L);

        User user = mock(User.class);

        UserDetails userDetails = mock(UserDetails.class);
        Organization organization = mock(Organization.class);

        when(userUtil.getCurrentUserDetails()).thenReturn(userDetails);

        when(organizationUtil.getOrganizationById(organizationId)).thenReturn(organization);

        when(organization.getCreator()).thenReturn(user);

        when(organization.getCreator()
            .getUsername()).thenReturn(email);

        when(userDetails.getUsername()).thenReturn(email);

        // when
        boolean expected = underTest.isUserOrganizationOwner(organizationId);

        // then
        assertTrue(expected);
    }

    @Test
    void itWillReturnFalseWhenUserIsNotOrganizationOwner() {
        // given
        String email = faker.internet()
            .emailAddress();
        String email2 = faker.internet()
            .emailAddress();
        Long organizationId = faker.random()
            .nextLong(50L);

        User user = mock(User.class);
        Organization organization = mock(Organization.class);

        UserDetails userDetails = mock(UserDetails.class);

        when(userUtil.getCurrentUserDetails()).thenReturn(userDetails);

        when(organizationUtil.getOrganizationById(organizationId)).thenReturn(organization);

        when(organization.getCreator()).thenReturn(user);

        when(organization.getCreator()
            .getUsername()).thenReturn(email);

        when(userDetails.getUsername()).thenReturn(email2);

        // when
        boolean expected = underTest.isUserOrganizationOwner(organizationId);

        // then
        assertFalse(expected);
    }

    @Test
    void itWillReturnTrueWhenUserIsMemberOfOrganization() {
        // given
        Long organizationId = faker.random()
            .nextLong(50L);
        String email = faker.internet()
            .emailAddress();

        Organization organization = mock(Organization.class);
        OrganizationUser organizationUser = mock(OrganizationUser.class);

        UserDetails userDetails = mock(UserDetails.class);

        when(userUtil.getCurrentUserDetails()).thenReturn(userDetails);

        when(organizationUtil.getOrganizationById(organizationId)).thenReturn(organization);

        when(userDetails.getUsername()).thenReturn(email);

        when(organization.getId()).thenReturn(organizationId);

        when(organizationUserUtil.getOrganizationUserByEmailAndOrganization(email, organizationId)).thenReturn(
            organizationUser);

        when(organizationUser.getOrganization()).thenReturn(organization);

        when(organizationUser.getOrganization()
            .getId()).thenReturn(organizationId);

        // when
        boolean expected = underTest.isUserMemberOfOrganization(organizationId);

        // then
        assertTrue(expected);
    }

    @Test
    void itWillReturnFalseWhenUserIsNotMemberOfOrganization() {
        // given
        Long organizationId = faker.random()
            .nextLong(50L);
        Long otherOrganizationId = organizationId + 1L;
        String email = faker.internet()
            .emailAddress();

        Organization organization = mock(Organization.class);
        OrganizationUser organizationUser = mock(OrganizationUser.class);

        UserDetails userDetails = mock(UserDetails.class);

        when(userUtil.getCurrentUserDetails()).thenReturn(userDetails);

        when(organizationUtil.getOrganizationById(organizationId)).thenReturn(organization);

        when(userDetails.getUsername()).thenReturn(email);

        when(organization.getId()).thenReturn(organizationId);

        when(organizationUserUtil.getOrganizationUserByEmailAndOrganization(anyString(), anyLong())).thenReturn(
            organizationUser);

        when(organizationUser.getOrganization()).thenReturn(organization);

        when(organizationUser.getOrganization()
            .getId()).thenReturn(otherOrganizationId);

        // when
        boolean expected = underTest.isUserMemberOfOrganization(organizationId);

        // then
        assertFalse(expected);
    }

    @Test
    void canAllowUserToAddOrganizationUser() {
        // given
        String email = faker.internet()
            .emailAddress();
        Long organizationId = faker.random()
            .nextLong(50L);

        Organization organization = mock(Organization.class);
        OrganizationUser organizationUser = mock(OrganizationUser.class);

        UserDetails userDetails = mock(UserDetails.class);

        when(userUtil.getCurrentUserDetails()).thenReturn(userDetails);

        when(organizationUtil.getOrganizationById(organizationId)).thenReturn(organization);

        when(userDetails.getUsername()).thenReturn(email);

        when(organization.getId()).thenReturn(organizationId);

        when(organizationUserUtil.getOrganizationUserByEmailAndOrganization(email, organizationId)).thenReturn(
            organizationUser);

        when(organizationUser.getRole()).thenReturn(OrganizationRole.ADMIN);

        // when
        boolean expected = underTest.canUserAddOrganizationUser(organizationId);

        // then
        assertTrue(expected);

        verify(organizationUser).getRole();
    }

    @Test
    void canNotAllowMemberUserToAddOrganizationUser() {
        // given
        String email = faker.internet()
            .emailAddress();
        Long organizationId = faker.random()
            .nextLong(50L);

        Organization organization = mock(Organization.class);
        OrganizationUser organizationUser = mock(OrganizationUser.class);

        UserDetails userDetails = mock(UserDetails.class);

        when(userUtil.getCurrentUserDetails()).thenReturn(userDetails);

        when(organizationUtil.getOrganizationById(organizationId)).thenReturn(organization);

        when(userDetails.getUsername()).thenReturn(email);

        when(organization.getId()).thenReturn(organizationId);

        when(organizationUserUtil.getOrganizationUserByEmailAndOrganization(email, organizationId)).thenReturn(
            organizationUser);

        when(organizationUser.getRole()).thenReturn(OrganizationRole.MEMBER);

        // when
        boolean expected = underTest.canUserAddOrganizationUser(organizationId);

        // then
        assertFalse(expected);

        verify(organizationUser).getRole();
    }

    @Test
    void canNotAllowModeratorUserToAddOrganizationUser() {
        // given
        String email = faker.internet()
            .emailAddress();
        Long organizationId = faker.random()
            .nextLong(50L);

        Organization organization = mock(Organization.class);
        OrganizationUser organizationUser = mock(OrganizationUser.class);

        UserDetails userDetails = mock(UserDetails.class);

        when(userUtil.getCurrentUserDetails()).thenReturn(userDetails);

        when(organizationUtil.getOrganizationById(organizationId)).thenReturn(organization);

        when(userDetails.getUsername()).thenReturn(email);

        when(organization.getId()).thenReturn(organizationId);

        when(organizationUserUtil.getOrganizationUserByEmailAndOrganization(email, organizationId)).thenReturn(
            organizationUser);

        when(organizationUser.getRole()).thenReturn(OrganizationRole.MODERATOR);

        // when
        boolean expected = underTest.canUserAddOrganizationUser(organizationId);

        // then
        assertFalse(expected);

        verify(organizationUser).getRole();
    }

}
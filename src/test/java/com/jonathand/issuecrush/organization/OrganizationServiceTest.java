package com.jonathand.issuecrush.organization;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.organization.user.OrganizationUserDTOMapper;
import com.jonathand.issuecrush.organization.user.OrganizationUserRepository;
import com.jonathand.issuecrush.organization.user.OrganizationUserUtil;
import com.jonathand.issuecrush.ticket.TicketRepository;
import com.jonathand.issuecrush.ticket.comment.TicketCommentRepository;
import com.jonathand.issuecrush.user.User;
import com.jonathand.issuecrush.user.UserRepository;
import com.jonathand.issuecrush.user.UserUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    private final Faker faker = new Faker();

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketCommentRepository ticketCommentRepository;

    @Mock
    private OrganizationUserRepository organizationUserRepository;

    @Mock
    private OrganizationDTOMapper organizationDTOMapper;

    @Mock
    private OrganizationUserDTOMapper organizationUserDTOMapper;

    @Mock
    private UserUtil userUtil;

    @Mock
    private OrganizationUtil organizationUtil;

    @Mock
    private OrganizationUserUtil organizationUserUtil;

    @InjectMocks
    private OrganizationService underTest;

    @Test
    void canGetAllOrganizationsForUser() {
        // given
        String email = faker.internet()
            .emailAddress();
        Pageable pageable = PageRequest.of(0, 10);

        Page<Organization> expectedPage = new PageImpl<>(Collections.emptyList());

        when(organizationRepository.findOrganizationsForEmail(email, pageable)).thenReturn(expectedPage);

        // when
        underTest.getOrganizationsForUser(email, pageable);

        // then
        verify(organizationRepository).findOrganizationsForEmail(email, pageable);
    }

    @Test
    void canAddNewOrganization() {
        // given
        String email = faker.internet()
            .emailAddress();
        String organizationName = faker.company()
            .name();

        OrganizationNewRequest organizationNewRequest = new OrganizationNewRequest(organizationName);

        User user = mock(User.class);

        when(userUtil.getUserByEmail(email)).thenReturn(user);

        // when
        underTest.addNewOrganization(email, organizationNewRequest);

        // then
        ArgumentCaptor<Organization> organizationArgumentCaptor = ArgumentCaptor.forClass(Organization.class);

        verify(organizationRepository).save(organizationArgumentCaptor.capture());

        Organization capturedOrganization = organizationArgumentCaptor.getValue();

        assertThat(capturedOrganization).isNotNull();
        assertThat(capturedOrganization.getId()).isNull();
        assertThat(capturedOrganization.getCreator()).isEqualTo(user);
        assertThat(capturedOrganization.getName()).isEqualTo(organizationName);
        assertThat(capturedOrganization.getCreatedAt()).isNull();
        assertThat(capturedOrganization.getUpdatedAt()).isNull();
        assertThat(capturedOrganization.getUsers()).isNotNull();
        assertThat(capturedOrganization.getTickets()).isNotNull();
    }

    @Test
    void canUpdateOrganization() {
        // given
        Long organizationId = faker.random()
            .nextLong(100L);
        String newOrganizationName = faker.company()
            .name();

        Organization organization = mock(Organization.class);

        OrganizationUpdateRequest organizationNewRequest = new OrganizationUpdateRequest(newOrganizationName);

        when(organizationUtil.getOrganizationById(organizationId)).thenReturn(organization);

        // when
        underTest.updateOrganization(organizationId, organizationNewRequest);

        // then
        verify(organizationRepository).save(any());
        verify(organizationDTOMapper).apply(any());
    }

    @Test
    void canDeleteOrganization() {
        // given
        Long organizationId = faker.random()
            .nextLong(100L);

        Organization organization = mock(Organization.class);

        when(organizationUtil.getOrganizationById(organizationId)).thenReturn(organization);

        when(organization.getId()).thenReturn(organizationId);

        // when
        underTest.deleteOrganization(organizationId);

        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(organizationRepository).deleteById(longArgumentCaptor.capture());

        Long capturedLong = longArgumentCaptor.getValue();

        // then
        assertThat(organizationId).isEqualTo(capturedLong);
    }

    @Test
    void canGetOrganization() {
        // given
        Long organizationId = faker.random()
            .nextLong(100L);

        Organization organization = mock(Organization.class);

        when(organizationUtil.getOrganizationById(organizationId)).thenReturn(organization);

        // when
        underTest.getOrganization(organizationId);

        // then
        verify(organizationDTOMapper).apply(organization);
    }

    // TODO ADD WHEREVER APPLICABLE
    @Test
    @Disabled
    void canGetOrganizationStatistics() {

    }

    // TODO TEST isUserInTicketCommentOrganization

    @Test
    void canGetOrganizationUsersForOrganization() {
        // given
        Long organizationId = faker.random()
            .nextLong(50L);
        Pageable pageable = PageRequest.of(0, 10);

        when(organizationUserRepository.findByOrganization_Id(organizationId, pageable)).thenReturn(Page.empty());

        // when
        underTest.getOrganizationUsers(organizationId, pageable);

        // then
        verify(organizationUserRepository).findByOrganization_Id(organizationId, pageable);
    }

    @Test
    void canAddUserToOrganization() {
        // given
        Long organizationId = faker.random()
            .nextLong(100L);
        Long organizationUserId = faker.random()
            .nextLong(100L);
        String emailToAdd = faker.internet()
            .emailAddress();

        OrganizationAddUserRequest addUserRequest = new OrganizationAddUserRequest(organizationUserId, emailToAdd);

        Organization organization = mock(Organization.class);

        when(organizationUtil.getOrganizationById(organizationId)).thenReturn(organization);

        // when
        underTest.addUserToOrganization(organizationId, addUserRequest);

        // then
        verify(organizationUserRepository).save(any());
        verify(organizationUserDTOMapper).apply(any());
    }

}

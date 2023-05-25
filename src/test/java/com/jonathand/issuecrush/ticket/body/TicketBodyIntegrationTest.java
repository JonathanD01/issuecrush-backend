package com.jonathand.issuecrush.ticket.body;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.UserRegisterExtractor;
import com.jonathand.issuecrush.auth.AuthenticationResponse;
import com.jonathand.issuecrush.organization.OrganizationDTO;
import com.jonathand.issuecrush.organization.OrganizationNewRequest;
import com.jonathand.issuecrush.response.APIResponse;
import com.jonathand.issuecrush.response.APIResponseType;
import com.jonathand.issuecrush.ticket.TicketCreateRequest;
import com.jonathand.issuecrush.ticket.TicketDTO;
import com.jonathand.issuecrush.ticket.TicketDepartment;
import com.jonathand.issuecrush.ticket.property.TicketPriority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class TicketBodyIntegrationTest {

    private static final String TICKET_PATH = "/api/v1/tickets";

    private static final String TICKET_BODY_PATH = "/api/v1/ticket-body";

    private static final String ORGANIZATION_PATH = "/api/v1/organizations";

    private static final String AUTHENTICATION_PATH = "/api/v1/auth";

    private final Faker faker = new Faker();

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void canGetTicketBody() {
        // given
        String email = faker.internet()
            .emailAddress();
        String password = faker.internet()
            .password();

        // get jwt token
        EntityExchangeResult<AuthenticationResponse> authResult = UserRegisterExtractor.builder()
            .webTestClient(webTestClient)
            .authenticationPath(AUTHENTICATION_PATH)
            .firstName(faker.name()
                .firstName())
            .lastName(faker.name()
                .lastName())
            .email(email)
            .password(password)
            .build()
            .get();

        assert authResult.getResponseBody() != null;

        String jwtToken = authResult.getResponseBody()
            .getToken();

        // use jwt token to create an organization
        String organizationName = faker.company()
            .name();

        OrganizationNewRequest organizationNewRequest = new OrganizationNewRequest(organizationName);

        EntityExchangeResult<APIResponse<OrganizationDTO>> organizationResult = webTestClient.post()
            .uri(ORGANIZATION_PATH)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(organizationNewRequest), OrganizationNewRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<OrganizationDTO>>() {
            })
            .returnResult();

        APIResponse<OrganizationDTO> response = organizationResult.getResponseBody();

        assert response != null;

        OrganizationDTO organizationDTO = response.getResult();
        Long organizationId = organizationDTO.id();

        TicketCreateRequest createRequest = new TicketCreateRequest(faker.lorem()
            .sentence(10), faker.lorem()
            .sentence(30), faker.options()
            .option(TicketPriority.values())
            .name(), faker.options()
            .option(TicketDepartment.values())
            .name(), faker.bool()
            .bool());

        EntityExchangeResult<APIResponse<TicketDTO>> ticketResult = webTestClient.post()
            .uri(TICKET_PATH + "/organization/{organizationId}", organizationId)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just(createRequest), TicketCreateRequest.class)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<TicketDTO>>() {
            })
            .returnResult();

        assert ticketResult.getResponseBody() != null;

        Long ticketId = ticketResult.getResponseBody()
            .getResult()
            .id();

        // when
        EntityExchangeResult<APIResponse<TicketBodyDTO>> result = webTestClient.get()
            .uri(TICKET_BODY_PATH + "/{ticketId}", ticketId)
            .accept(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth(jwtToken))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(new ParameterizedTypeReference<APIResponse<TicketBodyDTO>>() {
            })
            .returnResult();

        // then
        assert result.getResponseBody() != null;

        APIResponse<TicketBodyDTO> apiResponse = result.getResponseBody();
        TicketBodyDTO ticketBodyDTO = apiResponse.getResult();

        assertThat(apiResponse.getResponseType()).isEqualTo(APIResponseType.SUCCESS);
        assertThat(ticketBodyDTO.title()).isEqualTo(createRequest.title());
        assertThat(ticketBodyDTO.content()).isEqualTo(createRequest.content());
    }

}
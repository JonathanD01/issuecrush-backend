package com.jonathand.issuecrush.ticket.body;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.javafaker.Faker;
import com.jonathand.issuecrush.ticket.Ticket;
import com.jonathand.issuecrush.ticket.TicketRepository;
import com.jonathand.issuecrush.ticket.TicketUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TicketBodyServiceTest {

    private final Faker faker = new Faker();

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketBodyDTOMapper ticketBodyDTOMapper;

    @Mock
    private TicketUtil ticketUtil;

    @InjectMocks
    private TicketBodyService underTest;

    @Test
    void canGetTicketBody() {
        // given
        Long ticketId = faker.random()
            .nextLong(50L);

        Ticket ticket = mock(Ticket.class);
        TicketBody ticketBody = mock(TicketBody.class);

        when(ticketUtil.getTicketById(ticketId)).thenReturn(ticket);

        when(ticket.getTicketBody()).thenReturn(ticketBody);

        // when
        underTest.getTicketBodyForTicket(ticketId);

        // then
        verify(ticketBodyDTOMapper).apply(any());
    }


}
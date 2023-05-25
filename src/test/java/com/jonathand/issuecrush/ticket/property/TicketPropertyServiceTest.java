package com.jonathand.issuecrush.ticket.property;

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
class TicketPropertyServiceTest {

    private final Faker faker = new Faker();

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketPropertyDTOMapper ticketPropertyDTOMapper;

    @Mock
    private TicketUtil ticketUtil;

    @InjectMocks
    private TicketPropertyService underTest;

    @Test
    void canGetTicketProperty() {
        // given
        Long ticketId = faker.random()
            .nextLong(50L);

        Ticket ticket = mock(Ticket.class);
        TicketProperty ticketProperty = mock(TicketProperty.class);

        when(ticketUtil.getTicketById(ticketId)).thenReturn(ticket);

        when(ticket.getTicketProperty()).thenReturn(ticketProperty);

        // when
        underTest.getTicketPropertyForTicket(ticketId);

        // then
        verify(ticketPropertyDTOMapper).apply(any());
    }

}
package com.jonathand.issuecrush.ticket.body;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketBodyRepository extends JpaRepository<TicketBody, Long> {

}

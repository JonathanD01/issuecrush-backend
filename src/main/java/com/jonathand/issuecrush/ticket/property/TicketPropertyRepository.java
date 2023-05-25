package com.jonathand.issuecrush.ticket.property;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketPropertyRepository extends JpaRepository<TicketProperty, Long> {

}
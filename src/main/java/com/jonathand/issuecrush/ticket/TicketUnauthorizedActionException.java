package com.jonathand.issuecrush.ticket;

import com.jonathand.issuecrush.organization.OrganizationRole;

public class TicketUnauthorizedActionException extends RuntimeException {

    public TicketUnauthorizedActionException(OrganizationRole organizationRole) {
        super("You need the '" + organizationRole.name()
            .toUpperCase() + "' role to do this");
    }

}

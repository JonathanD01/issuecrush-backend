package com.jonathand.issuecrush.organization;

public class OrganizationUnauthorizedActionException extends RuntimeException {

    public OrganizationUnauthorizedActionException(OrganizationRole organizationRole) {
        super("You need the '" + organizationRole.name()
                                                 .toUpperCase() + "' role to do this");
    }

    public OrganizationUnauthorizedActionException(String message) {
        super(message);
    }

}

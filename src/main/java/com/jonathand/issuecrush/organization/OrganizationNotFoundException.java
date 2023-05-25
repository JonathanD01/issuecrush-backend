package com.jonathand.issuecrush.organization;

public class OrganizationNotFoundException extends RuntimeException {

    public OrganizationNotFoundException(Long organizationId) {
        super("Organization with id " + organizationId + " does not exist");
    }

}

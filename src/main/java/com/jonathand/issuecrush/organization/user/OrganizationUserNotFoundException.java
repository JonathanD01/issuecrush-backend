package com.jonathand.issuecrush.organization.user;

public class OrganizationUserNotFoundException extends RuntimeException {

    public OrganizationUserNotFoundException() {
        super("OrganizationUser was not found");
    }

}

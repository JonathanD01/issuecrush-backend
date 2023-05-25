package com.jonathand.issuecrush.organization;

public class OrganizationRoleNotFoundException extends RuntimeException {

    public OrganizationRoleNotFoundException(String roleArgument) {
        super("The role " + roleArgument + " was not found...");
    }

}

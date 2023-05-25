package com.jonathand.issuecrush.ticket;

public class TicketDepartmentNotFoundException extends RuntimeException {

    public TicketDepartmentNotFoundException(String departmentArgument) {
        super("The department " + departmentArgument + " was not found...");
    }

}

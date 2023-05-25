package com.jonathand.issuecrush.ticket;

public enum TicketDepartment {
    IT,
    HUMAN_RESOURCES,
    MARKETING,
    SALES,
    CUSTOMER_SUPPORT,
    LEGAL,
    ENGINEERING,
    PROCUREMENT,
    FINANCE,
    OPERATIONS,
    ADMINISTRATION,
    RESEARCH_AND_DEVELOPMENT,
    PRODUCT_MANAGEMENT,
    DESIGN,
    QUALITY_ASSURANCE,
    PUBLIC_RELATIONS,
    EVENTS,
    CORPORATE_COMMUNICATIONS,
    SOCIAL_MEDIA,
    OTHER,
    ;

    public static TicketDepartment fromString(String departmentArgument) {
        try {
            return TicketDepartment.valueOf(departmentArgument.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new TicketDepartmentNotFoundException(departmentArgument.toUpperCase());
        }
    }
}

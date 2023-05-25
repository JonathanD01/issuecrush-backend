package com.jonathand.issuecrush.config;

import com.jonathand.issuecrush.organization.OrganizationDTOMapper;
import com.jonathand.issuecrush.organization.OrganizationSecurityExpressions;
import com.jonathand.issuecrush.organization.OrganizationUtil;
import com.jonathand.issuecrush.organization.user.OrganizationUserDTOMapper;
import com.jonathand.issuecrush.organization.user.OrganizationUserUtil;
import com.jonathand.issuecrush.response.APIResponseUtil;
import com.jonathand.issuecrush.ticket.TicketDTOMapper;
import com.jonathand.issuecrush.ticket.TicketUtil;
import com.jonathand.issuecrush.ticket.body.TicketBodyDTOMapper;
import com.jonathand.issuecrush.ticket.comment.TicketCommentDTOMapper;
import com.jonathand.issuecrush.ticket.comment.TicketCommentUtil;
import com.jonathand.issuecrush.ticket.property.TicketPropertyDTOMapper;
import com.jonathand.issuecrush.user.UserDTOMapper;
import com.jonathand.issuecrush.user.UserRepository;
import com.jonathand.issuecrush.user.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDTOMapper userDTOMapper() {
        return new UserDTOMapper();
    }

    @Bean
    public OrganizationDTOMapper organizationDTOMapper() {
        return new OrganizationDTOMapper(userDTOMapper());
    }

    @Bean
    public OrganizationUserDTOMapper organizationUserDTOMapper() {
        return new OrganizationUserDTOMapper();
    }

    @Bean
    public TicketDTOMapper ticketDTOMapper() {
        return new TicketDTOMapper(organizationUserDTOMapper());
    }

    @Bean
    public TicketBodyDTOMapper ticketBodyDTOMapper() {
        return new TicketBodyDTOMapper();
    }

    @Bean
    public TicketPropertyDTOMapper ticketPropertyDTOMapper() {
        return new TicketPropertyDTOMapper(organizationUserDTOMapper());
    }

    @Bean
    public TicketCommentDTOMapper ticketCommentDTOMapper() {
        return new TicketCommentDTOMapper(organizationUserDTOMapper());
    }

    @Bean
    public OrganizationSecurityExpressions organizationSecurityExpressions() {
        return new OrganizationSecurityExpressions();
    }

    @Bean
    public APIResponseUtil apiErrorUtil() {
        return new APIResponseUtil();
    }

    @Bean
    public UserUtil userUtil() {
        return new UserUtil();
    }

    @Bean
    public OrganizationUtil organizationUtil() {
        return new OrganizationUtil();
    }

    @Bean
    public OrganizationUserUtil organizationUserUtil() {
        return new OrganizationUserUtil();
    }

    @Bean
    public TicketUtil ticketUtil() {
        return new TicketUtil();
    }

    @Bean
    public TicketCommentUtil ticketCommentUtil() {
        return new TicketCommentUtil();
    }

}

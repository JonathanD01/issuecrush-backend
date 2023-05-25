package com.jonathand.issuecrush.monitoring;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@RestController
@RequestMapping("api/v1/monitoring")
@RequiredArgsConstructor
public class MonitoringController {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    /**
     * <a
     * href="https://betterprogramming.pub/building-an-api-to-list-all-endpoints-exposed-by-spring-boot-645f1f64ebf3
     * ">Source</a>
     */
    @GetMapping("endpoints")
    public ResponseEntity<List<String>> getEndpoints() {
        return new ResponseEntity<>(
            requestMappingHandlerMapping.getHandlerMethods()
                                        .keySet()
                                        .stream()
                                        .map(RequestMappingInfo::toString)
                                        .collect(Collectors.toList()),
            HttpStatus.OK);
    }

}

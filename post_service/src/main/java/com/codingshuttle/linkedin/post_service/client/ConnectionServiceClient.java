package com.codingshuttle.linkedin.post_service.client;


import com.codingshuttle.linkedin.post_service.dto.PersonDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "connection-service")
public interface ConnectionServiceClient {

    @GetMapping("/api/v1/connections/core/{userId}/first-degree")
    List<PersonDto> getFirstDegreeConnections(@PathVariable Long userId);
}

package com.codingshuttle.linkedin.connection_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionAcceptedEvent {
    private Long accepterId;
    private Long receiverId;
    private String accepterName;
}
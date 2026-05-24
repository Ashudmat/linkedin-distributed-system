package com.codingshuttle.linkedin.user_service.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCreatedEvent {
    private Long id;
    private String name;
    private String email;
}

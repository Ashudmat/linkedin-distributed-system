package com.codingshuttle.linkedin.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImageUpdatedEvent {
    private Long userId;
    private String profileImageUrl;
}
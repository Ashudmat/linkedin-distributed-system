package com.codingshuttle.linkedin.notification_service.entity;

import com.codingshuttle.linkedin.notification_service.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long receiverUserId;
    private Long actorUserId;
    private Long postId;
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private boolean seen;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
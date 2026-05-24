package com.codingshuttle.linkedin.notification_service.consumer;

import com.codingshuttle.linkedin.connection_service.event.ConnectionAcceptedEvent;
import com.codingshuttle.linkedin.connection_service.event.ConnectionRequestSentEvent;
import com.codingshuttle.linkedin.notification_service.entity.Notification;
import com.codingshuttle.linkedin.notification_service.enums.NotificationType;
import com.codingshuttle.linkedin.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectionConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "connection_request_sent_topic", groupId = "notification-service")
    public void handleConnectionRequestSent(ConnectionRequestSentEvent event) {
        log.info("Received connection request event");
        String notificationMessage = String.format("%s sent you a connection request", event.getSenderName());
        Notification notification = Notification.builder()
                .receiverUserId(event.getReceiverId())
                .actorUserId(event.getSenderId())
                .message(notificationMessage)
                .type(NotificationType.CONNECTION_REQUEST)
                .seen(false)
                .build();

        notificationService.addNotification(notification);
    }

    @KafkaListener(topics = "connection_accepted_topic", groupId = "notification-service")
    public void handleConnectionAccepted(ConnectionAcceptedEvent event) {
        log.info("Received connection accepted event");
        String notificationMessage = String.format("%s accepted your connection request", event.getAccepterName());
        Notification notification = Notification.builder()
                .receiverUserId(event.getReceiverId())
                .actorUserId(event.getAccepterId())
                .message(notificationMessage)
                .type(NotificationType.CONNECTION_ACCEPTED)
                .seen(false)
                .build();

        notificationService.addNotification(notification);
    }
}
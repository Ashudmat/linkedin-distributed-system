package com.codingshuttle.linkedin.notification_service.service;

import com.codingshuttle.linkedin.notification_service.entity.Notification;
import com.codingshuttle.linkedin.notification_service.exceptions.BadRequestException;
import com.codingshuttle.linkedin.notification_service.exceptions.ResourceNotFoundException;
import com.codingshuttle.linkedin.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    public void addNotification(Notification notification){
        Notification saved = notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/notifications/" + saved.getReceiverUserId(), saved);
    }

    public void markAsSeen(Long notificationId){
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() ->
                new ResourceNotFoundException("Notification not found with id: " + notificationId));
        notification.setSeen(true);
        notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(Long userId){
        return notificationRepository.findByReceiverUserIdOrderByCreatedAtDesc(userId);
    }
}

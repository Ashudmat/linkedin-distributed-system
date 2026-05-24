package com.codingshuttle.linkedin.notification_service.controller;

import com.codingshuttle.linkedin.notification_service.entity.Notification;
import com.codingshuttle.linkedin.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/{notificationId}/seen")
    public ResponseEntity<Void> markAsSeen(@PathVariable Long notificationId){
        notificationService.markAsSeen(notificationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable Long userId){
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }
}

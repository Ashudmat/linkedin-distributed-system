package com.codingshuttle.linkedin.notification_service.consumer;

import com.codingshuttle.linkedin.notification_service.entity.Notification;
import com.codingshuttle.linkedin.notification_service.enums.NotificationType;
import com.codingshuttle.linkedin.notification_service.service.NotificationService;
import com.codingshuttle.linkedin.post_service.event.PostCommented;
import com.codingshuttle.linkedin.post_service.event.PostCreated;
import com.codingshuttle.linkedin.post_service.event.PostLiked;
import com.codingshuttle.linkedin.post_service.event.PostReposted;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "post_created_topic", groupId = "notification-service")
    public void handlePostCreated(PostCreated postCreated) {
        log.info("Received post created event");
        String message = String.format("Your connection with id %d created a post: %s", postCreated.getAuthorId(), postCreated.getContent());

        Notification notification = Notification.builder()
                .postId(postCreated.getPostId())
                .actorUserId(postCreated.getAuthorId())
                .receiverUserId(postCreated.getReceiverUserId())
                .type(NotificationType.POST_CREATED)
                .seen(false)
                .message(message)
                .build();

        notificationService.addNotification(notification);
    }

    @KafkaListener(topics = "post_liked_topic", groupId = "notification-service")
    public void handlePostLiked(PostLiked postLiked) {
        log.info("Received post liked event");
        String message = String.format("User with id %d liked your post", postLiked.getLikedByUserId());

        Notification notification = Notification.builder()
                .postId(postLiked.getPostId())
                .actorUserId(postLiked.getLikedByUserId())
                .receiverUserId(postLiked.getOwnerUserId())
                .type(NotificationType.POST_LIKED)
                .message(message)
                .seen(false)
                .build();

        notificationService.addNotification(notification);
    }

    @KafkaListener(topics = "post_commented_topic", groupId = "notification-service")
    public void handlePostCommented(PostCommented event) {

        String message = String.format("User with id %d commented your post", event.getPostId());
        Notification notification = Notification.builder()
                .postId(event.getPostId())
                .actorUserId(event.getCommenterUserId())
                .receiverUserId(event.getOwnerUserId())
                .type(NotificationType.POST_COMMENTED)
                .message(message)
                .seen(false)
                .build();

        notificationService.addNotification(notification);
    }

    @KafkaListener(
            topics = "post_reposted_topic",
            groupId = "notification-service"
    )
    public void handlePostReposted(PostReposted event) {

        Notification notification =
                Notification.builder()
                        .receiverUserId(event.getOwnerUserId())
                        .actorUserId(event.getRepostedByUserId())
                        .postId(event.getPostId())
                        .message(String.format("User with id %d shared your post with their network", event.getRepostedByUserId()))
                        .type(NotificationType.POST_REPOSTED)
                        .seen(false)
                        .build();


        notificationService.addNotification(notification);


    }
}
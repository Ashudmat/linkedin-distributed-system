package com.codingshuttle.linkedin.connection_service.consumer;

import com.codingshuttle.linkedin.connection_service.entity.Person;
import com.codingshuttle.linkedin.connection_service.repository.PersonRepository;
import com.codingshuttle.linkedin.user_service.event.ProfileImageUpdatedEvent;
import com.codingshuttle.linkedin.user_service.event.UserCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserConsumer {

    private final PersonRepository personRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user_created_topic" , groupId = "connection_service")
    public void handleUserCreated(String message) throws JsonProcessingException {
        UserCreatedEvent event = objectMapper.readValue(message, UserCreatedEvent.class);
        log.info("Received user created event for userId: {}", event.getId());
        Person person = Person.builder()
                .userId(event.getId())
                .name(event.getName())
                .email(event.getEmail())
                .build();
        personRepository.save(person);
        log.info("Person node created in Neo4j");
    }

    @KafkaListener(topics = "profile_image_updated_topic", groupId = "connection_service")
    public void handleProfileImageUpdated(String message) throws JsonProcessingException {
        ProfileImageUpdatedEvent event = objectMapper.readValue(message, ProfileImageUpdatedEvent.class);
        Person person = personRepository.findByUserId(event.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        person.setProfileImageUrl(event.getProfileImageUrl());
        personRepository.save(person);
        log.info("Profile image updated for userId: {}", event.getUserId());
    }
}

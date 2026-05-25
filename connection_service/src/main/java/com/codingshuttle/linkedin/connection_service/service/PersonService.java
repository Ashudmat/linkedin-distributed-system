package com.codingshuttle.linkedin.connection_service.service;

import com.codingshuttle.linkedin.connection_service.auth.AuthContextHolder;
import com.codingshuttle.linkedin.connection_service.dto.PersonDto;
import com.codingshuttle.linkedin.connection_service.entity.Person;
import com.codingshuttle.linkedin.connection_service.event.ConnectionAcceptedEvent;
import com.codingshuttle.linkedin.connection_service.event.ConnectionRequestSentEvent;
import com.codingshuttle.linkedin.connection_service.exceptions.BadRequestException;
import com.codingshuttle.linkedin.connection_service.exceptions.ResourceNotFoundException;
import com.codingshuttle.linkedin.connection_service.repository.PersonRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonService {

    private final PersonRepository personRepository;
    private final KafkaTemplate<String, ConnectionRequestSentEvent> connectionRequestKafkaTemplate;
    private final KafkaTemplate<String, ConnectionAcceptedEvent> connectionAcceptedKafkaTemplate;

    private PersonDto convertToDto(Person person){
        PersonDto dto = new PersonDto();
        dto.setId(person.getUserId());
        dto.setName(person.getName());
        dto.setEmail(person.getEmail());
        return dto;
    }

    public List<PersonDto> getFirstDegreeConnection(Long userId){
        personRepository.findByUserId(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
        return personRepository
                .findFirstDegreeConnections(userId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public void removeConnection(Long otherUserId){
        Long currentUserId = AuthContextHolder.getCurrrentUserId();
        personRepository.findByUserId(otherUserId).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
        if(currentUserId.equals(otherUserId)){
            throw new BadRequestException("You cannot remove yourself from your connections");
        }
        boolean connected = personRepository.connectionExists(currentUserId, otherUserId);
        if(!connected){
            throw new BadRequestException("No active connection found with this user");
        }
        personRepository.removeConnection(currentUserId, otherUserId);
    }

    public void rejectRequest(Long senderId){
        Long receiverId = AuthContextHolder.getCurrrentUserId();
        personRepository.findByUserId(senderId).orElseThrow(() ->
                new ResourceNotFoundException("Sender not found"));
        if(senderId.equals(receiverId)){
            throw new BadRequestException("You cannot reject your own connection request");
        }
        boolean requestExists = personRepository.connectionRequestExists(senderId, receiverId);
        if(!requestExists){
            throw new BadRequestException("No pending connection request found from this user");
        }
        personRepository.rejectConnectionRequest(senderId, receiverId);
    }

    public List<PersonDto> getPendingRequests(){
        Long currentUserId = AuthContextHolder.getCurrrentUserId();
        return personRepository
                .getPendingRequests(currentUserId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<PersonDto> getSecondDegreeConnections(Long userId){
        personRepository.findByUserId(userId).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
        return personRepository
                .getSecondDegreeConnections(userId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public void cancelSentRequest(Long receiverId){
        Long senderId = AuthContextHolder.getCurrrentUserId();
        personRepository.findByUserId(receiverId).orElseThrow(() ->
                new ResourceNotFoundException("Receiver not found"));
        if(senderId.equals(receiverId)){
            throw new BadRequestException("You cannot cancel a request sent to yourself");
        }
        boolean requestExists = personRepository.connectionRequestExists(senderId, receiverId);
        if(!requestExists){
            throw new ResourceNotFoundException("No pending connection request exists for this user");
        }
        personRepository.cancelSentRequest(senderId, receiverId);
    }

    public List<PersonDto> getMutualConnections(Long otherUserId){
        Long currentUserId = AuthContextHolder.getCurrrentUserId();
        if(currentUserId.equals(otherUserId)){
            throw new BadRequestException("You cannot view mutual connections with yourself");
        }
        personRepository.findByUserId(otherUserId).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
        return personRepository.getMutualConnections(currentUserId, otherUserId)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public Integer shortestPath(Long otherUserId){
        Long currentUserId = AuthContextHolder.getCurrrentUserId();
        if(currentUserId.equals(otherUserId)){
            throw new BadRequestException("Cannot calculate connection path to yourself");
        }
        personRepository.findByUserId(otherUserId).orElseThrow(() ->
                new ResourceNotFoundException("User not found"));
        return personRepository.shortestPathLength(currentUserId, otherUserId);
    }

    public void sendConnectionRequest(Long receiverId) {
        Long senderId = AuthContextHolder.getCurrrentUserId();
        Person sender = personRepository.findByUserId(senderId).orElseThrow(() ->
                new ResourceNotFoundException("Sender profile not found"));
        personRepository.findByUserId(receiverId).orElseThrow(() ->
                new ResourceNotFoundException("Receiver profile not found"));
        if(senderId.equals(receiverId)){
            throw new BadRequestException("You cannot send a connection request to yourself");
        }
        boolean alreadyConnected = personRepository.connectionExists(senderId, receiverId);
        if(alreadyConnected){
            throw new BadRequestException("You are already connected with this user");
        }
        boolean requestAlreadySent = personRepository.connectionRequestExists(senderId, receiverId);
        if(requestAlreadySent){
            throw new BadRequestException("A connection request has already been sent to this user");
        }
        boolean reverseRequestExists = personRepository.connectionRequestExists(receiverId, senderId);
        if(reverseRequestExists){
            log.info("Reverse connection request found. Auto accepting connection between {} and {}", senderId, receiverId);
            acceptRequest(receiverId);
            return;
        }
        personRepository.sendConnectionRequest(senderId, receiverId);
        ConnectionRequestSentEvent event = ConnectionRequestSentEvent
                .builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .senderName(sender.getName())
                .build();
        connectionRequestKafkaTemplate.send("connection_request_sent_topic", event);
    }

    public void acceptRequest(Long senderId){
        Long receiverId = AuthContextHolder.getCurrrentUserId();
        personRepository.findByUserId(senderId).orElseThrow(() ->
                new ResourceNotFoundException("Sender profile not found"));
        Person accepter = personRepository.findByUserId(receiverId).orElseThrow(() ->
                new ResourceNotFoundException("Receiver profile not found"));
        if(senderId.equals(receiverId)){
            throw new BadRequestException("You cannot accept your own connection request");
        }
        boolean requestExists = personRepository.connectionRequestExists(senderId, receiverId);
        if(!requestExists){
            throw new ResourceNotFoundException("No pending connection request found from this user");
        }
        boolean alreadyConnected = personRepository.connectionExists(senderId, receiverId);
        if(alreadyConnected){
            throw new BadRequestException("You are already connected with this user");
        }
        personRepository.acceptConnectionRequest(senderId, receiverId);
        ConnectionAcceptedEvent event = ConnectionAcceptedEvent
                .builder()
                .accepterId(receiverId)
                .receiverId(senderId)
                .accepterName(accepter.getName())
                .build();
        connectionAcceptedKafkaTemplate.send("connection_accepted_topic", event);
    }
}
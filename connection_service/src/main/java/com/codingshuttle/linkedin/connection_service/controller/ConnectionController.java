package com.codingshuttle.linkedin.connection_service.controller;

import com.codingshuttle.linkedin.connection_service.dto.PersonDto;
import com.codingshuttle.linkedin.connection_service.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/core")
@RequiredArgsConstructor
public class ConnectionController {

    private final PersonService personService;

    @GetMapping("/{userId}/first-degree")
    public ResponseEntity<List<PersonDto>> getFirstDegreeConnection(@PathVariable Long userId){
        return ResponseEntity.ok(personService.getFirstDegreeConnection(userId));
    }

    @PostMapping("/request/{receiverId}")
    public ResponseEntity<String> sendRequest(@PathVariable Long receiverId){
        personService.sendConnectionRequest(receiverId);
        return ResponseEntity.ok("Request Sent");
    }

    @PostMapping("/accept/{senderId}")
    public ResponseEntity<String> acceptRequest(@PathVariable Long senderId){
        personService.acceptRequest(senderId);
        return ResponseEntity.ok("Connection Accepted");
    }

    @DeleteMapping("/remove/{userId}")
    public ResponseEntity<String> removeConnection(@PathVariable Long userId){
        personService.removeConnection(userId);
        return ResponseEntity.ok("Connection Removed");
    }

    @PostMapping("/reject/{senderId}")
    public ResponseEntity<String> rejectRequest(@PathVariable Long senderId){
        personService.rejectRequest(senderId);
        return ResponseEntity.ok("Request Rejected");
    }

    @GetMapping("/pending-requests")
    public ResponseEntity<List<PersonDto>> getPendingRequests(){
        return ResponseEntity.ok(personService.getPendingRequests());
    }

    @GetMapping("/{userId}/second-degree")
    public ResponseEntity<List<PersonDto>> getSecondDegreeConnections(@PathVariable Long userId){
        return ResponseEntity.ok(personService.getSecondDegreeConnections(userId));
    }

    @DeleteMapping("/cancel-request/{receiverId}")
    public ResponseEntity<String> cancelRequest(@PathVariable Long receiverId){
        personService.cancelSentRequest(receiverId);
        return ResponseEntity.ok("Request Cancelled");
    }

    @GetMapping("/mutual/{userId}")
    public ResponseEntity<List<PersonDto>> getMutualConnections(@PathVariable Long userId){
        return ResponseEntity.ok(personService.getMutualConnections(userId));
    }

    @GetMapping("/shortest-path/{userId}")
    public ResponseEntity<Integer> shortestPath(@PathVariable Long userId){
        return ResponseEntity.ok(personService.shortestPath(userId));
    }
}
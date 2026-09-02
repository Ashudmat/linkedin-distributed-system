package com.codingshuttle.linkedin.chat_service.controller;

import com.codingshuttle.linkedin.chat_service.dto.ChatMessageDto;
import com.codingshuttle.linkedin.chat_service.entity.ChatMessage;
import com.codingshuttle.linkedin.chat_service.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessageDto dto) {
        return chatService.save(dto);
    }

    @GetMapping("/history/{senderId}/{receiverId}")
    public List<ChatMessage> getHistory(@PathVariable Long senderId, @PathVariable Long receiverId) {
        return chatService.getConversation(senderId, receiverId);
    }
}
package com.codingshuttle.linkedin.chat_service.service;

import com.codingshuttle.linkedin.chat_service.dto.ChatMessageDto;
import com.codingshuttle.linkedin.chat_service.entity.ChatMessage;
import com.codingshuttle.linkedin.chat_service.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository repository;

    public ChatMessage save(ChatMessageDto dto) {
        ChatMessage message = ChatMessage.builder()
                .senderId(dto.getSenderId()).receiverId(dto.getReceiverId())
                .content(dto.getContent()).createdAt(LocalDateTime.now()).build();
        message.setCreatedAt(LocalDateTime.now());
        return repository.save(message);
    }

    public List<ChatMessage> getConversation(Long senderId, Long receiverId) {
        return repository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderId(senderId, receiverId, senderId, receiverId);
    }
}
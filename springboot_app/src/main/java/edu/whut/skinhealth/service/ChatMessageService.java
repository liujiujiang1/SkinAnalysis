package edu.whut.skinhealth.service;

import edu.whut.skinhealth.dao.ChatMessageRepository;
import edu.whut.skinhealth.entity.ChatMessage;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public ChatMessage save(ChatMessage message) {
        if (message.getCreatedTime() == null) {
            message.setCreatedTime(new Timestamp(new Date(System.currentTimeMillis()).getTime()));
        }
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getRecentByUsername(String username) {
        List<ChatMessage> messages = chatMessageRepository.findTop50ByUsernameOrderByCreatedTimeDesc(username);
        Collections.reverse(messages);
        return messages;
    }
}

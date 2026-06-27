package edu.whut.skinhealth.dao;

import edu.whut.skinhealth.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findTop50ByUsernameOrderByCreatedTimeDesc(String username);
}

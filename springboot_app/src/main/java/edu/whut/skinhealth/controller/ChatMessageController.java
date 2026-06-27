package edu.whut.skinhealth.controller;

import edu.whut.skinhealth.entity.ChatMessage;
import edu.whut.skinhealth.service.ChatMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("chat-message")
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @GetMapping("user/{username}")
    public ResponseEntity<List<ChatMessage>> queryByUsername(@PathVariable("username") String username) {
        return new ResponseEntity<>(chatMessageService.getRecentByUsername(username), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ChatMessage> create(@RequestBody ChatMessage request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(chatMessageService.save(request), HttpStatus.OK);
    }
}

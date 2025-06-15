package com.example.Sliderbackend.Message;

import com.example.Sliderbackend.Message.Message;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDTO {
    private Long id;
    private String content;
    private LocalDateTime timestamp;
    private Long senderId;
    private String senderUsername;

    public MessageDTO(Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.timestamp = message.getTimestamp();
        this.senderId = message.getSender_id().getId();
        this.senderUsername = message.getSender_id().getUsername();
    }

    // Getters and Setters
}

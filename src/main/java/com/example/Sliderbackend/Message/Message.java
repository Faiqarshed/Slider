package com.example.Sliderbackend.Message;

import com.example.Sliderbackend.Profile;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    //@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonBackReference
    private Profile sender_id;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    //@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonBackReference
    private Profile receiver_id;

    private String content;
    private LocalDateTime timestamp;

    // Getters and setters (important for Jackson)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Profile getSender_id() { return sender_id; }
    public void setSender_id(Profile sender_id) { this.sender_id = sender_id; }

    public Profile getReceiver_id() { return receiver_id; }
    public void setReceiver_id(Profile receiver_id) { this.receiver_id = receiver_id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}

package com.example.Sliderbackend.Message;

import com.example.Sliderbackend.Message.MessageRepository;
import com.example.Sliderbackend.Profile;
import com.example.Sliderbackend.ProfileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private MessageRepository messageRepository;

    @MessageMapping("/chat")
    public void handleMessage(@Payload Map<String, Object> payload, Principal principal) {
        System.out.println("=== ChatController.handleMessage called ===");
        System.out.println("Payload: " + payload);
        System.out.println("Principal: " + principal);
        try {
            String content = (String) payload.get("content");
            Integer receiverId = (Integer) payload.get("receiver_id");
            System.out.println("content is " + content + " rec id is " + receiverId);

            // Get the sender (authenticated user) from Principal
            String senderUsername = principal.getName();
            System.out.println("Principle is " + senderUsername);
            Optional<Profile> sender = profileRepository.findByUsername(senderUsername);
            Profile sender1 = sender.get();
            Profile receiver = profileRepository.findById(Long.valueOf(receiverId)).orElse(null);

            if (!sender.isPresent() || receiver == null) {
                System.out.println("Invalid sender or receiver");
                return;
            }

            Message message = new Message();
            message.setContent(content);
            message.setSender_id(sender1);
            message.setReceiver_id(receiver);
            message.setTimestamp(LocalDateTime.now());

            messageRepository.save(message);

            // Send to the specific user's private queue
            messagingTemplate.convertAndSendToUser(
                    receiver.getUsername(),
                    "/queue/messages",
                    message
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package com.example.Sliderbackend.Message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT DISTINCT m.receiver_id.id FROM Message m WHERE m.sender_id.id = :senderId")
    List<Integer> findDistinctReceiverIdsBySenderId(@Param("senderId") int senderId);

    @Query("SELECT DISTINCT m.sender_id.id FROM Message m WHERE m.receiver_id.id = :receiverId")
    List<Integer> findDistinctSenderIdsByReceiverId(@Param("receiverId") int receiverId);

    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender_id.id = :senderId AND m.receiver_id.id = :receiverId) OR " +
            "(m.sender_id.id = :receiverId AND m.receiver_id.id = :senderId) " +
            "ORDER BY m.timestamp ASC")
    List<Message> findChatHistory(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);



}

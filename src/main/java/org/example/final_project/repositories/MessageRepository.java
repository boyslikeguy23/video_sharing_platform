package org.example.final_project.repositories;

import org.example.final_project.models.Message;
import org.example.final_project.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long>
{
    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId1 AND m.receiver.id = :userId2) " +
            "OR (m.sender.id = :userId2 AND m.receiver.id = :userId1) ORDER BY m.sentAt")
    List<Message> findConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

//    @Query("SELECT DISTINCT CASE " +
//            "WHEN m.sender.id = :userId THEN m.receiver " +
//            "ELSE m.sender END " +
//            "FROM Message m " +
//            "WHERE m.sender.id = :userId OR m.receiver.id = :userId " +
//            "ORDER BY m.sentAt DESC")
//    @Query("SELECT DISTINCT CASE " +
//            "WHEN m.sender.id = :userId THEN m.receiver " +
//            "ELSE m.sender END " +
//            "FROM Message m " +
//            "WHERE m.sender.id = :userId OR m.receiver.id = :userId " +
//            "ORDER BY m.sentAt DESC")
//    List<User> findRecentChatUsers(@Param("userId") Long userId);
    @Query("SELECT DISTINCT m.receiver FROM Message m WHERE m.sender.id = :userId")
    List<User> findReceiversBySender(@Param("userId") Long userId);

    @Query("SELECT DISTINCT m.sender FROM Message m WHERE m.receiver.id = :userId")
    List<User> findSendersByReceiver(@Param("userId") Long userId);

    @Query("SELECT m FROM Message m WHERE m.receiver.id = :userId AND m.read = false ORDER BY m.sentAt")
    List<Message> findUnreadMessages(@Param("userId") Long userId);

    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId1 AND m.receiver.id = :userId2) " +
           "OR (m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
           "ORDER BY m.sentAt DESC")
    List<Message> findMessagesBetweenUsersOrderedByDate(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}

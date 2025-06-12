//package org.example.final_project.dtos;
//
//import java.time.LocalDateTime;
//
//public class ChatMessage {
//    private String content;
//    private Integer senderId;
//    private Integer receiverId;
//    private MessageType type;
//    private LocalDateTime timestamp;
//
//    public enum MessageType {
//        CHAT,
//        JOIN,
//        LEAVE
//    }
//
//    // Getters and setters
//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//
//    public Integer getSenderId() {
//        return senderId;
//    }
//
//    public void setSenderId(Integer senderId) {
//        this.senderId = senderId;
//    }
//
//    public Integer getReceiverId() {
//        return receiverId;
//    }
//
//    public void setReceiverId(Integer receiverId) {
//        this.receiverId = receiverId;
//    }
//
//    public MessageType getType() {
//        return type;
//    }
//
//    public void setType(MessageType type) {
//        this.type = type;
//    }
//
//    public LocalDateTime getTimestamp() {
//        return timestamp;
//    }
//
//    public void setTimestamp(LocalDateTime timestamp) {
//        this.timestamp = timestamp;
//    }
//}
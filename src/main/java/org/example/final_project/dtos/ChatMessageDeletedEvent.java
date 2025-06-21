package org.example.final_project.dtos;

/**
 * DTO representing a message deletion event for WebSocket communication
 */
public class ChatMessageDeletedEvent {
    private Long messageId;
    private Long senderId;
    private Long receiverId;
    private String eventType = "MESSAGE_DELETED";

    // Constructors
    public ChatMessageDeletedEvent() {}

    public ChatMessageDeletedEvent(Long messageId, Long senderId, Long receiverId) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    // Getters and Setters
    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
package org.example.final_project.dtos;

import org.example.final_project.models.Message;

import java.time.LocalDateTime;

public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime sentAt;
    private boolean read;
    private UserDto sender;
    private UserDto receiver;

    // Constructors
    public ChatMessageResponse() {}

    public static ChatMessageResponse fromMessage(Message message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setSenderId(message.getSender().getId());
        response.setReceiverId(message.getReceiver().getId());
        response.setContent(message.getContent());
        response.setSentAt(message.getSentAt());
        response.setRead(message.isRead());

        // Convert sender to UserDto
        UserDto senderDto = new UserDto();
        senderDto.setId(message.getSender().getId());
        senderDto.setUsername(message.getSender().getUsername());
        senderDto.setName(message.getSender().getName());
        senderDto.setUserImage(message.getSender().getImage());
        response.setSender(senderDto);

        // Convert receiver to UserDto
        UserDto receiverDto = new UserDto();
        receiverDto.setId(message.getReceiver().getId());
        receiverDto.setUsername(message.getReceiver().getUsername());
        receiverDto.setName(message.getReceiver().getName());
        receiverDto.setUserImage(message.getReceiver().getImage());
        response.setReceiver(receiverDto);

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public UserDto getSender() {
        return sender;
    }

    public void setSender(UserDto sender) {
        this.sender = sender;
    }

    public UserDto getReceiver() {
        return receiver;
    }

    public void setReceiver(UserDto receiver) {
        this.receiver = receiver;
    }

    public ChatMessageResponse(Long id, Long senderId, Long receiverId, String content, LocalDateTime sentAt, boolean read, UserDto sender, UserDto receiver) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.sentAt = sentAt;
        this.read = read;
        this.sender = sender;
        this.receiver = receiver;
    }
}

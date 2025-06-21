package org.example.final_project.dtos;

import java.time.LocalDateTime;

public class RecentChatDto extends UserDto {
    private String lastMessage;
    private LocalDateTime sentAt;

    public RecentChatDto() {
        super();
    }

    public RecentChatDto(String username, String name, String userImage, String email, Long id, String lastMessage, LocalDateTime sentAt) {
        super(username, name, userImage, email, id);
        this.lastMessage = lastMessage;
        this.sentAt = sentAt;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
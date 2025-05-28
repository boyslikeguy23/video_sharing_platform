package org.example.final_project.dtos;

public class ChatMessageRequest {
    private Integer receiverId;
    private String content;

    // Getters and Setters
    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}

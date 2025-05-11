package org.example.final_project.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    private Chat chat;

    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName = "id")
    private User user;


    @Column(nullable = false, length = 500)
    private String messageText;

    @Column(nullable = false)
    @LastModifiedDate
    private Instant instant;

}

package org.example.final_project.payloads;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class GroupChatRequest {

    private List<Long> userIDs;
    private String chatName;
    private String chatImage;

}

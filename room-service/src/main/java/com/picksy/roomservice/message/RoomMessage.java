package com.picksy.roomservice.message;


import com.picksy.roomservice.util.MessageType;
import com.picksy.roomservice.util.ParticipantInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomMessage {
    private String roomCode;
    private MessageType type;
    private Long userId;
    private String username;
    private int participantCount;
    private List<ParticipantInfo> participantList;
}

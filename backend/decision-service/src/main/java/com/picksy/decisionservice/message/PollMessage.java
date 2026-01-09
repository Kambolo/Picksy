package com.picksy.decisionservice.message;

import com.picksy.decisionservice.util.MessageType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PollMessage {
    private Long userId;
    private List<Long> optionsId;
    private MessageType messageType;
    private int participantsCount;
}

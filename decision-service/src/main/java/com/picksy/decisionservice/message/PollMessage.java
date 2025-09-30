package com.picksy.decisionservice.message;

import com.picksy.decisionservice.util.MessageType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PollMessage {
    private Long optionId;
    private boolean selected;
    private MessageType messageType;
}

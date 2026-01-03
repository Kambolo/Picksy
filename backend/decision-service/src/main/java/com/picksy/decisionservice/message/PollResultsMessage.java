package com.picksy.decisionservice.message;

import com.picksy.decisionservice.model.PollDTO;
import com.picksy.decisionservice.util.MessageType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PollResultsMessage {
    private MessageType messageType;
    private List<PollDTO> polls;
}




package com.picksy.decisionservice.controller;

import com.picksy.decisionservice.message.PollMessage;
import com.picksy.decisionservice.service.DecisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DecisionController {
    private final DecisionService decisionService;

    @MessageMapping("/poll/{roomCode}/{categoryId}")
    public void handleMessages(@DestinationVariable String roomCode,
                               @DestinationVariable Long categoryId,
                               @Payload PollMessage PollMessage ){
        decisionService.messageHandling(roomCode, categoryId, PollMessage);
    }
}

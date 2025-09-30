package com.picksy.decisionservice.service;

import com.picksy.decisionservice.message.PollMessage;
import com.picksy.decisionservice.model.Choice;
import com.picksy.decisionservice.model.Poll;
import com.picksy.decisionservice.repository.ChoiceRepository;
import com.picksy.decisionservice.repository.PollRepository;
import com.picksy.decisionservice.util.MessageType;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class DecisionService {
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChoiceRepository choiceRepository;
    private final PollRepository pollRepository;
    private final OptionClient optionClient;

    public void messageHandling(String roomCode, Long catId, PollMessage pollMessage){
        switch(pollMessage.getMessageType()){
            case MessageType.SETUP:
                setup(roomCode, catId);

            case MessageType.VOTE:
                vote(roomCode, catId, pollMessage);
        }

    }

    @Transactional
    protected void setup(String roomCode, Long catId){

        List<Choice> choices = optionClient.getOptionsByCategory(catId)
                .map(optionDto -> Choice.builder()
                        .optionId(optionDto.getId())
                        .count(0)
                        .poll(null)
                        .build()
                )
                .collectList()
                .block();

        Poll poll = Poll.builder()
                .categoryId(catId)
                .roomCode(roomCode)
                .build();

        poll.setChoices(choices);

        pollRepository.save(poll);

        messagingTemplate.convertAndSend("/topic/poll/"+ roomCode + "/" + catId,
                new PollMessage(null, false, MessageType.START));
    }

    @Transactional
    protected void vote(String roomCode, Long catId, PollMessage pollMessage){
        pollRepository.findByRoomCodeAndCategoryId(roomCode, catId).orElseThrow(() -> new BadRequestException("Poll not found"));
        Choice choice = choiceRepository.findById(pollMessage.getOptionId()).orElseThrow(() -> new BadRequestException("Choice not found"));

        if(!pollMessage.isSelected()) return;

        choice.setCount(choice.getCount() + 1);
        choiceRepository.save(choice);
    }

}

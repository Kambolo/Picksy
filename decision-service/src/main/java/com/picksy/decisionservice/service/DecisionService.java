package com.picksy.decisionservice.service;

import com.picksy.decisionservice.message.PollMessage;
import com.picksy.decisionservice.message.PollResultsMessage;
import com.picksy.decisionservice.model.Choice;
import com.picksy.decisionservice.model.ChoiceDTO;
import com.picksy.decisionservice.model.Poll;
import com.picksy.decisionservice.model.PollDTO;
import com.picksy.decisionservice.repository.ChoiceRepository;
import com.picksy.decisionservice.repository.PollRepository;
import com.picksy.decisionservice.util.CategoryType;
import com.picksy.decisionservice.util.MessageType;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DecisionService {
  private final SimpMessageSendingOperations messagingTemplate;
  private final ChoiceRepository choiceRepository;
  private final PollRepository pollRepository;
  private final CategoryClient categoryClient;

  public void messageHandling(String roomCode, Long catId, PollMessage pollMessage) {
    System.out.println(pollMessage.toString());
    switch (pollMessage.getMessageType()) {
      case MessageType.SETUP:
        setup(roomCode, catId, pollMessage);
        break;

      case MessageType.VOTE:
        vote(roomCode, catId, pollMessage);
        break;

      case MessageType.END:
        end(roomCode, catId, pollMessage);
        break;

      case MessageType.UPDATE_PARTICIPANT_COUNT:
        updateParticipantsCount(roomCode, catId, pollMessage);
        break;

    }
  }

  @Transactional
  protected void setup(String roomCode, Long catId, PollMessage pollMessage) {
    try {
      // Try to find or create within the same transaction
      Poll poll =
          pollRepository
              .findByRoomCodeAndCategoryId(roomCode, catId)
              .orElseGet(() -> pollRepository.save(buildPoll(roomCode, catId, pollMessage)));

      messagingTemplate.convertAndSend(
          "/topic/poll/" + roomCode + "/" + catId,
          new PollMessage(null, MessageType.START, poll.getParticipantsCount()));

    } catch (DataIntegrityViolationException ex) {
      // Another thread inserted concurrently – now fetch and proceed
      Poll poll =
          pollRepository
              .findByRoomCodeAndCategoryId(roomCode, catId)
              .orElseThrow(() -> new IllegalStateException("Poll should exist now"));
    }
  }

  private Poll buildPoll(String roomCode, Long catId, PollMessage pollMessage) {
    List<Choice> choices =
        categoryClient
            .getOptionsByCategory(catId)
            .map(
                optionDto ->
                    Choice.builder().optionId(optionDto.getId()).count(0).poll(null).build())
            .collectList()
            .block();

    System.out.println("LOADED CHOICES: " + choices);

    CategoryType type = categoryClient.getCategoryType(catId).block();

    Poll poll =
        Poll.builder()
            .categoryId(catId)
            .roomCode(roomCode)
            .participantsCount(pollMessage.getParticipantsCount())
            .categoryType(type)
            .build();

    poll.setChoices(choices);
    return poll;
  }

  // In PICK polls there is only one vote that have all the user choices
  // In SWIPE polls one vote have one choice innit
  @Transactional
  protected void vote(String roomCode, Long catId, PollMessage pollMessage) {
    Poll poll =
        pollRepository
            .findByRoomCodeAndCategoryId(roomCode, catId)
            .orElseThrow(() -> new BadRequestException("Poll not found"));

    // SWIPE
    if (poll.getCategoryType().equals(CategoryType.SWIPE)) {
      // There is only one option at once in SWIPE voting
      Choice choice =
          choiceRepository
              .findByPollIdAndOptionId(poll.getId(), pollMessage.getOptionsId().getFirst())
              .orElseThrow(() -> new BadRequestException("Option not found"));
      choice.setCount(choice.getCount() + 1);
      choiceRepository.save(choice);

      // We have a match
      if (choice.getCount() == poll.getParticipantsCount()) {
        // Winner is a List that only contain id of the matched choice
        List<Long> winner = new ArrayList<>();
        winner.add(choice.getOptionId());

        System.out.println(winner);

        messagingTemplate.convertAndSend(
            "/topic/poll/" + roomCode + "/" + catId,
            new PollMessage(winner, MessageType.MATCH, poll.getParticipantsCount()));
      }
    }
    // PICK
    else if (poll.getCategoryType().equals(CategoryType.PICK)) {
      // Find choices based on categoryIds provided from the user
      List<Choice> choices =
          choiceRepository.findByPollIdAndOptionIdIn(poll.getId(), pollMessage.getOptionsId());

      // Check if all choices were found
      if (choices.size() != pollMessage.getOptionsId().size())
        throw new BadRequestException("Not all options were found");

      // increase count for choices
      for (Choice choice : choices) {
        choice.setCount(choice.getCount() + 1);
      }

      poll.setVotedCount(poll.getVotedCount() + 1);
      pollRepository.save(poll);

      if (poll.getVotedCount() == pollMessage.getParticipantsCount()) {
        // if everyone voted send END message
        messagingTemplate.convertAndSend(
            "/topic/poll/" + roomCode + "/" + catId,
            new PollMessage(null, MessageType.END, poll.getParticipantsCount()));
      }
    }
  }

  protected void end(String roomCode, Long catId, PollMessage pollMessage) {
    pollRepository
        .findByRoomCodeAndCategoryId(roomCode, catId)
        .orElseThrow(() -> new BadRequestException("Poll not found"));
    messagingTemplate.convertAndSend(
        "/topic/poll/" + roomCode + "/" + catId,
        new PollMessage(null, MessageType.END, pollMessage.getParticipantsCount()));
  }

  @Transactional
  protected void updateParticipantsCount(String roomCode, Long catId, PollMessage pollMessage) {
    Poll poll =
        pollRepository
            .findByRoomCodeAndCategoryId(roomCode, catId)
            .orElseThrow(() -> new BadRequestException("Poll not found"));
    poll.setParticipantsCount(pollMessage.getParticipantsCount());
    pollRepository.save(poll);
  }

  @Transactional
  protected void increaseVoteCount(String roomCode, Long catId, PollMessage pollMessage) {
    Poll poll =
        pollRepository
            .findByRoomCodeAndCategoryId(roomCode, catId)
            .orElseThrow(() -> new BadRequestException("Poll not found"));

    poll.setVotedCount(poll.getVotedCount() + 1);
    pollRepository.save(poll);

    List<Long> votedCount = new ArrayList<>();
    votedCount.add(Long.parseLong(String.valueOf(poll.getVotedCount())));

    messagingTemplate.convertAndSend(
        "/topic/poll/" + roomCode + "/" + catId,
        new PollMessage(votedCount, MessageType.INCREASE_VOTE_COUNT, pollMessage.getParticipantsCount()));
  }

  protected void getResults(String roomCode) {
    List<Poll> polls = pollRepository.findAllWithChoicesByRoomCode(roomCode);

    if (polls.isEmpty()) {
      throw new BadRequestException("Poll not found");
    }

    List<ChoiceDTO> choiceDTOS = new ArrayList<>();
    List<PollDTO> pollDTOS = new ArrayList<>();

    for (Poll poll : polls) {
        for(Choice choice : poll.getChoices()) {
            choiceDTOS.add(new ChoiceDTO(choice.getOptionId(), choice.getCount()));
        }
        pollDTOS.add(new PollDTO(poll.getId(), poll.getCategoryId(), choiceDTOS));
    }

      messagingTemplate.convertAndSend(
              "/topic/poll/results/" + roomCode,
              new PollResultsMessage(MessageType.RESULTS, pollDTOS)
      );

  }
}

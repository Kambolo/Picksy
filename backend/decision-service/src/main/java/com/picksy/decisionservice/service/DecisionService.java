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
  private final RoomClient roomClient;

  @Transactional
  public void messageHandling(String roomCode, Long catId, PollMessage pollMessage) {
    System.out.println(roomClient.isParticipant(roomCode, pollMessage.getUserId()).block());
    if (Boolean.FALSE.equals(roomClient.isParticipant(roomCode, pollMessage.getUserId()).block()))
        return;

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

      case MessageType.INCREASE_VOTED_COUNT:
        increaseVotedCount(roomCode, catId, pollMessage);
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
          new PollMessage((long)0, null, MessageType.START, poll.getParticipantsCount()));

    } catch (DataIntegrityViolationException ex) {
      // Another thread inserted concurrently – now fetch and proceed
      Poll poll =
          pollRepository
              .findByRoomCodeAndCategoryId(roomCode, catId)
              .orElseThrow(() -> new IllegalStateException("Poll should exist now"));
    }
  }

  private Poll buildPoll(String roomCode, Long catId, PollMessage pollMessage) {
    CategoryType type = categoryClient.getCategoryType(catId).block();

    return Poll.builder()
        .categoryId(catId)
        .roomCode(roomCode)
        .participantsCount(pollMessage.getParticipantsCount())
        .categoryType(type)
        .build();
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
              .orElse(
                  Choice.builder()
                      .optionId(pollMessage.getOptionsId().getFirst())
                      .count(0)
                      .poll(poll)
                      .build());
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
            new PollMessage((long)0, winner, MessageType.MATCH, poll.getParticipantsCount()));
      }
    }
    // PICK
    else if (poll.getCategoryType().equals(CategoryType.PICK)) {
      // Find choices based on categoryIds provided from the user
      List<Choice> choices =
          choiceRepository.findByPollIdAndOptionIdIn(poll.getId(), pollMessage.getOptionsId());

      for (Long optionId : pollMessage.getOptionsId()) {
        boolean anyMatch =
            choices.stream().anyMatch(choice -> choice.getOptionId().equals(optionId));

        if (!anyMatch) choices.add(Choice.builder().optionId(optionId).poll(poll).count(0).build());
      }

      // increase count for choices
      for (Choice choice : choices) {
        choice.setCount(choice.getCount() + 1);
        choiceRepository.save(choice);
      }
      poll.setVotedCount(poll.getVotedCount() + 1);
      pollRepository.save(poll);

      if (poll.getVotedCount() == pollMessage.getParticipantsCount()) {
        // if everyone voted send END message
        messagingTemplate.convertAndSend(
            "/topic/poll/" + roomCode + "/" + catId,
            new PollMessage((long)0, null, MessageType.END, poll.getParticipantsCount()));
      }
    }
  }

  protected void end(String roomCode, Long catId, PollMessage pollMessage) {
    pollRepository
        .findByRoomCodeAndCategoryId(roomCode, catId)
        .orElseThrow(() -> new BadRequestException("Poll not found"));
    messagingTemplate.convertAndSend(
        "/topic/poll/" + roomCode + "/" + catId,
        new PollMessage((long)0, null, MessageType.END, pollMessage.getParticipantsCount()));
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
  protected void increaseVotedCount(String roomCode, Long catId, PollMessage pollMessage) {
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
        new PollMessage(
                (long)0, votedCount, MessageType.INCREASE_VOTED_COUNT, pollMessage.getParticipantsCount()));
  }

  public List<PollDTO> getResults(String roomCode) {
    List<Poll> polls = pollRepository.findAllWithChoicesByRoomCode(roomCode);

    if (polls.isEmpty()) {
      throw new BadRequestException("Poll not found");
    }

    List<PollDTO> pollDTOS = new ArrayList<>();

    for (Poll poll : polls) {
      List<ChoiceDTO> choiceDTOS = new ArrayList<>();
      for (Choice choice : poll.getChoices()) {
        choiceDTOS.add(new ChoiceDTO(choice.getOptionId(), choice.getCount()));
      }
      pollDTOS.add(
          new PollDTO(poll.getId(), poll.getCategoryId(), choiceDTOS, poll.getParticipantsCount()));
    }

    return pollDTOS;
  }
}

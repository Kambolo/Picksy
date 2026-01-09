package com.picksy.roomservice.service;

import com.picksy.roomservice.message.RoomMessage;
import com.picksy.roomservice.model.PollDTO;
import com.picksy.roomservice.model.Room;
import com.picksy.roomservice.model.SetCategoryKey;
import com.picksy.roomservice.repository.RoomRepository;
import com.picksy.roomservice.request.RoomCreateRequest;
import com.picksy.roomservice.request.RoomUserInfo;
import com.picksy.roomservice.response.PollDTOResponse;
import com.picksy.roomservice.response.RoomDTO;
import com.picksy.roomservice.util.MessageType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RoomService {

  private final RoomRepository roomRepository;
  private final SimpMessageSendingOperations messagingTemplate;
  private final DecisionClient decisionClient;

  @Transactional
  public RoomDTO createRoom(RoomCreateRequest request, Long userId) {
    Room newRoom =
        Room.builder()
            .name(request.name())
            .roomCode(generateUniqueRoomCode())
            .ownerId(userId)
            .categorySet(request.categories())
            .votingStarted(false)
            .roomClosed(false)
            .createdAt(LocalDateTime.now().plusHours(1))
            .build();

    roomRepository.save(newRoom);

    return mapToDTO(newRoom);
  }

  @Transactional
  public void deleteRoom(String roomCode) throws BadRequestException {
    Optional<Room> room = roomRepository.findByRoomCode(roomCode);
    if (room.isEmpty()) throw new BadRequestException("Room does not exist");
    roomRepository.delete(room.get());
  }

  @Transactional
  public void markRoomAsClosed(String roomCode) throws BadRequestException {
    Optional<Room> room = roomRepository.findByRoomCode(roomCode);
    if (room.isEmpty()) throw new BadRequestException("Room does not exist");
    room.get().setRoomClosed(true);
    roomRepository.save(room.get());
  }

  @Transactional
  public void closeRoom(Long ownerId, String roomCode) throws BadRequestException {
    Optional<Room> room = roomRepository.findByRoomCode(roomCode);

    if (room.isEmpty()) throw new BadRequestException("Room does not exist");

    if (!Objects.equals(room.get().getOwnerId(), ownerId))
      throw new BadRequestException("You have to be room owner to close the room.");

    room.get().setRoomClosed(true);

    roomRepository.save(room.get());

    messagingTemplate.convertAndSend(
        "/topic/room/" + roomCode,
        new RoomMessage(MessageType.ROOM_CLOSED, room.get().getOwnerId(), null, null));
  }

  @Transactional
  public void startVoting(Long ownerId, String roomCode) throws BadRequestException {
    Optional<Room> room = roomRepository.findByRoomCode(roomCode);

    if (room.isEmpty()) throw new BadRequestException("Room does not exist");
    if (!Objects.equals(room.get().getOwnerId(), ownerId))
      throw new BadRequestException("You have to be room owner to start voting.");

    room.get().setVotingStarted(true);

    int currentCategoryIndex = room.get().getCurrentCategoryIndex();

    messagingTemplate.convertAndSend(
        "/topic/room/" + roomCode,
        new RoomMessage(
            MessageType.VOTING_STARTED,
            room.get().getOwnerId(),
            null,
            room.get().getCategorySet().get(currentCategoryIndex)));

    room.get().setCurrentCategoryIndex(currentCategoryIndex);
    roomRepository.save(room.get());
  }

  @Transactional
  public void nextCategory(Long ownerId, String roomCode) throws BadRequestException {
    Optional<Room> room = roomRepository.findByRoomCode(roomCode);

    if (room.isEmpty()) throw new BadRequestException("Room does not exist");
    if (!Objects.equals(room.get().getOwnerId(), ownerId))
      throw new BadRequestException("You have to be room owner to change voting category.");

    MessageType type = MessageType.NEXT_CATEGORY;
    int currentCategoryIndex = room.get().getCurrentCategoryIndex() + 1;

    SetCategoryKey category =
        room.get().getCategorySet().get(room.get().getCategorySet().size() - 1);

    if (currentCategoryIndex >= room.get().getCategorySet().size()) {
      type = MessageType.VOTING_FINISHED;
      room.get().setRoomClosed(true);
    } else {
      category = room.get().getCategorySet().get(currentCategoryIndex);
    }

    messagingTemplate.convertAndSend(
        "/topic/room/" + roomCode, new RoomMessage(type, room.get().getOwnerId(), null, category));

    room.get().setCurrentCategoryIndex(currentCategoryIndex);
    roomRepository.save(room.get());
  }

  @Transactional
  public Long joinRoom(String roomCode, RoomUserInfo userInfo) throws BadRequestException {
    Optional<Room> optionalRoom = roomRepository.findByRoomCode(roomCode);

    if (optionalRoom.isEmpty()) throw new BadRequestException("Room does not exist");

    Room room = optionalRoom.get();

    if (room.isRoomClosed()) throw new BadRequestException("Room is closed.");
    if (room.isVotingStarted()) throw new BadRequestException("Voting has already started.");

    Long userId = userInfo.userId();

    // If user is not logged (userId == -1), generate random negative UUID
    if (userId < 0) {
        long maxSafeJS = 9007199254740991L;
      do {
          userId = -ThreadLocalRandom.current().nextLong(1, maxSafeJS);
      } while (room.getParticipants().containsKey(userId));
    } else {
      if (room.getParticipants().containsKey(userId)) {
        throw new BadRequestException("You have already joined this room.");
      }
    }

    Map.Entry<Long, String> newParticipant = room.addParticipant(userId, userInfo.username());

    roomRepository.save(room);

    messagingTemplate.convertAndSend(
        "/topic/room/" + roomCode,
        new RoomMessage(
            MessageType.JOIN, newParticipant.getKey(), newParticipant.getValue(), null));

    return newParticipant.getKey();
  }

  public RoomDTO getRoomDetails(String roomCode) throws BadRequestException {
    Optional<Room> room = roomRepository.findByRoomCode(roomCode);
    if (room.isEmpty()) throw new BadRequestException("Room does not exist");
    return mapToDTO(room.get());
  }

  private String generateUniqueRoomCode() {
    String code;
    do {
      int number = (int) (Math.random() * 9000000) + 1000000;
      code = String.valueOf(number);
    } while (roomRepository.existsByRoomCode(code));
    return code;
  }

  @Transactional
  public void leaveRoom(String roomCode, RoomUserInfo userInfo) throws BadRequestException {
    Optional<Room> optionalRoom = roomRepository.findByRoomCode(roomCode);

    if (optionalRoom.isEmpty()) throw new BadRequestException("Room does not exist");

    Room room = optionalRoom.get();

    if (!room.getParticipants().containsKey(userInfo.userId())) {
      throw new BadRequestException("You have not joined this room.");
    }

    // When owner exits before end
    if (Objects.equals(userInfo.userId(), room.getOwnerId())) {
      messagingTemplate.convertAndSend(
          "/topic/room/" + roomCode,
          new RoomMessage(MessageType.ROOM_CLOSED, room.getOwnerId(), null, null));

      markRoomAsClosed(roomCode);
      return;
    }

    room.removeParticipant(userInfo.userId());
    roomRepository.save(room);

    messagingTemplate.convertAndSend(
        "/topic/room/" + roomCode,
        new RoomMessage(MessageType.LEAVE, userInfo.userId(), null, null));
  }

  @Transactional
  public void endVoting(String roomCode, Long ownerId) throws BadRequestException {
    Optional<Room> room = roomRepository.findByRoomCode(roomCode);

    if (room.isEmpty()) throw new BadRequestException("Room does not exist");
    if (!Objects.equals(room.get().getOwnerId(), ownerId))
      throw new BadRequestException("You have to be room owner to change voting category.");

    if (room.get().isRoomClosed()) throw new BadRequestException("Room is closed.");
    if (!room.get().isVotingStarted()) throw new BadRequestException("Voting hasn't started.");

    messagingTemplate.convertAndSend(
        "/topic/room/" + roomCode, new RoomMessage(MessageType.VOTING_FINISHED, null, null, null));
  }

  public List<PollDTOResponse> getPolls(String roomCode) throws BadRequestException {
    // Get polls from decision service using synchronous communication (block)
    List<PollDTO> pollDTOS = decisionClient.getResults(roomCode).collectList().block();

    // Get category ids from room service
    List<SetCategoryKey> categories = roomRepository.findAllSetCategoriesByRoomCode(roomCode);

    List<PollDTOResponse> pollDTOResponses = new ArrayList<>();

    for (SetCategoryKey categorySet : categories) {
      // Check if a poll with this categoryId exists in the results
      List<PollDTO> pollDTOs =
          pollDTOS.stream()
              .filter(p -> p.categoryId().equals(categorySet.getCategoryId()))
              .toList();

      if (pollDTOs.isEmpty()) {
        // Create an empty PollDTO for a category that has no voting data
        pollDTOResponses.add(
            new PollDTOResponse(
                null, // poll id is null because it doesn't exist yet
                categorySet,
                null, // or an empty list,
                0));
      } else {
        PollDTO pollDTO = pollDTOs.getFirst();
        pollDTOResponses.add(
            new PollDTOResponse(
                pollDTO.pollId(), categorySet, pollDTO.choices(), pollDTO.participantsCount()));
      }
    }
    return pollDTOResponses;
  }

  public int getParticipantsCount(String roomCode) throws BadRequestException {
    if (!roomRepository.existsByRoomCode(roomCode))
      throw new BadRequestException("Room does not exist.");
    return roomRepository.getParticipantsCountByRoomCode(roomCode);
  }

  public List<RoomDTO> getAllClosedRoomsForUser(Long userId) throws BadRequestException {
    List<Room> rooms = roomRepository.findAllClosedByParticipant(userId);
    System.out.println("rooms: " + rooms);
    return rooms.stream().map(this::mapToDTO).toList();
  }

  private RoomDTO mapToDTO(Room room) {
    return new RoomDTO(
        room.getRoomCode(),
        room.getName(),
        room.getCategorySet(),
        room.isVotingStarted(),
        room.isRoomClosed(),
        room.getParticipants(),
        room.getOwnerId(),
        room.getCreatedAt());
  }

  public Boolean isParticipant(String roomCode, Long userId) {
    if (!roomRepository.existsByRoomCode(roomCode)) return false;
    System.out.println("user id: " + userId);
    return roomRepository.existsParticipantInRoom(roomCode, userId);
  }
}

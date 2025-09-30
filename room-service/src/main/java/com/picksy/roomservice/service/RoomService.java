package com.picksy.roomservice.service;

import com.picksy.roomservice.message.RoomMessage;
import com.picksy.roomservice.model.Room;
import com.picksy.roomservice.repository.RoomRepository;
import com.picksy.roomservice.request.RoomCreateRequest;
import com.picksy.roomservice.response.RoomDTO;
import com.picksy.roomservice.util.MessageType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    @Transactional
    public RoomDTO createRoom(RoomCreateRequest request){
        Room newRoom = Room.builder()
                .name(request.name())
                .roomCode(generateUniqueRoomCode())
                .ownerId(request.ownerId())
                .categoryIds(request.categoryIds())
                .voting_started(false)
                .room_closed(false)
                .build();

        roomRepository.save(newRoom);

        return new RoomDTO(
                newRoom.getRoomCode(),
                newRoom.getName(),
                newRoom.getCategoryIds(),
                newRoom.isVoting_started(),
                newRoom.isRoom_closed(),
               null);
    }

    @Transactional
    public void deleteRoom(String roomCode) throws BadRequestException {
        Optional<Room> room = roomRepository.findByRoomCode(roomCode);
        if(room.isEmpty()) throw new BadRequestException("Room does not exist");
        roomRepository.delete(room.get());
    }

    @Transactional
    public void markRoomAsClosed(String roomCode)throws BadRequestException {
        Optional<Room> room = roomRepository.findByRoomCode(roomCode);
        if(room.isEmpty()) throw new BadRequestException("Room does not exist");
        room.get().setRoom_closed(true);
        roomRepository.save(room.get());
    }

    @Transactional
    public void closeRoom(Long ownerId, String roomCode) throws BadRequestException {
        Optional<Room> room = roomRepository.findByRoomCode(roomCode);

        if(room.isEmpty()) throw new BadRequestException("Room does not exist");

        if(!Objects.equals(room.get().getOwnerId(), ownerId)) throw new BadRequestException("You have to be room owner to close the room.");

        room.get().setRoom_closed(true);

        roomRepository.save(room.get());

        messagingTemplate.convertAndSend("/topic/room/"+ roomCode,
                new RoomMessage(MessageType.ROOM_CLOSED, room.get().getOwnerId(), null, null));
    }

    @Transactional
    public void startVoting(Long ownerId, String roomCode) throws BadRequestException {
        Optional<Room> room = roomRepository.findByRoomCode(roomCode);

        if(room.isEmpty()) throw new BadRequestException("Room does not exist");
        if(!Objects.equals(room.get().getOwnerId(), ownerId)) throw new BadRequestException("You have to be room owner to start voting.");

        room.get().setVoting_started(true);

        int currentCategoryIndex = room.get().getCurrentCategoryIndex();

        messagingTemplate.convertAndSend("/topic/room/"+ roomCode,
                new RoomMessage(MessageType.VOTING_STARTED,
                                room.get().getOwnerId(),
                        null,
                                room.get().getCategoryIds().get(currentCategoryIndex)));

        room.get().setCurrentCategoryIndex(currentCategoryIndex + 1);
        roomRepository.save(room.get());
    }

    @Transactional
    public void nextCategory(Long ownerId,String roomCode) throws BadRequestException {
        Optional<Room> room = roomRepository.findByRoomCode(roomCode);

        if(room.isEmpty()) throw new BadRequestException("Room does not exist");
        if(!Objects.equals(room.get().getOwnerId(), ownerId)) throw new BadRequestException("You have to be room owner to change voting category.");

        int currentCategoryIndex = room.get().getCurrentCategoryIndex();

        messagingTemplate.convertAndSend("/topic/room/"+ roomCode,
                new RoomMessage(MessageType.NEXT_CATEGORY,
                        room.get().getOwnerId(),
                        null,
                        room.get().getCategoryIds().get(currentCategoryIndex)));

        room.get().setCurrentCategoryIndex(currentCategoryIndex + 1);
        roomRepository.save(room.get());
    }

    @Transactional
    public void joinRoom(String roomCode, RoomMessage roomMessage) throws BadRequestException {
        if(!roomRepository.existsByRoomCode(roomCode)) throw new BadRequestException("Room does not exist.");

        Room room = roomRepository.findByRoomCode(roomCode).get();

        if(room.isRoom_closed()) throw new BadRequestException("Room is closed.");
        if(room.isVoting_started()) throw new BadRequestException("Voting has already started.");

        Map.Entry<Long, String> newParticipant = room.addParticipant(roomMessage.getUserId(), roomMessage.getUsername());

        messagingTemplate.convertAndSend("/topic/room/"+ roomCode,
               new RoomMessage(MessageType.JOIN, newParticipant.getKey(), newParticipant.getValue(), null));
    }

    public RoomDTO getRoomDetails(String roomCode) throws BadRequestException {
        Optional<Room> room = roomRepository.findByRoomCode(roomCode);
        if(room.isEmpty()) throw new BadRequestException("Room does not exist");
        return new RoomDTO(room.get().getRoomCode(),
                room.get().getName(),
                room.get().getCategoryIds(),
                room.get().isVoting_started(),
                room.get().isRoom_closed(),
                room.get().getParticipants());
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
    public void leaveRoom(String roomCode, RoomMessage roomMessage) throws BadRequestException {
        if(!roomRepository.existsByRoomCode(roomCode)) throw new BadRequestException("Room does not exist.");

        Room room = roomRepository.findByRoomCode(roomCode).get();

        // When owner exits before end
        if(Objects.equals(roomMessage.getUserId(), room.getOwnerId())){
            messagingTemplate.convertAndSend("/topic/room/"+ roomCode,
                    new RoomMessage(MessageType.ROOM_CLOSED, room.getOwnerId(), null, null));

            markRoomAsClosed(roomCode);
            return;
        }

        room.removeParticipant(roomMessage.getUserId());
        messagingTemplate.convertAndSend("/topic/room/"+ roomCode,
                new RoomMessage(MessageType.LEAVE, roomMessage.getUserId(), null, null));

    }
}

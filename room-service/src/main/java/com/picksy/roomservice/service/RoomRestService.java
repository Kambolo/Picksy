package com.picksy.roomservice.service;

import com.picksy.roomservice.model.Room;
import com.picksy.roomservice.repository.RoomRepository;
import com.picksy.roomservice.response.RoomDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomRestService {

    private final RoomRepository roomRepository;

    @Transactional
    public void createRoom(Long ownerId, String name){
        Room newRoom = Room.builder()
                .name(name)
                .roomCode(generateUniqueRoomCode())
                .ownerId(ownerId)
                .voting_started(false)
                .build();

        roomRepository.save(newRoom);
    }

    @Transactional
    public void deleteRoom(String roomCode) throws BadRequestException {
        Optional<Room> room = roomRepository.findByRoomCode(roomCode);
        if(room.isEmpty()) throw new BadRequestException("Room does not exist");
        roomRepository.delete(room.get());
    }

    public RoomDTO getRoomDetails(String roomCode) throws BadRequestException {
        Optional<Room> room = roomRepository.findByRoomCode(roomCode);
        if(room.isEmpty()) throw new BadRequestException("Room does not exist");
        return new RoomDTO(room.get().getRoomCode(), room.get().getName(), room.get().getOwnerId(), room.get().isVoting_started());
    }

    private String generateUniqueRoomCode() {
        String code;
        do {
            int number = (int) (Math.random() * 9000000) + 1000000;
            code = String.valueOf(number);
        } while (roomRepository.existsByRoomCode(code));
        return code;
    }
}

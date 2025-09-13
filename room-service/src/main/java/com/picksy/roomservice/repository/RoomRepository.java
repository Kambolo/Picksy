package com.picksy.roomservice.repository;

import com.picksy.roomservice.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByRoomCode(String roomCode);
    Optional<Room> findByRoomCode(String roomCode);
}

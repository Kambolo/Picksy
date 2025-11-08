package com.picksy.roomservice.repository;

import com.picksy.roomservice.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByRoomCode(String roomCode);
    Optional<Room> findByRoomCode(String roomCode);

    @Query("SELECT c FROM Room r JOIN r.categoryIds c WHERE r.roomCode = :roomCode")
    List<Long> findAllCategoryIdsByRoomCode(String roomCode);

    @Query("SELECT COUNT(p) FROM Room r JOIN r.participants p WHERE r.roomCode = :roomCode")
    int getParticipantsCountByRoomCode(String roomCode);
}

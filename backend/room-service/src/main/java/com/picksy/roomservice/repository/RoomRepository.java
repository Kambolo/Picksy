package com.picksy.roomservice.repository;

import com.picksy.roomservice.model.Room;
import com.picksy.roomservice.model.SetCategoryKey; // Musisz to zaimportować!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
  boolean existsByRoomCode(String roomCode);

  Optional<Room> findByRoomCode(String roomCode);

  @Query("SELECT sc FROM Room r JOIN r.categorySet sc WHERE r.roomCode = :roomCode")
  List<SetCategoryKey> findAllSetCategoriesByRoomCode(@Param("roomCode") String roomCode);

  @Query("SELECT COUNT(key(p)) FROM Room r JOIN r.participants p WHERE r.roomCode = :roomCode")
  int getParticipantsCountByRoomCode(@Param("roomCode") String roomCode);

  @Query(
      "SELECT r FROM Room r JOIN r.participants p WHERE KEY(p) = :userId AND r.roomClosed = TRUE AND r.votingStarted = TRUE")
  List<Room> findAllClosedByParticipant(@Param("userId") Long userId);

  @Query(
"""
    SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
    FROM Room r
    JOIN r.participants p
    WHERE r.roomCode = :roomCode
      AND KEY(p) = :participantId
""")
  boolean existsParticipantInRoom(
      @Param("roomCode") String roomCode, @Param("participantId") Long participantId);

    @Modifying
    @Query(value = "DELETE FROM room_set_categories WHERE category_id_col = :categoryId", nativeQuery = true)
    void removeFromAllRoomsByCategoryId(@Param("categoryId") Long categoryId);

    @Modifying
    @Query(value = "DELETE FROM room_set_categories WHERE set_id_col = :setId", nativeQuery = true)
    void removeFromAllRoomsBySetId(@Param("setId") Long setId);
}

package com.picksy.decisionservice.repository;

import com.picksy.decisionservice.model.Poll;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Long> {
    Optional<Poll> findByRoomCodeAndCategoryId(String roomCode, Long categoryId);
    boolean existsByRoomCodeAndCategoryId(String roomCode, Long categoryId);
    Optional<List<Poll>> findAllByRoomCode(String roomCode);
    @Query("SELECT p FROM Poll p LEFT JOIN FETCH p.choices WHERE p.roomCode = :roomCode")
    List<Poll> findAllWithChoicesByRoomCode(String roomCode);
}

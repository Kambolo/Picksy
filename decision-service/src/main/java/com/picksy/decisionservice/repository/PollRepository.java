package com.picksy.decisionservice.repository;

import com.picksy.decisionservice.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Long> {
    Optional<Poll> findByRoomCodeAndCategoryId(String roomCode, Long categoryId);
}

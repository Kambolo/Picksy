package com.picksy.decisionservice.repository;

import com.picksy.decisionservice.model.Choice;
import com.picksy.decisionservice.model.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    List<Choice> findByPollIdAndOptionIdIn(Long poll_id, Collection<Long> optionId);
    Optional<Choice> findByPollIdAndOptionId(Long poll_id, Long optionId);
    Optional<List<Choice>> findAllByPoll(Poll poll);
    @Transactional
    @Modifying
    @Query("DELETE FROM Choice c WHERE c.optionId = :optionId")
    void deleteByOptionId(Long optionId);
}

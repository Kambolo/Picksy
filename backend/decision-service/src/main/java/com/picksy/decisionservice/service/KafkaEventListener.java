package com.picksy.decisionservice.service;

import com.picksy.DeletionEvent;
import com.picksy.TypeUpdateEvent;
import com.picksy.decisionservice.model.Poll;
import com.picksy.decisionservice.repository.ChoiceRepository;
import com.picksy.decisionservice.repository.PollRepository;
import com.picksy.decisionservice.util.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaEventListener {

    private final PollRepository pollRepository;
    private final ChoiceRepository choiceRepository;

    @KafkaListener(topics = "category-deletion-topic", groupId = "room-service-group")
    @Transactional
    public void handleDeletion(DeletionEvent event) {
        log.info("Delete event: {} for ID: {}", event.getType(), event.getId());

        if ("CATEGORY".equalsIgnoreCase(event.getType())) {
            pollRepository.deleteByCategoryId(event.getId());
        } else if ("OPTION".equalsIgnoreCase(event.getType())) {
            choiceRepository.deleteByOptionId(event.getId());
        }
    }

    @KafkaListener(topics = "category-type-update-topic", groupId = "decision-group")
    @Transactional
    public void handleTypeUpdate(TypeUpdateEvent event) {
        try {
            CategoryType newType = CategoryType.valueOf(event.getNewType());

            List<Poll> polls = pollRepository.findAllByCategoryId(event.getCategoryId());

            for (Poll poll : polls) {
                poll.setCategoryType(newType);
            }

            pollRepository.saveAll(polls);

        } catch (IllegalArgumentException e) {
            System.err.println("Unrecognized category type: " + event.getNewType());
        }
    }
}

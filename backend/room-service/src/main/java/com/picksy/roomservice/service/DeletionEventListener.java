package com.picksy.roomservice.service;


import com.picksy.DeletionEvent;
import com.picksy.roomservice.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeletionEventListener {

    private final RoomRepository roomRepository;

    @KafkaListener(topics = "category-deletion-topic", groupId = "room-service-group")
    @Transactional
    public void handleDeletion(DeletionEvent event) {
        log.info("Delete event: {} for ID: {}", event.getType(), event.getId());

        if ("CATEGORY".equalsIgnoreCase(event.getType())) {
            roomRepository.removeFromAllRoomsByCategoryId(event.getId());
        } else if ("SET".equalsIgnoreCase(event.getType())) {
            roomRepository.removeFromAllRoomsBySetId(event.getId());
        }
    }
}

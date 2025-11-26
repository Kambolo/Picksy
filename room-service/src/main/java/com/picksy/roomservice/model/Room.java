package com.picksy.roomservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name="room_code")
    private String roomCode;

    private String name;

    @Column(name="category_id")
    @ElementCollection
    private List<Long> categoryIds;

    @Column(name="voting_started")
    private boolean votingStarted;

    @Column(name="room_closed")
    private boolean roomClosed;

    @Column(name="owner_id")
    private Long ownerId;

    @ElementCollection
    @CollectionTable(
            name = "room_participants",
            joinColumns = @JoinColumn(name = "room_id")
    )
    @MapKeyColumn(name = "user_id")
    @Column(name = "username")
    private Map<Long, String> participants;

    @Column(name="current_category_idx")
    private int currentCategoryIndex = 0;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    public Map.Entry<Long, String> addParticipant(Long id, String username){
        if(participants == null){
            participants = new HashMap<>();
        }

        participants.put(id, username);

        return Map.entry(id, username);
    }

    public void addCategoryId(Long id){
        if(categoryIds == null){
            categoryIds = new ArrayList<>();
        }
        categoryIds.add(id);
    }

    public void removeParticipant(Long id){
        participants.remove(id);
    }

    public int getParticipantCount(){
        return participants != null ? participants.size() : 0;
    }

}

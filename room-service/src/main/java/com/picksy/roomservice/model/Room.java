package com.picksy.roomservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(unique = true)
    private String roomCode;

    private String name;

    @ElementCollection
    private List<Long> categoryIds;

    private boolean voting_started;

    private boolean room_closed;

    private Long ownerId;

    @ElementCollection
    @CollectionTable(
            name = "room_participants",
            joinColumns = @JoinColumn(name = "room_id")
    )
    @MapKeyColumn(name = "user_id")
    @Column(name = "username")
    private Map<Long, String> participants;

    @Transient
    private int anonymousCount;

    @Transient
    private int currentCategoryIndex = 0;

    public Map.Entry<Long, String> addParticipant(Long id, String username){
        if(participants == null){
            participants = new HashMap<>();
            anonymousCount = 0;
        }
        anonymousCount++;

        Long temp = id == null ? (long)-anonymousCount : id;

        participants.put(temp, username);

        return Map.entry(temp, username);
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

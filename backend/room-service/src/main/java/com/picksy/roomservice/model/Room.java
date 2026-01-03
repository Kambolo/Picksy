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

  @Column(unique = true, name = "room_code")
  private String roomCode;

  private String name;

  @ElementCollection
  @CollectionTable(name = "room_set_categories", joinColumns = @JoinColumn(name = "room_id"))
  @AttributeOverrides({
    @AttributeOverride(name = "setId", column = @Column(name = "set_id_col")),
    @AttributeOverride(name = "categoryId", column = @Column(name = "category_id_col"))
  })
  private List<SetCategoryKey> categorySet;

  @Column(name = "voting_started")
  private boolean votingStarted;

  @Column(name = "room_closed")
  private boolean roomClosed;

  @Column(name = "owner_id")
  private Long ownerId;

  @ElementCollection
  @CollectionTable(name = "room_participants", joinColumns = @JoinColumn(name = "room_id"))
  @MapKeyColumn(name = "user_id")
  @Column(name = "username")
  private Map<Long, String> participants;

  @Column(name = "current_category_idx")
  private int currentCategoryIndex = 0;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  public Map.Entry<Long, String> addParticipant(Long id, String username) {
    if (participants == null) {
      participants = new HashMap<>();
    }

    participants.put(id, username);

    return Map.entry(id, username);
  }

    public void addSetCategory(Long setId, Long categoryId){
        if(categorySet == null){
            categorySet = new ArrayList<>();
        }
        categorySet.add(new SetCategoryKey(setId, categoryId));
    }

  public void removeParticipant(Long id) {
    participants.remove(id);
  }

  public int getParticipantCount() {
    return participants != null ? participants.size() : 0;
  }
}

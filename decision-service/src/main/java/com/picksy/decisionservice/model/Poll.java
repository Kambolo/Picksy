package com.picksy.decisionservice.model;

import com.picksy.decisionservice.util.CategoryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"roomCode", "categoryId"})
})
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long categoryId;

    private String roomCode;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Choice> choices;

    private int participantsCount;

    private CategoryType categoryType;

    // for counting users that already voted
    private int votedCount;

    public void setChoices(List<Choice> choices){
        this.choices = choices;
        if (choices != null) {
            for (Choice choice : choices){
                choice.setPoll(this);
            }
        }
    }

}






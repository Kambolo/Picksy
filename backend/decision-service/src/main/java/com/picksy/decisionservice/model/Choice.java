package com.picksy.decisionservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Choice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long optionId;

    // number of votes
    private int count;

    @ManyToOne
    @JoinColumn(name="poll_id")
    @ToString.Exclude
    private Poll poll;
}


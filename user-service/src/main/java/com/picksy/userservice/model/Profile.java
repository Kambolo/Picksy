package com.picksy.userservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {
    @Id
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "avatar_url")
    private String avatarUrl;

    private String bio;
}

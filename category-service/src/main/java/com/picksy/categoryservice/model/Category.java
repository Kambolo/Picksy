package com.picksy.categoryservice.model;

import com.picksy.categoryservice.util.enums.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Type type;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options;

    @Column(name="author_id")
    private Long authorID;

    @Column(name = "photo_url")
    private String photoUrl;

    private String description;

    private int views;

    private LocalDateTime created;

    private Boolean isPublic;

    public void add(Option option){
        if(options == null) this.options = new ArrayList<>();

        options.add(option);
        option.setCategory(this);
    }

    public void remove(Option option){
        options.remove(option);
        option.setCategory(null);
    }
}

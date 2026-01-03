package com.picksy.categoryservice.model;

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
public class CategorySet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "author_id")
    private Long authorID;

    private String name;


    @OneToMany(mappedBy = "categorySet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    private Boolean isPublic;
    private int views;
    private LocalDateTime created;

    public void add(Category category) {
        categories.add(category);
        category.setCategorySet(this);
    }

    public void remove(Category category) {
        categories.remove(category);
        category.setCategorySet(null);
    }
}
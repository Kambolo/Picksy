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
public class CategorySet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long authorID;

    private String name;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "categoryset_category",
            joinColumns = @JoinColumn(name = "category_set_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories = new ArrayList<>();

    private Boolean isPublic;
    private int views;
    private LocalDateTime created;

    public void add(Category category){
        if(this.categories == null) this.categories = new ArrayList<>();

        this.categories.add(category);
    }

    public void remove(Category category){
        if(this.categories == null) return;

        this.categories.remove(category);
    }
}

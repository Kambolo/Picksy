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

  // -1 predefined (built-in)
  // -2 part of set
  @Column(name = "author_id")
  private Long authorID;

  private String photoUrl;

  private String description;

  private int views;

  private LocalDateTime created;

  private Boolean isPublic;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_set_id")
  private CategorySet categorySet;

  public void add(Option option) {
    if (options == null) this.options = new ArrayList<>();

    options.add(option);
    option.setCategory(this);
  }

  public void remove(Option option) {
    if (options == null) return;

    options.remove(option);
    option.setCategory(null);
  }

}

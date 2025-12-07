package com.picksy.roomservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetCategoryKey implements Serializable {

  @Column(name = "set_id")
  private Long setId;

  @Column(name = "category_id")
  private Long categoryId;
}

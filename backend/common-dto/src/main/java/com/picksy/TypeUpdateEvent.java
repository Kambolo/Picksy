package com.picksy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeUpdateEvent {
    private Long categoryId;
    private String newType;
}

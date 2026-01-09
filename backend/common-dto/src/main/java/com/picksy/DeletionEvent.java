package com.picksy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeletionEvent {
    private Long id;
    private String type; // "CATEGORY" lub "SET"
}

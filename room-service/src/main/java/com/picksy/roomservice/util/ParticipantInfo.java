package com.picksy.roomservice.util;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipantInfo {
    private Long userId;
    private String username;
}

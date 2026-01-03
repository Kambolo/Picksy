package com.picksy.roomservice.message;


import com.picksy.roomservice.model.SetCategoryKey;
import com.picksy.roomservice.util.MessageType;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomMessage {
    private MessageType type;
    private Long userId;
    private String username;
    private SetCategoryKey category;
}

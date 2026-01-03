package com.picksy.authservice.response;

import com.picksy.authservice.Util.ROLE;

public record UserDTO(
                        Long id,
                        String username,
                        String email,
                        ROLE role,
                        Boolean isBlocked
){
}

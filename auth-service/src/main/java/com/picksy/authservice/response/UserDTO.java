package com.picksy.authservice.response;

public record UserDTO(
                        Long id,
                        String username,
                        String email
){
}

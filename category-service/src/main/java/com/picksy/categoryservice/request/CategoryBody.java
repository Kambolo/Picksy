package com.picksy.categoryservice.request;

public record CategoryBody(
                        String name,
                        String type,
                        String description,
                        Boolean isPublic){
}

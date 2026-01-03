package com.picksy.categoryservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Movie(
    String title,
    @JsonProperty("poster_path") String poster,
    @JsonProperty("release_date") String releaseDate) {}

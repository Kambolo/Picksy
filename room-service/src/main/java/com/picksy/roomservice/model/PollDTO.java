package com.picksy.roomservice.model;

import java.util.List;

public record PollDTO(Long pollId, Long categoryId, List<ChoiceDTO> choices) {}

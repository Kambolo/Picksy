package com.picksy.decisionservice.model;

import java.util.List;

public record PollDTO(Long pollId, Long categoryId, List<ChoiceDTO> choices) {}

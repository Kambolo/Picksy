package com.picksy.roomservice.model;

import java.util.List;

public record PollDTO(Long pollId, SetCategoryKey category, List<ChoiceDTO> choices, int participantsCount) {}

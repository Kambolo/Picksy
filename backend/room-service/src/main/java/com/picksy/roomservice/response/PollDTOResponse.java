package com.picksy.roomservice.response;

import com.picksy.roomservice.model.ChoiceDTO;
import com.picksy.roomservice.model.SetCategoryKey;

import java.util.List;

public record PollDTOResponse(Long pollId, SetCategoryKey category, List<ChoiceDTO> choices, int participantsCount) {}

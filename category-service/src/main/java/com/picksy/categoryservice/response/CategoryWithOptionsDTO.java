package com.picksy.categoryservice.response;

import java.util.List;

public record CategoryWithOptionsDTO(CategoryDTO categoryDTO, List<OptionDTO> optionDTOs) {}

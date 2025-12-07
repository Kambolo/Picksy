package com.picksy.categoryservice.request;

import java.util.List;

public record CreateCategorySetBody(String name, List<Long> categoryIds, Boolean isPublic) {}

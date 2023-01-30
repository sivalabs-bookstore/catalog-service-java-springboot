package com.sivalabs.bookstore.catalog.domain;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateProductModel(
        String code,
        @NotNull String name,
        String description,
        String imageUrl,
        @NotNull BigDecimal price) {}

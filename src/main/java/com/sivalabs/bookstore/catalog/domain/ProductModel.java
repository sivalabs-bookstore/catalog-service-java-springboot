package com.sivalabs.bookstore.catalog.domain;

import java.math.BigDecimal;

public record ProductModel(
        String id,
        String code,
        String name,
        String description,
        String imageUrl,
        BigDecimal price,
        BigDecimal discount,
        BigDecimal salePrice) {}

package com.sivalabs.bookstore.catalog.domain;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ProductModel {
    private String id;
    private String code;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal discount;
    private BigDecimal salePrice;
}

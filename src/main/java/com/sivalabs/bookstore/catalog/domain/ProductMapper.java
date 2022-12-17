package com.sivalabs.bookstore.catalog.domain;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductModel toModel(Product product) {
        var discount = BigDecimal.ZERO;
        if (product.getDiscount() != null) {
            discount = product.getDiscount();
        }
        return new ProductModel(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getDescription(),
                product.getImageUrl(),
                product.getPrice(),
                discount,
                product.getPrice().subtract(discount));
    }
}

package com.sivalabs.bookstore.catalog.domain;

import com.sivalabs.bookstore.catalog.clients.promotions.Promotion;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductModel toModel(Product product, Promotion promotion) {
        var discount = BigDecimal.ZERO;
        if (promotion != null && product.getCode().equals(promotion.getCode())) {
            discount = promotion.getDiscount();
        }
        return ProductModel.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .discount(discount)
                .salePrice(product.getPrice().subtract(discount))
                .build();
    }
}

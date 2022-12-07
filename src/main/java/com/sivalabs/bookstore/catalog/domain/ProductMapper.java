package com.sivalabs.bookstore.catalog.domain;

import com.sivalabs.bookstore.catalog.clients.promotions.Promotion;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductMapper {

    public ProductModel toModel(Product product, Promotion promotion) {
        ProductModel pm = new ProductModel();
        pm.setId(product.getId());
        pm.setIsbn(product.getIsbn());
        pm.setName(product.getName());
        pm.setDescription(product.getDescription());
        pm.setPrice(product.getPrice());
        var discount = BigDecimal.ZERO;
        if(promotion != null && product.getIsbn().equals(promotion.getIsbn())) {
            discount = promotion.getDiscount();
        }
        pm.setDiscount(discount);
        pm.setSalePrice(product.getPrice().subtract(discount));
        return pm;
    }
}

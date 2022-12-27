package com.sivalabs.bookstore.catalog.domain;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {
    @Id private String id;

    @Indexed(unique = true)
    @NotEmpty(message = "Product code must not be null/empty")
    private String code;

    @NotEmpty(message = "Product name must not be null/empty")
    @TextIndexed
    private String name;

    @TextIndexed
    private String description;

    private String imageUrl;

    @NotNull(message = "Product price must not be null")
    @DecimalMin("0.1")
    private BigDecimal price;

    private BigDecimal discount;

    public Product() {}

    public Product(
            String id,
            String code,
            String name,
            String description,
            String imageUrl,
            BigDecimal price,
            BigDecimal discount) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.discount = discount;
    }

    public String getId() {
        return this.id;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public BigDecimal getDiscount() {
        return this.discount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
}

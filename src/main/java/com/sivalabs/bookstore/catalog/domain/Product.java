package com.sivalabs.bookstore.catalog.domain;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {
    @Id private String id;

    @Indexed(unique = true)
    @NotEmpty(message = "Product code must not be null/empty")
    private String code;

    @NotEmpty(message = "Product name must not be null/empty")
    private String name;

    private String description;

    private String imageUrl;

    @NotNull(message = "Product price must not be null")
    @DecimalMin("0.1")
    private BigDecimal price;
}

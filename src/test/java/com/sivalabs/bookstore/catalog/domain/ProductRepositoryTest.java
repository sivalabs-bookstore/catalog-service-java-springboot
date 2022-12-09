package com.sivalabs.bookstore.catalog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataMongoTest
@Testcontainers
class ProductRepositoryTest {
    @Autowired private ProductRepository productRepository;

    @Container static final MongoDBContainer mongodb = new MongoDBContainer("mongo:4.2");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongodb::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        productRepository.save(
                new Product(null, "P100", "Product 1", "Product 1 desc", null, BigDecimal.TEN));
        productRepository.save(
                new Product(
                        null, "P101", "Product 2", "Product 2 desc", null, BigDecimal.valueOf(24)));
    }

    @Test
    void shouldGetAllProducts() {
        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(2);
    }

    @Test
    void shouldFailToProductWithDuplicateCode() {
        var product =
                new Product(null, "P100", "Product name", "Product desc", null, BigDecimal.TEN);
        assertThrows(DuplicateKeyException.class, () -> productRepository.save(product));
    }

    @Test
    void shouldGetProductByCode() {
        Optional<Product> optionalProduct = productRepository.findByCode("P100");
        assertThat(optionalProduct).isNotEmpty();
        assertThat(optionalProduct.get().getCode()).isEqualTo("P100");
        assertThat(optionalProduct.get().getName()).isEqualTo("Product 1");
        assertThat(optionalProduct.get().getDescription()).isEqualTo("Product 1 desc");
        assertThat(optionalProduct.get().getPrice()).isEqualTo(BigDecimal.TEN);
    }
}

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.TextCriteria;
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
                new Product(
                        null,
                        "P100",
                        "Product 1",
                        "Product 1 desc",
                        null,
                        BigDecimal.TEN,
                        BigDecimal.valueOf(2.5)));
        productRepository.save(
                new Product(
                        null,
                        "P101",
                        "Product 2",
                        "Product 2 desc",
                        null,
                        BigDecimal.valueOf(24),
                        BigDecimal.ZERO));
    }

    @Test
    void shouldGetAllProducts() {
        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(2);
    }

    @Test
    void shouldFailToSaveProductWithDuplicateCode() {
        var product =
                new Product(
                        null,
                        "P100",
                        "Product name",
                        "Product desc",
                        null,
                        BigDecimal.TEN,
                        BigDecimal.ZERO);
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
        assertThat(optionalProduct.get().getDiscount()).isEqualTo(BigDecimal.valueOf(2.5));
    }

    @Test
    void shouldSearchProductByName() {
        Page<Product> actualResult =
                productRepository.findAllBy(
                        new TextCriteria().matchingPhrase("Product 1"), PageRequest.of(0, 10));

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getTotalElements()).isEqualTo(1);
        assertThat(actualResult.getContent().get(0).getCode()).isEqualTo("P100");
    }

    @Test
    void shouldReturnNoResultForNoMatchInSearch() {
        Page<Product> actualResult =
                productRepository.findAllBy(
                        new TextCriteria().matchingPhrase("catalog"), PageRequest.of(0, 2));

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getTotalElements()).isEqualTo(0);
        assertThat(actualResult.getTotalPages()).isEqualTo(0);
    }

    @Test
    void shouldSearchProductByDescription() {
        Page<Product> actualResult =
                productRepository.findAllBy(
                        new TextCriteria().matchingPhrase("desc"), PageRequest.of(0, 10));

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getTotalElements()).isEqualTo(2);
        assertThat(
                        actualResult
                                .get()
                                .filter(product -> product.getDescription().contains("desc"))
                                .count())
                .isEqualTo(2);
    }
}

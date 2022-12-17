package com.sivalabs.bookstore.catalog.common;

import com.sivalabs.bookstore.catalog.domain.Product;
import com.sivalabs.bookstore.catalog.domain.ProductRepository;
import io.restassured.RestAssured;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.lifecycle.Startables;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    protected static final MongoDBContainer mongodb = new MongoDBContainer("mongo:4.2");

    @LocalServerPort private Integer port;

    @Autowired private ProductRepository productRepository;

    @BeforeAll
    static void beforeAll() {
        Startables.deepStart(mongodb).join();
    }

    protected final List<Product> products =
            List.of(
                    new Product(
                            null,
                            "P100",
                            "Product 1",
                            "Product 1 desc",
                            null,
                            BigDecimal.TEN,
                            BigDecimal.valueOf(2.5)),
                    new Product(
                            null,
                            "P101",
                            "Product 2",
                            "Product 2 desc",
                            null,
                            BigDecimal.valueOf(24),
                            BigDecimal.ZERO));

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        productRepository.deleteAll();
        productRepository.saveAll(products);
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongodb::getReplicaSetUrl);
    }
}

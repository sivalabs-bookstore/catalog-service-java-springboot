package com.sivalabs.bookstore.catalog.common;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

import com.sivalabs.bookstore.catalog.domain.Product;
import com.sivalabs.bookstore.catalog.domain.ProductRepository;
import io.restassured.RestAssured;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    protected static final MongoDBContainer mongodb = new MongoDBContainer("mongo:4.2");
    protected static final MockServerContainer mockServer =
            new MockServerContainer(
                    DockerImageName.parse("jamesdbloom/mockserver:mockserver-5.13.2"));

    protected static MockServerClient mockServerClient;

    @LocalServerPort private Integer port;

    @Autowired private ProductRepository productRepository;

    @BeforeAll
    static void beforeAll() {
        Startables.deepStart(mongodb, mockServer).join();
    }

    protected final List<Product> products =
            List.of(
                    new Product(null, "P100", "Product 1", "Product 1 desc", null, BigDecimal.TEN),
                    new Product(
                            null,
                            "P101",
                            "Product 2",
                            "Product 2 desc",
                            null,
                            BigDecimal.valueOf(24)));

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        mockServerClient.reset();
        productRepository.deleteAll();
        productRepository.saveAll(products);
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongodb::getReplicaSetUrl);
        registry.add("app.promotion-service-url", mockServer::getEndpoint);
        mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    }

    protected static void mockGetPromotions() {
        mockServerClient
                .when(request().withMethod("GET").withPath("/api/promotions?isbns=.*"))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(
                                        new Header(
                                                "Content-Type", "application/json; charset=utf-8"))
                                .withBody(
                                        json(
                                                """
                                                [
                                                    {
                                                        "code": "P100",
                                                        "discount": 2.5
                                                    },
                                                    {
                                                        "code": "P101",
                                                        "discount": 1.5
                                                    }
                                                ]
                                                """)));
    }

    protected static void mockGetPromotion(String isbn, BigDecimal discount) {
        mockServerClient
                .when(request().withMethod("GET").withPath("/api/promotions/" + isbn))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(
                                        new Header(
                                                "Content-Type", "application/json; charset=utf-8"))
                                .withBody(
                                        json(
                                                """
                                                    {
                                                        "code": "%s",
                                                        "discount": %f
                                                    }
                                                """
                                                        .formatted(isbn, discount.doubleValue()))));
    }
}

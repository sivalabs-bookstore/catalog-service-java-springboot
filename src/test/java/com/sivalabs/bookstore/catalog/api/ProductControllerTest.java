package com.sivalabs.bookstore.catalog.api;

import com.sivalabs.bookstore.catalog.common.AbstractIntegrationTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

class ProductControllerTest extends AbstractIntegrationTest {

    @Test
    void shouldGetAllProducts() {
        mockGetPromotions();

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/products")
                .then()
                .statusCode(200)
                .body("data", hasSize(products.size()))
                .body("totalElements", is(products.size()))
                .body("pageNumber", is(1))
                .body("totalPages", is(1))
                .body("isFirst", is(true))
                .body("isLast", is(true))
                .body("hasNext", is(false))
                .body("hasPrevious", is(false));
    }

    @Test
    void shouldGetProductByCode() {
        mockGetPromotion("P100", new BigDecimal("2.5"));
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/products/{isbn}", "P100")
                .then()
                .statusCode(200)
                .body("isbn", is("P100"))
                .body("name", is("Product 1"))
                .body("description", is("Product 1 desc"))
                .body("price", equalTo(10))
                .body("discount", is(2.5f))
                .body("salePrice", is(7.5f))
        ;
    }

    @Test
    void shouldReturnNotFoundWhenProductCodeNotExists() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/products/{code}", "invalid_product_code")
                .then()
                .statusCode(404);
    }
}
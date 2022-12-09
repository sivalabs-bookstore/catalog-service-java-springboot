package com.sivalabs.bookstore.catalog.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.sivalabs.bookstore.catalog.common.AbstractIntegrationTest;
import io.restassured.http.ContentType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductControllerTest extends AbstractIntegrationTest {

    @Test
    void shouldGetAllProducts() {
        mockGetPromotions();

        given().contentType(ContentType.JSON)
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
    void shouldGetProductByCodeWithPromotion() {
        mockGetPromotion("P100", new BigDecimal("2.5"));
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/products/{code}", "P100")
                .then()
                .statusCode(200)
                .body("code", is("P100"))
                .body("name", is("Product 1"))
                .body("description", is("Product 1 desc"))
                .body("price", equalTo(10))
                .body("discount", is(2.5f))
                .body("salePrice", is(7.5f));
    }

    @Test
    void shouldGetProductByCodeWithoutPromotion() {
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/products/{code}", "P100")
                .then()
                .statusCode(200)
                .body("code", is("P100"))
                .body("name", is("Product 1"))
                .body("description", is("Product 1 desc"))
                .body("price", equalTo(10))
                .body("discount", is(0))
                .body("salePrice", is(10));
    }

    @Test
    void shouldReturnNotFoundWhenProductCodeNotExists() {
        String code = "invalid_product_code";
        given().contentType(ContentType.JSON)
                .when()
                .get("/api/products/{code}", code)
                .then()
                .statusCode(404)
                .body("status", is(404))
                .body("title", is("Product Not Found"))
                .body("detail", is("Product with code: " + code + " not found"));
    }
}

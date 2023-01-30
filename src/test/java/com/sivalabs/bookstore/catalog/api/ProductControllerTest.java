package com.sivalabs.bookstore.catalog.api;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.sivalabs.bookstore.catalog.common.AbstractIntegrationTest;
import com.sivalabs.bookstore.catalog.common.TestHelper;
import com.sivalabs.bookstore.catalog.domain.CreateProductModel;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import java.math.BigDecimal;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class ProductControllerTest extends AbstractIntegrationTest {

    @Test
    void shouldGetAllProducts() {
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
    void shouldGetProductByCode() {
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

    @Test
    void shouldReturnNoResult_WhenNoSearchMatchExists() {
        given().contentType(ContentType.JSON)
                .when()
                .get(TestHelper.buildSearchEndpoint("gibberish"))
                .then()
                .statusCode(200)
                .body("data", hasSize(0))
                .body("totalElements", is(0))
                .body("pageNumber", is(1))
                .body("totalPages", is(0))
                .body("isFirst", is(true))
                .body("isLast", is(true))
                .body("hasNext", is(false))
                .body("hasPrevious", is(false));
    }

    @Test
    void shouldReturnResult_WhenSearchMatchExists() {
        given().contentType(ContentType.JSON)
                .when()
                .get(TestHelper.buildSearchEndpoint("Product 2"))
                .then()
                .statusCode(200)
                .body("data", hasSize(1))
                .body("totalElements", is(1))
                .body("pageNumber", is(1))
                .body("totalPages", is(1))
                .body("isFirst", is(true))
                .body("isLast", is(true))
                .body("hasNext", is(false))
                .body("hasPrevious", is(false));
    }

    @Test
    void shouldCreateNewProduct_WhenValidDetailsProvided() {
        CreateProductModel createProductModel =
                new CreateProductModel(
                        "P200",
                        "A Dog with A Ball",
                        "Awesome read about a dog with super ball",
                        null,
                        new BigDecimal(10).stripTrailingZeros());

        RestAssured.config =
                RestAssured.config()
                        .jsonConfig(
                                jsonConfig()
                                        .numberReturnType(
                                                JsonPathConfig.NumberReturnType.BIG_DECIMAL));

        given().contentType(ContentType.JSON)
                .body(createProductModel)
                .when()
                .post(TestHelper.CREATE_PRODUCT_ENDPOINT)
                .then()
                .statusCode(201)
                .body("$", Matchers.hasKey("id"))
                .body("code", is(createProductModel.code()))
                .body("name", is(createProductModel.name()))
                .body("description", is(createProductModel.description()))
                .body("imageUrl", is(createProductModel.imageUrl()))
                .body("price", is(createProductModel.price()));
    }

    @Test
    void shouldReturnValidationError_WhenInvalidDetailsProvided() {
        CreateProductModel missingName =
                new CreateProductModel(
                        "P200",
                        null,
                        "Awesome read about a dog with super ball",
                        null,
                        BigDecimal.TEN);

        given().contentType(ContentType.JSON)
                .body(missingName)
                .when()
                .post(TestHelper.CREATE_PRODUCT_ENDPOINT)
                .then()
                .statusCode(400)
                .body("status", is(400))
                .body("title", is("Invalid Request"))
                .body("errorCategory", is("Validation"))
                .body("detail", is("'name' must not be null"));
    }

    @Test
    void shouldReturnProductExistsError_WhenDuplicateProductCodeSupplied() {
        CreateProductModel duplicateProduct =
                new CreateProductModel("P100", "Product 1", "Product 1 desc", null, BigDecimal.TEN);

        given().contentType(ContentType.JSON)
                .body(duplicateProduct)
                .when()
                .post(TestHelper.CREATE_PRODUCT_ENDPOINT)
                .then()
                .log()
                .all()
                .statusCode(400)
                .body("status", is(400))
                .body("title", is("Product already exist"))
                .body(
                        "detail",
                        is("Product with code '" + duplicateProduct.code() + "' already exist"));
    }
}

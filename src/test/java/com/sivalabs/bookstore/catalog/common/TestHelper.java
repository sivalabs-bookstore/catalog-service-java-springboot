package com.sivalabs.bookstore.catalog.common;

import com.sivalabs.bookstore.catalog.domain.Product;
import com.sivalabs.bookstore.catalog.domain.ProductMapper;
import com.sivalabs.bookstore.catalog.domain.ProductModel;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class TestHelper {

    private static final String SEARCH_ENDPOINT = "/api/products/search?query=%s";

    public static String buildSearchEndpoint(String queryString) {
        return String.format(SEARCH_ENDPOINT, queryString);
    }

    public static Page<ProductModel> emptyPageOfProductModel() {
        return Page.empty(PageRequest.of(0, 2));
    }

    public static Page<Product> emptyPageOfProduct() {
        return Page.empty(PageRequest.of(0, 2));
    }

    public static Page<Product> pageOfSortedProducts() {
        return new PageImpl<>(stubbedProducts());
    }

    public static Page<ProductModel> pageOfSortedProductModels() {
        ProductMapper productMapper = new ProductMapper();
        return pageOfSortedProducts().map(productMapper::toModel);
    }

    public static List<Product> sortableProducts() {
        return stubbedProducts();
    }

    private static List<Product> stubbedProducts() {
        return List.of(
                new Product(
                        null,
                        "P200",
                        "A Dog with A Ball",
                        "Awesome read about a dog with super ball",
                        null,
                        BigDecimal.TEN,
                        BigDecimal.valueOf(0.1)),
                new Product(
                        null,
                        "P201",
                        "Dog",
                        "Awesome read about a dog with super powers",
                        null,
                        BigDecimal.TEN,
                        BigDecimal.valueOf(0.5)));
    }
}

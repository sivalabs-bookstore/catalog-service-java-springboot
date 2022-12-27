package com.sivalabs.bookstore.catalog.common;

import com.sivalabs.bookstore.catalog.domain.Product;
import com.sivalabs.bookstore.catalog.domain.ProductModel;
import org.springframework.data.domain.Page;
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
}

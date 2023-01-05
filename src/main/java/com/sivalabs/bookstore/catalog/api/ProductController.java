package com.sivalabs.bookstore.catalog.api;

import com.sivalabs.bookstore.catalog.domain.PagedResult;
import com.sivalabs.bookstore.catalog.domain.ProductModel;
import com.sivalabs.bookstore.catalog.domain.ProductNotFoundException;
import com.sivalabs.bookstore.catalog.domain.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public PagedResult<ProductModel> getProducts(
            @RequestParam(name = "page", defaultValue = "1") int pageNo) {
        return productService.getProducts(pageNo);
    }

    @GetMapping("/{code}")
    public ResponseEntity<ProductModel> getProductByCode(@PathVariable String code) {
        return productService
                .getProductByCode(code)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ProductNotFoundException(code));
    }

    @DeleteMapping("/{code}")
    public void deleteProduct(@PathVariable String code) {
        productService.deleteProduct(code).orElseThrow(() -> new ProductNotFoundException(code));
    }
}

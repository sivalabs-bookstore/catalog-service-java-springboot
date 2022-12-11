package com.sivalabs.bookstore.catalog.api;

import com.sivalabs.bookstore.catalog.domain.PagedResult;
import com.sivalabs.bookstore.catalog.domain.ProductModel;
import com.sivalabs.bookstore.catalog.domain.ProductNotFoundException;
import com.sivalabs.bookstore.catalog.domain.ProductService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

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

    @GetMapping("/search")
    @ResponseStatus(code = HttpStatus.OK)
    public void searchProducts(@RequestParam(name = "query")String query) {
        //do nothing for now
        //TODO: do seach
    }
}

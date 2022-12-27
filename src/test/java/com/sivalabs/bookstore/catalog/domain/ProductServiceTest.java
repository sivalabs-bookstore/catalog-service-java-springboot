package com.sivalabs.bookstore.catalog.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;

public class ProductServiceTest {

    private ProductService productService;
    private ProductRepository productRepository;
    private ProductMapper productMapper;

    @BeforeEach
    public void setup() {
        this.productMapper = mock(ProductMapper.class);
        this.productRepository = mock(ProductRepository.class);

        this.productService = new ProductService(productRepository, productMapper);
    }

    /**
     * tests the search result list is empty when there are no query matches
     */
    @Test
    public void shouldReturnEmptyListForWhenNoResults(){
        when(productRepository.findAllBy(any(TextCriteria.class), any(Pageable.class))).thenReturn(Page.empty());
        Page<Product> actualPage = productService.searchProductsByCriteria("search text",1);

        assertNotNull(actualPage, "Search result is null");
        assertEquals(Page.empty(), actualPage);

        verify(productRepository,times(1)).findAllBy(any(TextCriteria.class), any(Pageable.class));
    }
}

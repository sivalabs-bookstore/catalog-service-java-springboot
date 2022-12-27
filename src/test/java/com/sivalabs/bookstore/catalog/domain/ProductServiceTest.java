package com.sivalabs.bookstore.catalog.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sivalabs.bookstore.catalog.common.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    /** tests the search result list is empty when there are no query matches */
    @Test
    public void shouldReturnEmptyListForWhenNoResults() {
        when(productRepository.findAllBy(any(TextCriteria.class), any(Pageable.class)))
                .thenReturn(TestHelper.emptyPageOfProduct());
        PagedResult<ProductModel> actualPage =
                productService.searchProductsByCriteria("search text", 1);

        assertNotNull(actualPage, "Search result is null");
        assertTrue(actualPage.getData().isEmpty());
        assertTrue(actualPage.isFirst());
        assertFalse(actualPage.isHasNext());
        assertEquals(1, actualPage.getPageNumber());
        // TODO: update the assertion
        // since pagenumber is auto incremented by 1, this results in descrepancy between total
        // pages and current pages number
        // e.g. if no results, then pagenumber = 1, but, totalPages is 0
        assertEquals(0, actualPage.getTotalPages());
        assertEquals(0, actualPage.getTotalElements());

        verify(productRepository, times(1)).findAllBy(any(TextCriteria.class), any(Pageable.class));
    }
}

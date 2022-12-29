package com.sivalabs.bookstore.catalog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sivalabs.bookstore.catalog.common.TestHelper;
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

    @Test
    public void shouldReturnEmptyListForWhenNoResults() {
        when(productRepository.findAllBy(any(TextCriteria.class), any(Pageable.class)))
                .thenReturn(TestHelper.emptyPageOfProduct());
        PagedResult<ProductModel> actualPage =
                productService.searchProductsByCriteria("search text", 1);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getData().isEmpty()).isTrue();
        assertThat(actualPage.isFirst()).isTrue();
        assertThat(actualPage.isHasNext()).isFalse();
        assertThat(actualPage.getPageNumber()).isEqualTo(1);
        // TODO: update the assertion
        // since pagenumber is auto incremented by 1, this results in descrepancy between total
        // pages and current pages number
        // e.g. if no results, then pagenumber = 1, but, totalPages is 0
        assertThat(actualPage.getTotalPages()).isZero();
        assertThat(actualPage.getTotalElements()).isZero();

        verify(productRepository, times(1)).findAllBy(any(TextCriteria.class), any(Pageable.class));
    }

    @Test
    public void shouldReturnSortedListForMatchedSearches() {
        Page<ProductModel> expectedProductModels = TestHelper.pageOfSortedProductModels();
        when(productRepository.findAllBy(any(TextCriteria.class), any(Pageable.class)))
                .thenReturn(TestHelper.pageOfSortedProducts());

        when(productMapper.toModel(any(Product.class)))
                .thenReturn(
                        expectedProductModels.getContent().get(0),
                        expectedProductModels.getContent().get(1));

        PagedResult<ProductModel> actualPage = productService.searchProductsByCriteria("Dog", 1);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getData().isEmpty()).isFalse();
        assertThat(actualPage.isFirst()).isTrue();
        assertThat(actualPage.isHasNext()).isFalse();
        assertThat(actualPage.getPageNumber()).isEqualTo(1);
        assertThat(actualPage.getTotalPages()).isOne();
        assertThat(actualPage.getTotalElements()).isEqualTo(2);
        assertThat(actualPage.getData().get(0).code()).isEqualTo("P200");
        assertThat(actualPage.getData().get(1).code()).isEqualTo("P201");

        verify(productRepository, times(1)).findAllBy(any(TextCriteria.class), any(Pageable.class));
        verify(productMapper, times(2)).toModel(any(Product.class));
    }
}

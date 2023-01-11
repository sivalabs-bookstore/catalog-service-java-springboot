package com.sivalabs.bookstore.catalog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sivalabs.bookstore.catalog.common.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;

public class ProductServiceTest {

    @InjectMocks private ProductService productService;

    @Mock private ProductRepository productRepository;

    @Mock private ProductMapper productMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldReturnEmptyListForWhenNoResults() {
        when(productRepository.findAllBy(any(TextCriteria.class), any(Pageable.class)))
                .thenReturn(TestHelper.emptyPageOfProduct());
        PagedResult<ProductModel> actualPage =
                productService.searchProductsByCriteria("search text", 1);

        assertThat(actualPage).isNotNull();
        assertThat(actualPage.data().isEmpty()).isTrue();
        assertThat(actualPage.isFirst()).isTrue();
        assertThat(actualPage.hasNext()).isFalse();
        assertThat(actualPage.pageNumber()).isEqualTo(1);
        // TODO: update the assertion
        // since pagenumber is auto incremented by 1, this results in descrepancy between total
        // pages and current pages number
        // e.g. if no results, then pagenumber = 1, but, totalPages is 0
        assertThat(actualPage.totalPages()).isZero();
        assertThat(actualPage.totalElements()).isZero();

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
        assertThat(actualPage.data().isEmpty()).isFalse();
        assertThat(actualPage.isFirst()).isTrue();
        assertThat(actualPage.hasNext()).isFalse();
        assertThat(actualPage.pageNumber()).isEqualTo(1);
        assertThat(actualPage.totalPages()).isOne();
        assertThat(actualPage.totalElements()).isEqualTo(2);
        assertThat(actualPage.data().get(0).code()).isEqualTo("P200");
        assertThat(actualPage.data().get(1).code()).isEqualTo("P201");

        verify(productRepository, times(1)).findAllBy(any(TextCriteria.class), any(Pageable.class));
        verify(productMapper, times(2)).toModel(any(Product.class));
    }
}

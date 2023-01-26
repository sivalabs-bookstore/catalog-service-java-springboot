package com.sivalabs.bookstore.catalog.api;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sivalabs.bookstore.catalog.common.TestHelper;
import com.sivalabs.bookstore.catalog.domain.PagedResult;
import com.sivalabs.bookstore.catalog.domain.ProductNotFoundException;
import com.sivalabs.bookstore.catalog.domain.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(controllers = {ProductController.class})
public class ProductControllerUnitTest {

    private static final String DATA = "$.data";
    private static final String PAGE_NUMBER = "$.pageNumber";
    private static final String TOTAL_ELEMENTS = "$.totalElements";
    private static final String TOTAL_PAGES = "$.totalPages";

    @Autowired MockMvc mockMvc;

    @MockBean private ProductService productService;

    @Test
    public void shouldReturnStatusOKForValidCatalogSearches() throws Exception {
        String endpoint = TestHelper.buildSearchEndpoint("abc");
        mockServiceToReturnEmptyPage();
        mockMvc.perform(get(endpoint)).andExpect(status().isOk());
        verifyMockCallForSearchProducts(1);
    }

    @Test
    public void shouldReturnEmptyListForValidSearchWithNoResult() throws Exception {
        String endpoint = TestHelper.buildSearchEndpoint("gibberish");
        mockServiceToReturnEmptyPage();
        mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TOTAL_ELEMENTS).value(0))
                .andExpect(jsonPath(TOTAL_PAGES).value(0))
                .andExpect(jsonPath(PAGE_NUMBER).value(1))
                .andExpect(jsonPath(DATA).isArray())
                .andExpect(jsonPath(DATA).isEmpty());

        verifyMockCallForSearchProducts(1);
    }

    private void mockServiceToReturnEmptyPage() {
        when(productService.searchProductsByCriteria(anyString(), anyInt()))
                .thenReturn(PagedResult.fromPage(TestHelper.emptyPageOfProductModel()));
    }

    private void verifyMockCallForSearchProducts(int times) {
        verify(productService, times(times)).searchProductsByCriteria(anyString(), anyInt());
    }

    @Test
    public void testDeleteProduct_whenProductExists_shouldReturnDeletedProduct() throws Exception {
        String code = "P109";
        doNothing().when(productService).deleteProduct(code);

        mockMvc.perform(delete("/api/products/" + code).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteProduct_whenProductDoesNotExist_shouldThrowProductNotFoundException() throws Exception {
        String code = "P1090";
        doThrow(new ProductNotFoundException("Product with code:"+code+"not found")).when(productService).deleteProduct(code);

        mockMvc.perform(delete("/api/products/" + code).contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Product with code:"+code+"not found")));
    }

}

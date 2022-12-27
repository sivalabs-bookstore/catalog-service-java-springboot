package com.sivalabs.bookstore.catalog.api;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sivalabs.bookstore.catalog.common.TestHelper;
import com.sivalabs.bookstore.catalog.domain.PagedResult;
import com.sivalabs.bookstore.catalog.domain.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {ProductController.class})
public class ProductControllerUnitTest {

    private static final String DATA = "$.data";
    private static final String PAGE_NUMBER = "$.pageNumber";
    private static final String TOTAL_ELEMENTS = "$.totalElements";
    private static final String TOTAL_PAGES = "$.totalPages";

    @Autowired MockMvc mockMvc;

    @MockBean private ProductService productService;

    /** Tests that Http Status is {@code 200:OK} for a valid search request */
    @Test
    public void shouldReturnStatusOKForValidCatalogSearches() {
        String endpoint = TestHelper.buildSearchEndpoint("abc");
        mockServiceToReturnEmptyPage();
        try {
            mockMvc.perform(get(endpoint)).andExpect(status().isOk());
            verifyMockCallForSearchProducts(1);
        } catch (Exception e) {
            // fail the test case with a readable message incase of exception from mockMvc
            fail(
                    String.format(
                            "Unexpected exception during GET %s. Exception=[%s]",
                            endpoint, e.getMessage()));
        }
    }

    /*
     * Tests that HttpStatus is {@code 200:OK} and empty response for a valid search
     * with no results
     */
    @Test
    public void shouldReturnEmptyListForValidSearchWithNoResult() {
        String endpoint = TestHelper.buildSearchEndpoint("gibberish");
        mockServiceToReturnEmptyPage();
        try {
            mockMvc.perform(get(endpoint))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath(TOTAL_ELEMENTS).value(0))
                    .andExpect(jsonPath(TOTAL_PAGES).value(0))
                    .andExpect(jsonPath(PAGE_NUMBER).value(1))
                    .andExpect(jsonPath(DATA).isArray())
                    .andExpect(jsonPath(DATA).isEmpty());

            verifyMockCallForSearchProducts(1);
        } catch (Exception e) {
            // fail the test case with a readable message incase of exception from mockMvc
            fail(
                    String.format(
                            "Unexpected exception during GET %s. Exception=[%s]",
                            endpoint, e.getMessage()));
        }
    }

    private void mockServiceToReturnEmptyPage() {

        when(productService.searchProductsByCriteria(anyString(), anyInt()))
                .thenReturn(PagedResult.fromPage(TestHelper.emptyPageOfProductModel()));
    }

    private void verifyMockCallForSearchProducts(int times) {
        verify(productService, times(times)).searchProductsByCriteria(anyString(), anyInt());
    }
}

package com.sivalabs.bookstore.catalog.api;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.sivalabs.bookstore.catalog.domain.ProductService;

@WebMvcTest(controllers = { ProductController.class })
public class ProductControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    /**
     * Tests that Http Status is {@code 200:OK} for a valid search request
     */
    @Test
    public void shouldReturnStatusOKForValidCatalogSearches() {
        String endpoint = "/api/products/search?query=abc";
        try {
            mockMvc.perform(get(endpoint))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            // fail the test case with a readable message incase of exception from mockMvc
            fail(String.format("Unexpected exception during GET %s. Exception=[%s]", endpoint, e.getMessage()));
        }
    }
}

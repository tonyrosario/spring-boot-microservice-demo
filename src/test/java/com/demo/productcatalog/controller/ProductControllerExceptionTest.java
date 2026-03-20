package com.demo.productcatalog.controller;

import com.demo.productcatalog.domain.exception.DuplicateSkuException;
import com.demo.productcatalog.domain.exception.ProductNotFoundException;
import com.demo.productcatalog.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController — error responses")
class ProductControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private static final String VALID_BODY = """
            {
              "name": "Widget",
              "sku": "WGT-001",
              "priceAmount": 9.99,
              "priceCurrency": "USD"
            }
            """;

    @Nested
    @DisplayName("POST /api/products")
    class Create {

        @Test
        @DisplayName("returns 409 when SKU already exists")
        void should_return409_when_skuAlreadyExists() throws Exception {
            when(productService.create(any())).thenThrow(new DuplicateSkuException("WGT-001"));

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409));
        }

        @Test
        @DisplayName("returns 400 when name is missing")
        void should_return400_when_nameIsMissing() throws Exception {
            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "sku": "WGT-001",
                                      "priceAmount": 9.99,
                                      "priceCurrency": "USD"
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name").exists());
        }
    }

    @Nested
    @DisplayName("GET /api/products/{id}")
    class FindById {

        @Test
        @DisplayName("returns 404 when product does not exist")
        void should_return404_when_productDoesNotExist() throws Exception {
            when(productService.findById(99L)).thenThrow(new ProductNotFoundException(99L));

            mockMvc.perform(get("/api/products/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("PUT /api/products/{id}")
    class Update {

        @Test
        @DisplayName("returns 404 when product does not exist")
        void should_return404_when_productDoesNotExist() throws Exception {
            when(productService.update(eq(99L), any())).thenThrow(new ProductNotFoundException(99L));

            mockMvc.perform(put("/api/products/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(VALID_BODY))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("DELETE /api/products/{id}")
    class Deactivate {

        @Test
        @DisplayName("returns 404 when product does not exist")
        void should_return404_when_productDoesNotExist() throws Exception {
            doThrow(new ProductNotFoundException(99L)).when(productService).deactivate(99L);

            mockMvc.perform(delete("/api/products/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }
}

package com.demo.productcatalog.controller;

import com.demo.productcatalog.dto.ProductResponse;
import com.demo.productcatalog.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private ProductResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = ProductResponse.builder()
                .id(1L)
                .name("Widget")
                .description("A fine widget")
                .sku("WGT-001")
                .priceAmount(new BigDecimal("9.99"))
                .priceCurrency("USD")
                .active(true)
                .createdAt(Instant.parse("2026-01-01T00:00:00Z"))
                .updatedAt(Instant.parse("2026-01-01T00:00:00Z"))
                .build();
    }

    @Nested
    @DisplayName("POST /api/products")
    class Create {

        @Test
        @DisplayName("returns 201 with product body when request is valid")
        void should_return201WithBody_when_requestIsValid() throws Exception {
            when(productService.create(any())).thenReturn(sampleResponse);

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "name": "Widget",
                                      "description": "A fine widget",
                                      "sku": "WGT-001",
                                      "priceAmount": 9.99,
                                      "priceCurrency": "USD"
                                    }
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.sku").value("WGT-001"));
        }
    }

    @Nested
    @DisplayName("GET /api/products/{id}")
    class FindById {

        @Test
        @DisplayName("returns 200 with product body when product exists")
        void should_return200WithBody_when_productExists() throws Exception {
            when(productService.findById(1L)).thenReturn(sampleResponse);

            mockMvc.perform(get("/api/products/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Widget"));
        }
    }

    @Nested
    @DisplayName("GET /api/products")
    class FindAll {

        @Test
        @DisplayName("returns 200 with list of products")
        void should_return200WithList_when_productsExist() throws Exception {
            when(productService.findAll()).thenReturn(List.of(sampleResponse));

            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].sku").value("WGT-001"));
        }
    }

    @Nested
    @DisplayName("PUT /api/products/{id}")
    class Update {

        @Test
        @DisplayName("returns 200 with updated product body when product exists")
        void should_return200WithUpdatedBody_when_productExists() throws Exception {
            when(productService.update(eq(1L), any())).thenReturn(sampleResponse);

            mockMvc.perform(put("/api/products/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "name": "Widget",
                                      "sku": "WGT-001",
                                      "priceAmount": 9.99,
                                      "priceCurrency": "USD"
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }
    }

    @Nested
    @DisplayName("DELETE /api/products/{id}")
    class Deactivate {

        @Test
        @DisplayName("returns 204 when product is deactivated")
        void should_return204_when_productIsDeactivated() throws Exception {
            mockMvc.perform(delete("/api/products/1"))
                    .andExpect(status().isNoContent());
        }
    }
}

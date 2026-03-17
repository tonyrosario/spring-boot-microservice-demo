package com.demo.productcatalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductCatalogServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("application context loads without errors")
    void contextLoads() {
    }

    @Nested
    @DisplayName("POST /api/products")
    class CreateProduct {

        @Test
        @DisplayName("returns 201 when request is valid")
        void returns201ForValidRequest() throws Exception {
            String body = """
                    {
                      "name": "Smoke Widget",
                      "description": "A test widget",
                      "sku": "SMOKE-001",
                      "priceAmount": 9.99,
                      "priceCurrency": "USD"
                    }
                    """;

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.sku").value("SMOKE-001"))
                    .andExpect(jsonPath("$.active").value(true));
        }

        @Test
        @DisplayName("returns 400 when name is missing")
        void returns400WhenNameMissing() throws Exception {
            String body = """
                    {
                      "sku": "SMOKE-002",
                      "priceAmount": 9.99,
                      "priceCurrency": "USD"
                    }
                    """;

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name").exists());
        }
    }

    @Nested
    @DisplayName("GET /api/products/{id}")
    class GetProduct {

        @Test
        @DisplayName("returns 404 for unknown id")
        void returns404ForUnknownId() throws Exception {
            mockMvc.perform(get("/api/products/999999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }
}

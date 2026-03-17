package com.demo.productcatalog.controller;

import com.demo.productcatalog.dto.ProductRequest;
import com.demo.productcatalog.dto.ProductResponse;
import com.demo.productcatalog.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductController")
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = ProductResponse.builder()
                .id(1L)
                .name("Widget")
                .sku("WGT-001")
                .priceAmount(new BigDecimal("9.99"))
                .priceCurrency("USD")
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("POST /api/products")
    class Create {

        @Test
        @DisplayName("returns 201 with ProductResponse on success")
        void returns201() {
            ProductRequest request = ProductRequest.builder()
                    .name("Widget").sku("WGT-001")
                    .priceAmount(new BigDecimal("9.99")).priceCurrency("USD")
                    .build();
            when(productService.create(request)).thenReturn(sampleResponse);

            ResponseEntity<ProductResponse> response = productController.create(request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isEqualTo(sampleResponse);
        }
    }

    @Nested
    @DisplayName("GET /api/products/{id}")
    class FindById {

        @Test
        @DisplayName("returns 200 with ProductResponse when found")
        void returns200() {
            when(productService.findById(1L)).thenReturn(sampleResponse);

            ResponseEntity<ProductResponse> response = productController.findById(1L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(sampleResponse);
        }
    }

    @Nested
    @DisplayName("GET /api/products")
    class FindAll {

        @Test
        @DisplayName("returns 200 with list of ProductResponse")
        void returns200WithList() {
            when(productService.findAll()).thenReturn(List.of(sampleResponse));

            ResponseEntity<List<ProductResponse>> response = productController.findAll();

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("PUT /api/products/{id}")
    class Update {

        @Test
        @DisplayName("returns 200 with updated ProductResponse")
        void returns200() {
            ProductRequest request = ProductRequest.builder()
                    .name("Widget").sku("WGT-001")
                    .priceAmount(new BigDecimal("19.99")).priceCurrency("USD")
                    .build();
            when(productService.update(1L, request)).thenReturn(sampleResponse);

            ResponseEntity<ProductResponse> response = productController.update(1L, request);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(sampleResponse);
        }
    }

    @Nested
    @DisplayName("DELETE /api/products/{id}")
    class Deactivate {

        @Test
        @DisplayName("returns 204 and delegates to service")
        void returns204() {
            ResponseEntity<Void> response = productController.deactivate(1L);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            verify(productService).deactivate(1L);
        }
    }
}

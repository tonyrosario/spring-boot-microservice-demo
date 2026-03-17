package com.demo.productcatalog.service;

import com.demo.productcatalog.domain.MoneyEmbeddable;
import com.demo.productcatalog.domain.Product;
import com.demo.productcatalog.domain.exception.DuplicateSkuException;
import com.demo.productcatalog.domain.exception.ProductNotFoundException;
import com.demo.productcatalog.dto.ProductRequest;
import com.demo.productcatalog.dto.ProductResponse;
import com.demo.productcatalog.repository.ProductRepository;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductRequest validRequest;
    private Product savedProduct;

    @BeforeEach
    void setUp() {
        validRequest = ProductRequest.builder()
                .name("Widget")
                .description("A fine widget")
                .sku("WGT-001")
                .priceAmount(new BigDecimal("9.99"))
                .priceCurrency("USD")
                .build();

        savedProduct = Product.builder()
                .id(1L)
                .name("Widget")
                .description("A fine widget")
                .sku("WGT-001")
                .price(MoneyEmbeddable.of(Money.of(new BigDecimal("9.99"), "USD")))
                .active(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("returns ProductResponse when SKU is unique")
        void returnsResponseForUniqueSku() {
            when(productRepository.existsBySku("WGT-001")).thenReturn(false);
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

            ProductResponse response = productService.create(validRequest);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("Widget");
            assertThat(response.getSku()).isEqualTo("WGT-001");
            assertThat(response.getPriceAmount()).isEqualByComparingTo(new BigDecimal("9.99"));
            assertThat(response.getPriceCurrency()).isEqualTo("USD");
            assertThat(response.isActive()).isTrue();
        }

        @Test
        @DisplayName("throws DuplicateSkuException when SKU already exists")
        void throwsOnDuplicateSku() {
            when(productRepository.existsBySku("WGT-001")).thenReturn(true);

            assertThatThrownBy(() -> productService.create(validRequest))
                    .isInstanceOf(DuplicateSkuException.class)
                    .hasMessageContaining("WGT-001");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("returns ProductResponse when product exists")
        void returnsResponseWhenFound() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(savedProduct));

            ProductResponse response = productService.findById(1L);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getSku()).isEqualTo("WGT-001");
        }

        @Test
        @DisplayName("throws ProductNotFoundException when product does not exist")
        void throwsWhenNotFound() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.findById(99L))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("returns list of ProductResponse for all products")
        void returnsAllProducts() {
            when(productRepository.findAllByActiveTrue()).thenReturn(List.of(savedProduct));

            List<ProductResponse> responses = productService.findAll();

            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).getSku()).isEqualTo("WGT-001");
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("returns updated ProductResponse when product exists")
        void returnsUpdatedResponse() {
            Product updatedProduct = Product.builder()
                    .id(1L)
                    .name("Updated Widget")
                    .description("Updated description")
                    .sku("WGT-001")
                    .price(MoneyEmbeddable.of(Money.of(new BigDecimal("19.99"), "USD")))
                    .active(true)
                    .createdAt(savedProduct.getCreatedAt())
                    .updatedAt(Instant.now())
                    .build();

            ProductRequest updateRequest = ProductRequest.builder()
                    .name("Updated Widget")
                    .description("Updated description")
                    .sku("WGT-001")
                    .priceAmount(new BigDecimal("19.99"))
                    .priceCurrency("USD")
                    .build();

            when(productRepository.findById(1L)).thenReturn(Optional.of(savedProduct));
            when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

            ProductResponse response = productService.update(1L, updateRequest);

            assertThat(response.getName()).isEqualTo("Updated Widget");
            assertThat(response.getPriceAmount()).isEqualByComparingTo(new BigDecimal("19.99"));
        }

        @Test
        @DisplayName("throws ProductNotFoundException when product does not exist")
        void throwsWhenNotFound() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.update(99L, validRequest))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("deactivate()")
    class Deactivate {

        @Test
        @DisplayName("sets active to false and saves")
        void setsActiveToFalse() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(savedProduct));
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

            productService.deactivate(1L);

            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("throws ProductNotFoundException when product does not exist")
        void throwsWhenNotFound() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.deactivate(99L))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }
}

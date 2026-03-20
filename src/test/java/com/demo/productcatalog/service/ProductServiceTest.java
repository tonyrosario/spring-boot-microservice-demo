package com.demo.productcatalog.service;

import com.demo.productcatalog.domain.MoneyEmbeddable;
import com.demo.productcatalog.domain.Product;
import com.demo.productcatalog.dto.ProductRequest;
import com.demo.productcatalog.dto.ProductResponse;
import com.demo.productcatalog.repository.ProductRepository;
import com.demo.productcatalog.util.ProductTestFactory;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

        savedProduct = ProductTestFactory.aProduct().id(1L).build();
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("returns ProductResponse when SKU is unique")
        void should_returnProductResponse_when_skuIsUnique() {
            when(productRepository.existsBySku("WGT-001")).thenReturn(false);
            when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

            ProductResponse response = productService.create(validRequest);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getSku()).isEqualTo("WGT-001");
            assertThat(response.getPriceAmount()).isEqualByComparingTo(new BigDecimal("9.99"));
            assertThat(response.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("returns ProductResponse when product exists")
        void should_returnProductResponse_when_productExists() {
            when(productRepository.findById(1L)).thenReturn(Optional.of(savedProduct));

            ProductResponse response = productService.findById(1L);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getSku()).isEqualTo("WGT-001");
        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("returns list of active products")
        void should_returnActiveProducts_when_productsExist() {
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
        void should_returnUpdatedProductResponse_when_productExists() {
            Product updatedProduct = ProductTestFactory.aProduct()
                    .id(1L)
                    .name("Updated Widget")
                    .price(MoneyEmbeddable.of(Money.of(new BigDecimal("19.99"), "USD")))
                    .build();
            ProductRequest updateRequest = ProductRequest.builder()
                    .name("Updated Widget")
                    .description("A fine widget")
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
    }

    @Nested
    @DisplayName("deactivate()")
    class Deactivate {

        @Test
        @DisplayName("sets active to false and saves the product")
        void should_setActiveToFalse_when_productExists() {
            ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
            when(productRepository.findById(1L)).thenReturn(Optional.of(savedProduct));
            when(productRepository.save(captor.capture())).thenReturn(savedProduct);

            productService.deactivate(1L);

            assertThat(captor.getValue().isActive()).isFalse();
        }
    }
}

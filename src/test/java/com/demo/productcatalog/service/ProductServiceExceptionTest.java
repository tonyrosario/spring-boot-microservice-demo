package com.demo.productcatalog.service;

import com.demo.productcatalog.domain.exception.DuplicateSkuException;
import com.demo.productcatalog.domain.exception.ProductNotFoundException;
import com.demo.productcatalog.dto.ProductRequest;
import com.demo.productcatalog.repository.ProductRepository;
import com.demo.productcatalog.util.ProductTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService — exceptions")
class ProductServiceExceptionTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = ProductRequest.builder()
                .name("Widget")
                .description("A fine widget")
                .sku("WGT-001")
                .priceAmount(new BigDecimal("9.99"))
                .priceCurrency("USD")
                .build();
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("throws DuplicateSkuException when SKU already exists")
        void should_throwDuplicateSkuException_when_skuAlreadyExists() {
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
        @DisplayName("throws ProductNotFoundException when product does not exist")
        void should_throwProductNotFoundException_when_productIdIsUnknown() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.findById(99L))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("throws ProductNotFoundException when product does not exist")
        void should_throwProductNotFoundException_when_productIdIsUnknown() {
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
        @DisplayName("throws ProductNotFoundException when product does not exist")
        void should_throwProductNotFoundException_when_productIdIsUnknown() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.deactivate(99L))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }
}

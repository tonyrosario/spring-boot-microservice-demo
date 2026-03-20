package com.demo.productcatalog.repository;

import com.demo.productcatalog.domain.Product;
import com.demo.productcatalog.util.ProductTestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("ProductRepository")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.save(ProductTestFactory.aProduct().build());
        productRepository.save(ProductTestFactory.aProduct().sku("WGT-002").active(false).build());
    }

    @Nested
    @DisplayName("existsBySku()")
    class ExistsBySku {

        @Test
        @DisplayName("returns true when SKU exists")
        void should_returnTrue_when_skuExists() {
            assertThat(productRepository.existsBySku("WGT-001")).isTrue();
        }

        @Test
        @DisplayName("returns false when SKU does not exist")
        void should_returnFalse_when_skuDoesNotExist() {
            assertThat(productRepository.existsBySku("NONEXISTENT")).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByActiveTrue()")
    class FindAllByActiveTrue {

        @Test
        @DisplayName("returns only active products")
        void should_returnOnlyActiveProducts_when_someProductsAreInactive() {
            List<Product> results = productRepository.findAllByActiveTrue();

            assertThat(results).hasSize(1);
            assertThat(results.get(0).getSku()).isEqualTo("WGT-001");
        }
    }

    @Nested
    @DisplayName("findBySkuAndActiveTrue()")
    class FindBySkuAndActiveTrue {

        @Test
        @DisplayName("returns product when SKU exists and product is active")
        void should_returnProduct_when_skuExistsAndProductIsActive() {
            Optional<Product> result = productRepository.findBySkuAndActiveTrue("WGT-001");

            assertThat(result).isPresent();
            assertThat(result.get().getSku()).isEqualTo("WGT-001");
        }

        @Test
        @DisplayName("returns empty when product with SKU is inactive")
        void should_returnEmpty_when_productIsInactive() {
            Optional<Product> result = productRepository.findBySkuAndActiveTrue("WGT-002");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty when SKU does not exist")
        void should_returnEmpty_when_skuDoesNotExist() {
            Optional<Product> result = productRepository.findBySkuAndActiveTrue("NONEXISTENT");

            assertThat(result).isEmpty();
        }
    }
}

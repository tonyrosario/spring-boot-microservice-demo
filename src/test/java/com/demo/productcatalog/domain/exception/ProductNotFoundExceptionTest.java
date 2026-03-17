package com.demo.productcatalog.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductNotFoundException")
class ProductNotFoundExceptionTest {

    @Test
    @DisplayName("message includes the product id")
    void messageIncludesId() {
        ProductNotFoundException ex = new ProductNotFoundException(42L);
        assertThat(ex.getMessage()).isEqualTo("Product not found with id: 42");
    }
}

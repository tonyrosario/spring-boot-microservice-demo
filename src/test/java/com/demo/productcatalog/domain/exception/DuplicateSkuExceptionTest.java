package com.demo.productcatalog.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DuplicateSkuException")
class DuplicateSkuExceptionTest {

    @Test
    @DisplayName("includes the SKU in the message")
    void should_includeSkuInMessage_when_constructed() {
        DuplicateSkuException ex = new DuplicateSkuException("WGT-001");

        assertThat(ex.getMessage()).isEqualTo("Product already exists with SKU: WGT-001");
    }
}

package com.demo.productcatalog.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductResponse")
class ProductResponseTest {

    @Test
    @DisplayName("builds with all fields populated")
    void should_buildSuccessfully_when_allFieldsAreProvided() {
        Instant now = Instant.now();

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .name("Widget")
                .description("A fine widget")
                .sku("WGT-001")
                .priceAmount(new BigDecimal("9.99"))
                .priceCurrency("USD")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Widget");
        assertThat(response.getSku()).isEqualTo("WGT-001");
        assertThat(response.getPriceAmount()).isEqualByComparingTo(new BigDecimal("9.99"));
        assertThat(response.isActive()).isTrue();
    }
}

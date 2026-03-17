package com.demo.productcatalog.domain;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Product")
class ProductTest {

    @Nested
    @DisplayName("price field")
    class PriceField {

        @Test
        @DisplayName("stores price as MoneyEmbeddable with amount and currency")
        void storesPriceAsMoneyEmbeddable() {
            MoneyEmbeddable price = MoneyEmbeddable.of(Money.of(new BigDecimal("29.99"), "USD"));

            Product product = Product.builder()
                    .name("Test Product")
                    .sku("TEST-001")
                    .price(price)
                    .build();

            assertThat(product.getPrice().getAmount()).isEqualByComparingTo(new BigDecimal("29.99"));
            assertThat(product.getPrice().getCurrency()).isEqualTo("USD");
        }
    }
}

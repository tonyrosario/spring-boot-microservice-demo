package com.demo.productcatalog.domain;

import com.demo.productcatalog.util.ProductTestFactory;
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
        void should_storePriceAsMoneyEmbeddable_when_builtWithPrice() {
            MoneyEmbeddable price = MoneyEmbeddable.of(Money.of(new BigDecimal("29.99"), "USD"));

            Product product = ProductTestFactory.aProduct().price(price).build();

            assertThat(product.getPrice().getAmount()).isEqualByComparingTo(new BigDecimal("29.99"));
            assertThat(product.getPrice().getCurrency()).isEqualTo("USD");
        }
    }
}

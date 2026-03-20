package com.demo.productcatalog.domain;

import org.javamoney.moneta.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MoneyEmbeddable")
class MoneyEmbeddableTest {

    @Nested
    @DisplayName("of(MonetaryAmount)")
    class Of {

        @Test
        @DisplayName("stores the amount and currency from a MonetaryAmount")
        void should_storeAmountAndCurrency_when_createdFromMonetaryAmount() {
            MonetaryAmount source = Money.of(new BigDecimal("19.99"), "USD");

            MoneyEmbeddable money = MoneyEmbeddable.of(source);

            assertThat(money.getAmount()).isEqualByComparingTo(new BigDecimal("19.99"));
            assertThat(money.getCurrency()).isEqualTo("USD");
        }
    }

    @Nested
    @DisplayName("toMonetaryAmount()")
    class ToMonetaryAmount {

        @Test
        @DisplayName("reconstructs the original MonetaryAmount")
        void should_reconstructMonetaryAmount_when_convertedBack() {
            MoneyEmbeddable money = MoneyEmbeddable.of(Money.of(new BigDecimal("49.00"), "EUR"));

            MonetaryAmount result = money.toMonetaryAmount();

            assertThat(result.getCurrency().getCurrencyCode()).isEqualTo("EUR");
            assertThat(result.getNumber().numberValue(BigDecimal.class))
                    .isEqualByComparingTo(new BigDecimal("49.00"));
        }
    }
}

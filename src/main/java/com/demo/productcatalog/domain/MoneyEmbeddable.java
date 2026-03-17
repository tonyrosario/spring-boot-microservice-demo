package com.demo.productcatalog.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import org.javamoney.moneta.Money;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;

@Embeddable
@Getter
public class MoneyEmbeddable {

    private BigDecimal amount;
    private String currency;

    protected MoneyEmbeddable() {
    }

    private MoneyEmbeddable(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static MoneyEmbeddable of(MonetaryAmount monetaryAmount) {
        BigDecimal extractedAmount = monetaryAmount.getNumber().numberValue(BigDecimal.class);
        String extractedCurrency = monetaryAmount.getCurrency().getCurrencyCode();
        return new MoneyEmbeddable(extractedAmount, extractedCurrency);
    }

    public MonetaryAmount toMonetaryAmount() {
        return Money.of(amount, currency);
    }

}

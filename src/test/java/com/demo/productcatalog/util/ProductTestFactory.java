package com.demo.productcatalog.util;

import com.demo.productcatalog.domain.MoneyEmbeddable;
import com.demo.productcatalog.domain.Product;
import org.javamoney.moneta.Money;

import java.math.BigDecimal;
import java.time.Instant;

public final class ProductTestFactory {

    private static final String DEFAULT_SKU = "WGT-001";
    private static final String DEFAULT_NAME = "Widget";
    private static final String DEFAULT_DESCRIPTION = "A fine widget";
    private static final BigDecimal DEFAULT_PRICE = new BigDecimal("9.99");
    private static final String DEFAULT_CURRENCY = "USD";
    private static final Instant DEFAULT_TIMESTAMP = Instant.parse("2026-01-01T00:00:00Z");

    private ProductTestFactory() {
    }

    public static Product.ProductBuilder aProduct() {
        return Product.builder()
                .name(DEFAULT_NAME)
                .description(DEFAULT_DESCRIPTION)
                .sku(DEFAULT_SKU)
                .price(MoneyEmbeddable.of(Money.of(DEFAULT_PRICE, DEFAULT_CURRENCY)))
                .active(true)
                .createdAt(DEFAULT_TIMESTAMP)
                .updatedAt(DEFAULT_TIMESTAMP);
    }
}

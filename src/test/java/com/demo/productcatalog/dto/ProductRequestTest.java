package com.demo.productcatalog.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProductRequest")
class ProductRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private ProductRequest validRequest() {
        return ProductRequest.builder()
                .name("Widget")
                .description("A fine widget")
                .sku("WGT-001")
                .priceAmount(new BigDecimal("9.99"))
                .priceCurrency("USD")
                .build();
    }

    @Nested
    @DisplayName("when all fields are valid")
    class WhenValid {

        @Test
        @DisplayName("produces no constraint violations")
        void noViolations() {
            Set<ConstraintViolation<ProductRequest>> violations = validator.validate(validRequest());
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("name validation")
    class NameValidation {

        @Test
        @DisplayName("rejects blank name")
        void rejectsBlankName() {
            ProductRequest request = validRequest().toBuilder().name("").build();
            Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        }
    }

    @Nested
    @DisplayName("sku validation")
    class SkuValidation {

        @Test
        @DisplayName("rejects blank SKU")
        void rejectsBlankSku() {
            ProductRequest request = validRequest().toBuilder().sku("").build();
            Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("sku"));
        }
    }

    @Nested
    @DisplayName("priceAmount validation")
    class PriceAmountValidation {

        @Test
        @DisplayName("rejects null priceAmount")
        void rejectsNullPriceAmount() {
            ProductRequest request = validRequest().toBuilder().priceAmount(null).build();
            Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("priceAmount"));
        }

        @Test
        @DisplayName("rejects zero priceAmount")
        void rejectsZeroPriceAmount() {
            ProductRequest request = validRequest().toBuilder().priceAmount(BigDecimal.ZERO).build();
            Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("priceAmount"));
        }
    }

    @Nested
    @DisplayName("priceCurrency validation")
    class PriceCurrencyValidation {

        @Test
        @DisplayName("rejects blank priceCurrency")
        void rejectsBlankCurrency() {
            ProductRequest request = validRequest().toBuilder().priceCurrency("").build();
            Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("priceCurrency"));
        }

        @Test
        @DisplayName("rejects non-ISO-4217 currency code")
        void rejectsInvalidCurrencyCode() {
            ProductRequest request = validRequest().toBuilder().priceCurrency("us").build();
            Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("priceCurrency"));
        }
    }
}

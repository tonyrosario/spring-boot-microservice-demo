package com.demo.productcatalog.controller;

import com.demo.productcatalog.domain.exception.DuplicateSkuException;
import com.demo.productcatalog.domain.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("handleProductNotFound()")
    class HandleProductNotFound {

        @Test
        @DisplayName("returns 404 with error body")
        void should_return404WithErrorBody_when_productNotFound() {
            ResponseEntity<Map<String, Object>> response =
                    handler.handleProductNotFound(new ProductNotFoundException(1L));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).containsKeys("status", "message", "timestamp");
            assertThat(response.getBody().get("message")).asString().contains("1");
        }
    }

    @Nested
    @DisplayName("handleDuplicateSku()")
    class HandleDuplicateSku {

        @Test
        @DisplayName("returns 409 with error body")
        void should_return409WithErrorBody_when_skuIsDuplicate() {
            ResponseEntity<Map<String, Object>> response =
                    handler.handleDuplicateSku(new DuplicateSkuException("WGT-001"));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).containsKeys("status", "message", "timestamp");
            assertThat(response.getBody().get("message")).asString().contains("WGT-001");
        }
    }

    @Nested
    @DisplayName("handleValidation()")
    class HandleValidation {

        @Test
        @DisplayName("returns 400 with field-level validation errors")
        void should_return400WithFieldErrors_when_validationFails() {
            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            when(ex.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(
                    List.of(new FieldError("productRequest", "name", "Name is required"))
            );

            ResponseEntity<Map<String, Object>> response = handler.handleValidation(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).containsKeys("status", "errors", "timestamp");
        }
    }

    @Nested
    @DisplayName("handleGeneric()")
    class HandleGeneric {

        @Test
        @DisplayName("returns 500 for unexpected exceptions")
        void should_return500_when_unexpectedExceptionOccurs() {
            ResponseEntity<Map<String, Object>> response =
                    handler.handleGeneric(new RuntimeException("unexpected"));

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).containsKeys("status", "message", "timestamp");
        }
    }
}

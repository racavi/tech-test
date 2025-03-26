package com.rafacalvo.prices.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.rafacalvo.prices.domain.Price;
import com.rafacalvo.prices.domain.querying.PriceNotFoundException;


class PriceRepositoryAdapterTest {

    private final PriceRepositoryAdapter adapter = new PriceRepositoryAdapter();

    @Test
    void shouldReturnPrice_whenPriceExists() {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime fecha = LocalDateTime.parse("2020-06-14T16:00:00");

        // When
        Price price = adapter.findBestPrice(brandId, productId, fecha);

        // Then
        assertThat(price).isNotNull();
        assertThat(price.brandId()).isEqualTo(brandId);
        assertThat(price.productId()).isEqualTo(productId);
        assertThat(price.price()).isEqualTo(new BigDecimal("35.50"));
        assertThat(price.currency()).isEqualTo("EUR");
    }

    @Test
    void shouldThrowException_whenPriceDoesNotExist() {
        // Given
        Integer brandId = 1;
        Integer productId = 99999;
        LocalDateTime fecha = LocalDateTime.now();

        // When
        Throwable thrown = catchThrowable(() -> adapter.findBestPrice(brandId, productId, fecha));

        // Then
        assertThat(thrown)
            .isInstanceOf(PriceNotFoundException.class)
            .hasMessageContaining("No se encontró precio");
    }

    @Test
    void shouldPropagateException_whenRepositoryThrowsError() {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime fecha = LocalDateTime.parse("2020-06-14T16:00:00");

        // When
        Throwable thrown = catchThrowable(() -> adapter.findBestPrice(brandId, productId, fecha));

        // Then
        assertThat(thrown)
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Database error");
    }

}

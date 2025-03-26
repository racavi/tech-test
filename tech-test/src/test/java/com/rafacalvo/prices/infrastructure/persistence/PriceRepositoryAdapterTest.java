package com.rafacalvo.prices.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.rafacalvo.prices.domain.Price;
import com.rafacalvo.prices.domain.querying.PriceNotFoundException;

import reactor.core.publisher.Mono;

class PriceRepositoryAdapterTest {

    private final PriceR2DBCRepository r2dbcRepository = mock(PriceR2DBCRepository.class);
    private final PriceRepositoryAdapter adapter = new PriceRepositoryAdapter(r2dbcRepository);

    @Test
    void shouldReturnPrice_whenPriceExists() {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime fecha = LocalDateTime.parse("2020-06-14T16:00:00");

        PriceEntity mockEntity = new PriceEntity(
            1L, brandId, LocalDateTime.parse("2020-06-14T00:00:00"),
            LocalDateTime.parse("2020-12-31T23:59:59"), 1, productId, 0, new BigDecimal("35.50"), "EUR"
        );

        when(r2dbcRepository.findBestPrice(brandId, productId, fecha)).thenReturn(Mono.just(mockEntity));

        // When
        Price price = adapter.findBestPrice(brandId, productId, fecha);

        // Then
        assertThat(price).isNotNull();
        assertThat(price.brandId()).isEqualTo(brandId);
        assertThat(price.productId()).isEqualTo(productId);
        assertThat(price.price()).isEqualTo(new BigDecimal("35.50"));
        assertThat(price.currency()).isEqualTo("EUR");

        verify(r2dbcRepository, times(1)).findBestPrice(brandId, productId, fecha);
    }

    @Test
    void shouldThrowException_whenPriceDoesNotExist() {
        // Given
        Integer brandId = 1;
        Integer productId = 99999;
        LocalDateTime fecha = LocalDateTime.now();

        when(r2dbcRepository.findBestPrice(brandId, productId, fecha)).thenReturn(Mono.empty());

        // When
        Throwable thrown = catchThrowable(() -> adapter.findBestPrice(brandId, productId, fecha));

        // Then
        assertThat(thrown)
            .isInstanceOf(PriceNotFoundException.class)
            .hasMessageContaining("No se encontró precio");

        verify(r2dbcRepository, times(1)).findBestPrice(brandId, productId, fecha);
    }

    @Test
    void shouldPropagateException_whenRepositoryThrowsError() {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime fecha = LocalDateTime.parse("2020-06-14T16:00:00");

        when(r2dbcRepository.findBestPrice(brandId, productId, fecha))
            .thenReturn(Mono.error(new RuntimeException("Database error")));

        // When
        Throwable thrown = catchThrowable(() -> adapter.findBestPrice(brandId, productId, fecha));

        // Then
        assertThat(thrown)
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Database error");

        verify(r2dbcRepository, times(1)).findBestPrice(brandId, productId, fecha);
    }

}

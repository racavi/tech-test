package com.rafacalvo.prices.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.rafacalvo.prices.domain.querying.PriceNotFoundException;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

        // When & Then
        StepVerifier.create(adapter.findBestPrice(brandId, productId, fecha))
                    .expectNextMatches(price -> {
                        assertThat(price.brandId()).isEqualTo(brandId);
                        assertThat(price.productId()).isEqualTo(productId);
                        assertThat(price.price()).isEqualTo(new BigDecimal("35.50"));
                        assertThat(price.currency()).isEqualTo("EUR");
                        return true;
                    })
                    .verifyComplete();

        verify(r2dbcRepository, times(1)).findBestPrice(brandId, productId, fecha);
    }

    @Test
    void shouldThrowException_whenPriceDoesNotExist() {
        // Given
        Integer brandId = 1;
        Integer productId = 99999;
        LocalDateTime fecha = LocalDateTime.now();

        when(r2dbcRepository.findBestPrice(brandId, productId, fecha)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(adapter.findBestPrice(brandId, productId, fecha))
                    .expectErrorMatches(throwable -> throwable instanceof PriceNotFoundException &&
                            throwable.getMessage().contains("No se encontró precio"))
                    .verify();

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

        // When & Then
        StepVerifier.create(adapter.findBestPrice(brandId, productId, fecha))
                    .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                            throwable.getMessage().contains("Database error"))
                    .verify();

        verify(r2dbcRepository, times(1)).findBestPrice(brandId, productId, fecha);
    }

}

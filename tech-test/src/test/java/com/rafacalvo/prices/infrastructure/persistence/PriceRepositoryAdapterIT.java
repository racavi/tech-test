package com.rafacalvo.prices.infrastructure.persistence;

import com.rafacalvo.prices.domain.Price;
import com.rafacalvo.prices.domain.querying.PriceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@DataR2dbcTest
@Import(PriceRepositoryAdapter.class)
public class PriceRepositoryAdapterIT {

    @Autowired
    private PriceRepositoryAdapter adapter;

    @Test
    void shouldMapPriceEntityToDomainPrice_whenPriceExists() {
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
        assertThat(price.price()).isEqualTo(new BigDecimal("25.45")); // Based on data.sql
        assertThat(price.currency()).isEqualTo("EUR");
        assertThat(price.startDate()).isEqualTo(LocalDateTime.parse("2020-06-14T15:00:00"));
        assertThat(price.endDate()).isEqualTo(LocalDateTime.parse("2020-06-14T18:30:00"));
    }

    @Test
    void shouldThrowException_whenPriceDoesNotExist() {
        // Given
        Integer brandId = 1;
        Integer productId = 99999; // Non-existent product
        LocalDateTime fecha = LocalDateTime.now();

        // When
        Throwable thrown = catchThrowable(() -> adapter.findBestPrice(brandId, productId, fecha));

        // Then
        assertThat(thrown)
            .isInstanceOf(PriceNotFoundException.class)
            .hasMessageContaining("No se encontró precio");
    }

}

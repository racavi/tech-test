package com.rafacalvo.prices.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;

import reactor.test.StepVerifier;

@DataR2dbcTest
public class PriceR2DBCRepositoryIT {
    
    @Autowired
    private PriceR2DBCRepository r2dbcRepository;

    // Prueba 1: 14/06/2020 10:00 -> Solo aplica la primera tarifa (PRICE_LIST=1) el resto de START_DATE estan fuera de rango
    @Test
    void shouldReturnPriceList1WhenDateIs20200614At10AM() {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime fecha = LocalDateTime.parse("2020-06-14T10:00:00");

        // When & Then
        StepVerifier.create(r2dbcRepository.findBestPrice(brandId, productId, fecha))
        .assertNext(price -> {
            assertThat(price.getPriceList()).isEqualTo(1);
            assertThat(price.getPrice()).isEqualTo(new BigDecimal("35.5"));
        })
        .verifyComplete();
    }

    // Prueba 2: 14/06/2020 16:00 -> aplica la tarifa con mayor prioridad (PRICE_LIST=2) Entre 2 rangos de fechas, 
    @Test
    void shouldReturnPriceList2WhenDateIs20200614At4PM() {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime fecha = LocalDateTime.parse("2020-06-14T16:00:00");

        // When & Then
        StepVerifier.create(r2dbcRepository.findBestPrice(brandId, productId, fecha))
        .assertNext(price -> {
            assertThat(price.getPriceList()).isEqualTo(2);
            assertThat(price.getPrice()).isEqualTo(new BigDecimal("25.45"));
            })
        .verifyComplete();
    }

    // Prueba 3: 14/06/2020 21:00 -> Sólo aplica la tarifa de PRICE_LIST=1
    @Test
    void shouldReturnPriceList1WhenDateIs20200614At9PM() {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime fecha = LocalDateTime.parse("2020-06-14T21:00:00");

        // When & Then
        StepVerifier.create(r2dbcRepository.findBestPrice(brandId, productId, fecha))
        .assertNext(price -> {
            assertThat(price.getPriceList()).isEqualTo(1);
            assertThat(price.getPrice()).isEqualTo(new BigDecimal("35.5"));
            })
        .verifyComplete();
    }

    // Prueba 4: 15/06/2020 10:00 -> Aplica la tarifa de PRICE_LIST=3 (tiene prioridad sobre la tarifa de PRICE_LIST=1)
    @Test
    void shouldReturnPriceList3WhenDateIs20200615At10AM() {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime fecha = LocalDateTime.parse("2020-06-15T10:00:00");

        // When & Then
        StepVerifier.create(r2dbcRepository.findBestPrice(brandId, productId, fecha))
        .assertNext(price -> {
            assertThat(price.getPriceList()).isEqualTo(3);
            assertThat(price.getPrice()).isEqualTo(new BigDecimal("30.5"));
            })
        .verifyComplete();
    }

    // Prueba 5: 16/06/2020 21:00 -> Aplica la tarifa de PRICE_LIST=4 (tiene prioridad sobre la tarifa de PRICE_LIST=1)
    @Test
    void shouldReturnPriceList4WhenDateIs20200616At9PM() {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime fecha = LocalDateTime.parse("2020-06-16T21:00:00");

        // When & Then
        StepVerifier.create(r2dbcRepository.findBestPrice(brandId, productId, fecha))
        .assertNext(price -> {
            assertThat(price.getPriceList()).isEqualTo(4);
            assertThat(price.getPrice()).isEqualTo(new BigDecimal("38.95"));
            })
        .verifyComplete();
    }

    @Test
    void testFindBestPrice_NoMatch() {
        Integer brandId = 99; // Non-existent brand
        Integer productId = 35455;
        LocalDateTime fecha = LocalDateTime.parse("2020-06-14T16:00:00");

        StepVerifier.create(r2dbcRepository.findBestPrice(brandId, productId, fecha))
                    .verifyComplete(); // Expect no results
    }

    @Test
    void testFindBestPrice_DateOutOfRange() {
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime fecha = LocalDateTime.parse("2025-01-01T00:00:00"); // Date outside range

        StepVerifier.create(r2dbcRepository.findBestPrice(brandId, productId, fecha))
                    .verifyComplete(); // Expect no results
    }

}

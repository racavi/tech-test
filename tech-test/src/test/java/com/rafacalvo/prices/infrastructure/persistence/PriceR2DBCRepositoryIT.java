package com.rafacalvo.prices.infrastructure.persistence;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;

import reactor.test.StepVerifier;

@DataR2dbcTest
public class PriceR2DBCRepositoryIT {
    
    @Autowired
    private PriceR2DBCRepository r2dbcRepository;

    @Test
    void testFindBestPrice_ValidCase() {
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime fecha = LocalDateTime.parse("2020-06-14T16:00:00");

        StepVerifier.create(r2dbcRepository.findBestPrice(brandId, productId, fecha))
                    .expectNextMatches(entity -> entity.getBrandId().equals(brandId)
                            && entity.getProductId().equals(productId))
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

    @Test
    void testFindBestPrice_HighestPriority() {
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime fecha = LocalDateTime.parse("2020-06-14T16:00:00");

        StepVerifier.create(r2dbcRepository.findBestPrice(brandId, productId, fecha))
                    .expectNextMatches(entity -> entity.getPriority() == 1) // Expect highest priority
                    .verifyComplete();
    }

}

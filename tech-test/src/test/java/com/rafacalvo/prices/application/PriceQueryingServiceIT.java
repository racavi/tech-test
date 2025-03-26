package com.rafacalvo.prices.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import reactor.core.publisher.Mono;

@SpringBootTest
public class PriceQueryingServiceIT {

    @Autowired
    private PriceQueryingService priceQueryingService;

    @Test
    void testGetBestPrice_success() {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime dayTime = LocalDateTime.parse("2020-06-14T16:00:00");
        
        PriceResponse expectedResponse = new PriceResponse(
            brandId, 
            productId, 
            2, 
            LocalDateTime.parse("2020-06-14T15:00:00"), 
            LocalDateTime.parse("2020-06-14T18:30:00"), 
            new BigDecimal("25.45"), 
            "EUR");

        // When
        Mono<PriceResponse> actualResponseMono = priceQueryingService.getBestPrice(brandId, productId, dayTime);

        // Then
        assertThat(actualResponseMono.block()).isEqualTo(expectedResponse);
    }

}

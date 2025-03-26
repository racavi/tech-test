package com.rafacalvo.prices.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rafacalvo.prices.domain.querying.PriceRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class PriceQueryingServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private PriceQueryingService priceQueryingService;

    @Test
    void testGetBestPrice_success() {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime dateTime = LocalDateTime.parse("2020-06-14T16:00:00");

        LocalDateTime startDate = LocalDateTime.parse("2020-06-14T00:00:00");
        LocalDateTime endDate = LocalDateTime.parse("2020-06-15T00:00:00");
        Integer priceList = 1;
        BigDecimal price = new BigDecimal("100.0");
        String currency = "EUR";
        PriceResponse expectedResponse = new PriceResponse(
                brandId,
                productId,
                priceList,
                startDate,
                endDate,
                price,
                currency);

        // When
        Mono<PriceResponse> actualResponseMono = priceQueryingService.getBestPrice(brandId, productId, dateTime);

        // Then
        assertThat(actualResponseMono.block()).isEqualTo(expectedResponse);
        verify(priceRepository).findBestPrice(brandId, productId, dateTime);
    }

}

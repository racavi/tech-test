package com.rafacalvo.prices.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.rafacalvo.prices.application.PriceQueryingService;
import com.rafacalvo.prices.application.PriceResponse;
import com.rafacalvo.prices.domain.querying.PriceNotFoundException;

import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@WebFluxTest(PriceController.class)
public class PriceControllerTest {

    @MockitoBean
    private PriceQueryingService priceQueryingService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenPriceExists_thenReturnOk() {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime dateTime = LocalDateTime.parse("2020-06-14T16:00:00");
    
        PriceResponse priceResponse = new PriceResponse(
            brandId,
            productId,
            1, // priceList
            LocalDateTime.parse("2020-06-14T00:00:00"), // startDate
            LocalDateTime.parse("2020-06-14T23:59:59"), // endDate
            new BigDecimal("35.50"), // price
            "EUR" // currency
        );

        // Mock the service
        given(priceQueryingService.getBestPrice(brandId, productId, dateTime))
            .willReturn(Mono.just(priceResponse));

        // When & Then
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/prices/applicable")
                .queryParam("dateTime", dateTime)
                .queryParam("productId", productId)
                .queryParam("brandId", brandId)
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(PriceResponse.class)
            .value(response -> {
                assertThat(response).isNotNull();
                assertThat(response.price()).isEqualTo(new BigDecimal("35.50"));
            });
    }
    
    @Test
    void whenPriceNotExists_thenReturnNotFound() {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime dateTime = LocalDateTime.parse("1970-01-01T00:00:00");        

        given(priceQueryingService.getBestPrice(brandId, productId, dateTime))
            .willReturn(Mono.error(new PriceNotFoundException("No price found for the given parameters.")));

        // When & Then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                         .path("/api/prices/applicable")
                         .queryParam("brandId", brandId)
                         .queryParam("productId", productId)
                         .queryParam("dateTime", dateTime)
                         .build())
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isNotFound();
    }

    @Test
    void whenInvalidBrandId_thenReturnBadRequest() {
        // Given
        Integer invalidBrandId = -1; // Invalid brandId
        Integer productId = 35455;
        LocalDateTime dateTime = LocalDateTime.parse("2020-06-14T16:00:00");
    
        // When & Then
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/prices/applicable")
                .queryParam("dateTime", dateTime)
                .queryParam("productId", productId)
                .queryParam("brandId", invalidBrandId)
                .build())
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void whenMissingdBrandId_thenReturnBadRequest() {
        // Given
        Integer productId = 35455;
        LocalDateTime dateTime = LocalDateTime.parse("2020-06-14T16:00:00");

        // When & Then
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/prices/applicable")
                .queryParam("dateTime", dateTime)
                .queryParam("productId", productId)
                .build())
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void whenMissingDateTime_thenReturnBadRequest() {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;

        // When & Then
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/prices/applicable")
                .queryParam("productId", productId)
                .queryParam("brandId", brandId)
                .build())
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void whenInvalidProductId_thenReturnBadRequest() {
        // Given
        Integer brandId = 1;
        Integer invalidProductId = 0; // Invalid productId
        LocalDateTime dateTime = LocalDateTime.parse("2020-06-14T16:00:00");

        // When & Then
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/prices/applicable")
                .queryParam("dateTime", dateTime)
                .queryParam("productId", invalidProductId)
                .queryParam("brandId", brandId)
                .build())
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void whenMissingProductId_thenReturnBadRequest() {
        // Given
        Integer brandId = 1;
        LocalDateTime dateTime = LocalDateTime.parse("2020-06-14T16:00:00");

        // When & Then
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/prices/applicable")
                .queryParam("dateTime", dateTime)
                .queryParam("brandId", brandId)
                .build())
            .exchange()
            .expectStatus().isBadRequest();
    }

}

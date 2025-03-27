package com.rafacalvo.prices.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.rafacalvo.prices.application.PriceResponse;

@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
@WebFluxTest(PriceController.class)
public class PriceControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenPriceExists_thenReturnOk() {
        // Given
        Integer brandId = 1;
        Integer productId = 35455;
        LocalDateTime dateTime = LocalDateTime.parse("2020-06-14T16:00:00");
    
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

        // When & Then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder
                         .path("/prices/best")
                         .queryParam("brandId", brandId)
                         .queryParam("productId", productId)
                         .queryParam("fecha", dateTime)
                         .build())
                     .accept(MediaType.APPLICATION_JSON)
                     .exchange()
                     .expectStatus().isNotFound();
    }

}

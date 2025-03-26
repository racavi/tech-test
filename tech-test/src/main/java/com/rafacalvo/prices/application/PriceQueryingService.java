package com.rafacalvo.prices.application;

import java.time.LocalDateTime;

import reactor.core.publisher.Mono;

public class PriceQueryingService {

    public Mono<PriceResponse> getBestPrice(Integer brandId, Integer productId, LocalDateTime fecha) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}

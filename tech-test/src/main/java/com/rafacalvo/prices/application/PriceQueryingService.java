package com.rafacalvo.prices.application;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.rafacalvo.prices.domain.Price;
import com.rafacalvo.prices.domain.querying.PriceRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class PriceQueryingService {

    private final PriceRepository priceRepository;

    public Mono<PriceResponse> getBestPrice(Integer brandId, Integer productId, LocalDateTime dateTime) {
        return priceRepository.findBestPrice(brandId, productId, dateTime)
                   .map(this::toPriceResponse);
    }

    private PriceResponse toPriceResponse(Price price) {
        return new PriceResponse(
            price.brandId(),
            price.productId(),
            price.priceList(),
            price.startDate(),
            price.endDate(),
            price.price(),
            price.currency()
        );
    }

}

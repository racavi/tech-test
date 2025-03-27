package com.rafacalvo.prices.presentation;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rafacalvo.prices.application.PriceQueryingService;
import com.rafacalvo.prices.application.PriceResponse;
import com.rafacalvo.prices.domain.querying.PriceNotFoundException;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@AllArgsConstructor
@RestController
@RequestMapping("/api/prices")
public class PriceController {

    private final PriceQueryingService priceQueryingService;

    @GetMapping("/applicable")
    public Mono<ResponseEntity<PriceResponse>> getBestPrice(
            @RequestParam Integer brandId,
            @RequestParam Integer productId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {

        return priceQueryingService.getBestPrice(brandId, productId, dateTime)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    if (e instanceof PriceNotFoundException) {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                    return Mono.just(ResponseEntity.status(500).body(null));
                });
    }

}

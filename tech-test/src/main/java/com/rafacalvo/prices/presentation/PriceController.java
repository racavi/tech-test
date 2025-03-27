package com.rafacalvo.prices.presentation;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rafacalvo.prices.application.PriceResponse;

import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

    @GetMapping("/applicable")
    public Mono<ResponseEntity<PriceResponse>> getBestPrice(
        @RequestParam Integer brandId,
        @RequestParam Integer productId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
    
        // No-op implementation: Always return 500 Internal Server Error
        return Mono.just(ResponseEntity.status(500).build());
    }

}

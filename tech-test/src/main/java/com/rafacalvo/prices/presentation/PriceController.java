package com.rafacalvo.prices.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.rafacalvo.prices.application.PriceQueryingService;
import com.rafacalvo.prices.application.PriceResponse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Tag(name = "Prices API", description = "API for querying product prices")
@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/api/prices")
public class PriceController {

    private final PriceQueryingService priceQueryingService;

    @Operation(summary = "Get applicable price for a product", description = "Retrieves applicable price for a given product based on its own product ID, its related brand ID at a given date-time.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Price found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PriceResponse.class), examples = @ExampleObject(description = "Example Price Response", value = ApiExamples.EXAMPLE_PRICE_RESPONSE))),
            @ApiResponse(responseCode = "404", description = "Price not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    })
    @GetMapping("/applicable")
    public Mono<ResponseEntity<PriceResponse>> getBestPrice(
            @RequestParam @NotNull @Positive Integer brandId,
            @RequestParam @NotNull @Positive Integer productId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {

        return priceQueryingService.getBestPrice(brandId, productId, dateTime)
                .map(ResponseEntity::ok);
    }

}

package com.rafacalvo.prices.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceResponse(
        Integer brandId,
        Integer productId,
        Integer priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal price,
        String currency) {
}

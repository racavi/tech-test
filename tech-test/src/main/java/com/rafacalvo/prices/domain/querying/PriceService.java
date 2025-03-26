package com.rafacalvo.prices.domain.querying;

import java.time.LocalDateTime;

import com.rafacalvo.prices.domain.Price;

public interface PriceService {

    Price getBestPrice(Integer brandId, Integer productId, LocalDateTime fecha);

}

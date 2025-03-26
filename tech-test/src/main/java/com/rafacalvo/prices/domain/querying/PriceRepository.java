package com.rafacalvo.prices.domain.querying;

import java.time.LocalDateTime;

import com.rafacalvo.prices.domain.Price;

public interface PriceRepository {

    /**
     * Busca el mejor precio para una combinación de brandId, productId y fecha.
     * Lanza PriceNotFoundException si no se encuentra.
     */
    Price findBestPrice(Integer brandId, Integer productId, LocalDateTime fecha);

}

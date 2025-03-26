package com.rafacalvo.prices.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.rafacalvo.prices.domain.Price;
import com.rafacalvo.prices.domain.querying.PriceRepository;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Repository
public class PriceRepositoryAdapter implements PriceRepository {

    @Override
    public Price findBestPrice(Integer brandId, Integer productId, LocalDateTime fecha) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}

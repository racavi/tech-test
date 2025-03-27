package com.rafacalvo.prices.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.rafacalvo.prices.domain.Price;
import com.rafacalvo.prices.domain.querying.PriceNotFoundException;
import com.rafacalvo.prices.domain.querying.PriceRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@AllArgsConstructor
@Repository
public class PriceRepositoryAdapter implements PriceRepository {

    private final PriceR2DBCRepository r2dbcRepository;

    // Mapea PriceEntity a Price (modelo de dominio)
    private Price toDomain(PriceEntity entity) {
        return new Price(
                entity.getId(),
                entity.getBrandId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getPriceList(),
                entity.getProductId(),
                entity.getPriority(),
                entity.getPrice(),
                entity.getCurrency());
    }

    @Override
    public Mono<Price> findBestPrice(Integer brandId, Integer productId, LocalDateTime fecha) {
        return r2dbcRepository.findBestPrice(brandId, productId, fecha)
                .map(this::toDomain)
                .switchIfEmpty(Mono.error(new PriceNotFoundException(
                    "No se encontró precio para brandId=" + brandId +
                    ", productId=" + productId + " en la fecha " + fecha
                )));
    }

}

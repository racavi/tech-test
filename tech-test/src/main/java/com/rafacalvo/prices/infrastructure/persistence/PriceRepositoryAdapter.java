package com.rafacalvo.prices.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import com.rafacalvo.prices.domain.Price;
import com.rafacalvo.prices.domain.querying.PriceNotFoundException;
import com.rafacalvo.prices.domain.querying.PriceRepository;

import lombok.AllArgsConstructor;
import reactor.core.scheduler.Schedulers;

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
            entity.getCurrency()
        );
    }

    @Override
    public Price findBestPrice(Integer brandId, Integer productId, LocalDateTime fecha) {
        // Convertimos el Mono en un objeto Price bloqueando de forma controlada.
        PriceEntity entity = r2dbcRepository.findBestPrice(brandId, productId, fecha)
                .subscribeOn(Schedulers.boundedElastic())
                .block();
        if (entity == null) {
            throw new PriceNotFoundException("No se encontró precio para brandId=" + brandId +
                    ", productId=" + productId + " en la fecha " + fecha);
        }
        return toDomain(entity);
    }

}

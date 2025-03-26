package com.rafacalvo.prices.infrastructure.persistence;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

public interface PriceR2DBCRepository extends ReactiveCrudRepository<PriceEntity, Long> {

    @Query("""
        SELECT * 
        FROM PRICES 
        WHERE BRAND_ID = :brandId 
          AND PRODUCT_ID = :productId 
          AND :fecha BETWEEN START_DATE AND END_DATE 
        LIMIT 1
    """)
    Mono<PriceEntity> findBestPrice(Integer brandId, Integer productId, LocalDateTime fecha);

}

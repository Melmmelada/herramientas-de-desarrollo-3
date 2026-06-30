package cl.usm.sansaweigh.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor

// implementa la logica: 3 reintentos con espera exponencial y si
// todo falla busca en caché, luego en Redis con id -1, y por último devuelve un valor hardcoded.
public class ExternalScaleClient {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate;

    @Value("${external.scale.api.url}")
    private String externalApiUrl;

    private static final String CACHE_PREFIX = "scale:spec:";
    private static final long TTL_SECONDS = 120;
    private static final int MAX_RETRIES = 3;

    public ScaleSpecification getScaleSpecifications(String scaleId) {
        String cacheKey = CACHE_PREFIX + scaleId;

        // Intentar con reintentos exponenciales
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                ScaleSpecification spec = restTemplate.getForObject(
                        externalApiUrl + "/" + scaleId,
                        ScaleSpecification.class
                );
                if (spec != null) {
                    redisTemplate.opsForValue().set(cacheKey, spec, Duration.ofSeconds(TTL_SECONDS));
                    log.info("Spec obtenida de API externa para balanza {}", scaleId);
                    return spec;
                }
            } catch (Exception e) {
                log.warn("Intento {}/{} fallido para balanza {}: {}",
                        attempt, MAX_RETRIES, scaleId, e.getMessage());
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep((long) Math.pow(2, attempt) * 500);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        // Fallback 1: caché de la balanza solicitada
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof ScaleSpecification spec) {
            log.info("Usando caché para balanza {}", scaleId);
            return spec;
        }

        // Fallback 2: spec por defecto en Redis con id "-1"
        Object defaultSpec = redisTemplate.opsForValue().get(CACHE_PREFIX + "-1");
        if (defaultSpec instanceof ScaleSpecification spec) {
            log.warn("Usando especificación por defecto (-1) para balanza {}", scaleId);
            return spec;
        }

        // Fallback final hardcoded
        log.error("Sin datos en caché. Retornando spec hardcoded para balanza {}", scaleId);
        return new ScaleSpecification("-1", "Default Scale", "Unknown", 100.0, 0.1, 0.0);
    }
}
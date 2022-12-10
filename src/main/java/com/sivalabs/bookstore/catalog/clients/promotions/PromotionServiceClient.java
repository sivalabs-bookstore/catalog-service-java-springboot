package com.sivalabs.bookstore.catalog.clients.promotions;

import com.sivalabs.bookstore.catalog.ApplicationProperties;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionServiceClient {
    private final RestTemplate restTemplate;
    private final ApplicationProperties properties;

    public List<Promotion> getPromotions(List<String> productCodes) {
        log.info("Fetching promotions for productCodes: {}", productCodes);
        if (productCodes == null || productCodes.isEmpty()) {
            return List.of();
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> httpEntity = new HttpEntity<>(headers);
            String productCodesCsv = String.join(",", productCodes);
            String url =
                    properties.promotionServiceUrl()
                            + "/api/promotions?productCodes="
                            + productCodesCsv;
            ResponseEntity<List<Promotion>> response =
                    restTemplate.exchange(
                            url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {});
            return response.getBody();
        } catch (RuntimeException e) {
            log.error("Error while fetching promotion for productCodes: " + productCodes, e);
            return List.of();
        }
    }

    public Optional<Promotion> getPromotion(String productCode) {
        log.info("Fetching promotion for productCode: {}", productCode);
        if (productCode == null || productCode.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> httpEntity = new HttpEntity<>(headers);
            String url = properties.promotionServiceUrl() + "/api/promotions/" + productCode;
            ResponseEntity<Promotion> response =
                    restTemplate.exchange(url, HttpMethod.GET, httpEntity, Promotion.class);
            return Optional.ofNullable(response.getBody());
        } catch (RuntimeException e) {
            log.error("Error while fetching promotion for productCode: " + productCode, e);
            return Optional.empty();
        }
    }
}

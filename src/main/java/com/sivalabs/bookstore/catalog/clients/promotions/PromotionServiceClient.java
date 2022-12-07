package com.sivalabs.bookstore.catalog.clients.promotions;

import com.sivalabs.bookstore.catalog.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PromotionServiceClient {
    private final ApplicationProperties properties;

    public PromotionServiceClient(ApplicationProperties properties) {
        this.properties = properties;
    }

    public List<Promotion> getPromotions(List<String> isbnList) {
        log.info("Fetching promotions for isbnList: {}", isbnList);
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> httpEntity = new HttpEntity<>(headers);
            String isbnsCsv = String.join(",", isbnList);
            ResponseEntity<List<Promotion>> response = restTemplate.exchange(
                    properties.promotionServiceUrl() + "/api/promotions?isbns="+isbnsCsv, HttpMethod.GET, httpEntity,
                    new ParameterizedTypeReference<>() {
                    });
            return response.getBody();
        } catch (RuntimeException e) {
            log.error("Error while fetching promotion for isbnList: " + isbnList , e);
            return List.of();
        }
    }

    public Optional<Promotion> getPromotion(String isbn) {
        log.info("Fetching promotion for isbn: {}", isbn);
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> httpEntity = new HttpEntity<>(headers);
            ResponseEntity<Promotion> response = restTemplate.exchange(
                    properties.promotionServiceUrl() + "/api/promotions/" + isbn, HttpMethod.GET, httpEntity,
                    Promotion.class);
            return Optional.ofNullable(response.getBody());
        } catch (RuntimeException e) {
            log.error("Error while fetching promotion for isbn: " + isbn , e);
            return Optional.empty();
        }
    }
}

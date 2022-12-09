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

    public List<Promotion> getPromotions(List<String> isbnList) {
        log.info("Fetching promotions for isbnList: {}", isbnList);
        if (isbnList == null || isbnList.isEmpty()) {
            return List.of();
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> httpEntity = new HttpEntity<>(headers);
            String isbnsCsv = String.join(",", isbnList);
            String url = properties.promotionServiceUrl() + "/api/promotions?isbns=" + isbnsCsv;
            ResponseEntity<List<Promotion>> response =
                    restTemplate.exchange(
                            url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<>() {});
            return response.getBody();
        } catch (RuntimeException e) {
            log.error("Error while fetching promotion for isbnList: " + isbnList, e);
            return List.of();
        }
    }

    public Optional<Promotion> getPromotion(String isbn) {
        log.info("Fetching promotion for isbn: {}", isbn);
        if (isbn == null || isbn.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<?> httpEntity = new HttpEntity<>(headers);
            String url = properties.promotionServiceUrl() + "/api/promotions/" + isbn;
            ResponseEntity<Promotion> response =
                    restTemplate.exchange(url, HttpMethod.GET, httpEntity, Promotion.class);
            return Optional.ofNullable(response.getBody());
        } catch (RuntimeException e) {
            log.error("Error while fetching promotion for isbn: " + isbn, e);
            return Optional.empty();
        }
    }
}

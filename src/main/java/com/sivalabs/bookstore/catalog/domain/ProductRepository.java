package com.sivalabs.bookstore.catalog.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByCode(String code);

    Page<Product> findAllBy(TextCriteria searchCriteria, Pageable page);

    boolean existsProductByCode(String code);
}

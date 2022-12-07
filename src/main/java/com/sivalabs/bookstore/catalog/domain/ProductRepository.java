package com.sivalabs.bookstore.catalog.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByIsbn(String isbn);
}

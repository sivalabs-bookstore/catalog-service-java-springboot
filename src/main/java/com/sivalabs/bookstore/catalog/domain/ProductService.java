package com.sivalabs.bookstore.catalog.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private static final int PAGE_SIZE = 20;

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final MongoTemplate mongoTemplate;

    public ProductService(
            ProductRepository productRepository,
            ProductMapper productMapper,
            MongoTemplate mongoTemplate) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.mongoTemplate = mongoTemplate;
    }

    public PagedResult<ProductModel> getProducts(int pageNo) {
        int page = pageNo <= 1 ? 0 : pageNo - 1;
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.Direction.ASC, "name");
        Page<ProductModel> productsPage =
                productRepository.findAll(pageable).map(productMapper::toModel);
        return new PagedResult<>(productsPage);
    }

    public Optional<ProductModel> getProductByCode(String code) {
        return productRepository.findByCode(code).map(productMapper::toModel);
    }

    public Optional<Product> deleteProduct(String code) {
        Query query = new Query(Criteria.where("code").is(code));
        Update update = new Update().set("deleted", Boolean.TRUE);
        return Optional.ofNullable(mongoTemplate.findAndModify(query, update, Product.class));
    }
}

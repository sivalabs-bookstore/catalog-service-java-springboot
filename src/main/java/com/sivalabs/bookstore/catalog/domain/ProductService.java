package com.sivalabs.bookstore.catalog.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Term;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

import jakarta.websocket.Decoder.Text;

@Service
public class ProductService {
    private static final int PAGE_SIZE = 20;

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
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

    public Page<Product> searchProductsByCriteria(String searchCriteria, int page) {
        return productRepository.findAllBy(textCriteriaMatching(searchCriteria), pageableOf(page));
    }

    private TextCriteria textCriteriaMatching (String searchCriteria){
        return new TextCriteria().matching (searchCriteria);
    }

    private Pageable pageableOf (int page){
        return Pageable.ofSize(page);
    }
}

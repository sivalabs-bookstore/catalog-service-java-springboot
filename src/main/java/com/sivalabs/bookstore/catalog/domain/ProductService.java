package com.sivalabs.bookstore.catalog.domain;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

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
        int page = getRepositoryPageNo(pageNo);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.Direction.ASC, "name");
        Page<ProductModel> productsPage =
                productRepository.findAll(pageable).map(productMapper::toModel);
        return new PagedResult<>(productsPage);
    }

    private int getRepositoryPageNo(int pageNo) {
        return pageNo <= 1 ? 0 : pageNo - 1;
    }

    public Optional<ProductModel> getProductByCode(String code) {
        return productRepository.findByCode(code).map(productMapper::toModel);
    }

    public PagedResult<ProductModel> searchProductsByCriteria(String searchCriteria, int page) {
        Page<ProductModel> productPage =
                productRepository
                        .findAllBy(
                                textCriteriaMatching(searchCriteria),
                                pageableSortOf(getRepositoryPageNo(page)))
                        .map(productMapper::toModel);
        return PagedResult.fromPage(productPage);
    }

    private TextCriteria textCriteriaMatching(String searchCriteria) {
        return new TextCriteria().matchingPhrase(searchCriteria);
    }

    private Pageable pageableSortOf(int page) {
        return PageRequest.of(page, PAGE_SIZE, Sort.Direction.ASC, "name");
    }
}

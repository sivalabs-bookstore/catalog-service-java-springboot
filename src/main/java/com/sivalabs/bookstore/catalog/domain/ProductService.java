package com.sivalabs.bookstore.catalog.domain;

import com.sivalabs.bookstore.catalog.clients.promotions.Promotion;
import com.sivalabs.bookstore.catalog.clients.promotions.PromotionServiceClient;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private static final int PAGE_SIZE = 20;

    private final PromotionServiceClient promotionServiceClient;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public PagedResult<ProductModel> getProducts(int pageNo) {
        int page = pageNo <= 1 ? 0 : pageNo - 1;
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.Direction.ASC, "name");
        Page<Product> productsPage = productRepository.findAll(pageable);
        var productsWithDiscount = applyPromotionDiscount(productsPage);
        return new PagedResult<>(productsWithDiscount);
    }

    public Optional<ProductModel> getProductByCode(String code) {
        return productRepository
                .findByCode(code)
                .map(
                        product -> {
                            Optional<Promotion> promotion =
                                    promotionServiceClient.getPromotion(product.getCode());
                            return productMapper.toModel(product, promotion.orElse(null));
                        });
    }

    private Page<ProductModel> applyPromotionDiscount(Page<Product> productsPage) {
        List<String> productCodes =
                productsPage.getContent().stream().map(Product::getCode).toList();
        List<Promotion> promotions = promotionServiceClient.getPromotions(productCodes);
        Map<String, Promotion> promotionsMap =
                promotions.stream()
                        .collect(
                                Collectors.toMap(
                                        Promotion::getProductCode, promotion -> promotion));
        return productsPage.map(
                product -> productMapper.toModel(product, promotionsMap.get(product.getCode())));
    }
}

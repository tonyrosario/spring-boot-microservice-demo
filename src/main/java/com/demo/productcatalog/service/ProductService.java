package com.demo.productcatalog.service;

import com.demo.productcatalog.domain.MoneyEmbeddable;
import com.demo.productcatalog.domain.Product;
import com.demo.productcatalog.domain.exception.DuplicateSkuException;
import com.demo.productcatalog.domain.exception.ProductNotFoundException;
import com.demo.productcatalog.dto.ProductRequest;
import com.demo.productcatalog.dto.ProductResponse;
import com.demo.productcatalog.repository.ProductRepository;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException(request.getSku());
        }
        Product product = toEntity(request);
        return toResponse(productRepository.save(product));
    }

    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAllByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setPrice(toMoneyEmbeddable(request));
        return toResponse(productRepository.save(product));
    }

    public void deactivate(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        product.setActive(false);
        productRepository.save(product);
    }

    private Product toEntity(ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .sku(request.getSku())
                .price(toMoneyEmbeddable(request))
                .build();
    }

    private MoneyEmbeddable toMoneyEmbeddable(ProductRequest request) {
        return MoneyEmbeddable.of(Money.of(request.getPriceAmount(), request.getPriceCurrency()));
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .sku(product.getSku())
                .priceAmount(product.getPrice().getAmount())
                .priceCurrency(product.getPrice().getCurrency())
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}

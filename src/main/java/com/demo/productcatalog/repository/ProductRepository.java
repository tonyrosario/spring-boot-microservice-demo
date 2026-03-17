package com.demo.productcatalog.repository;

import com.demo.productcatalog.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySku(String sku);

    Optional<Product> findBySkuAndActiveTrue(String sku);

    List<Product> findAllByActiveTrue();
}

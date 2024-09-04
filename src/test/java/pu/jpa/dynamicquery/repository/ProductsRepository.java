package pu.jpa.dynamicquery.repository;

import org.springframework.stereotype.Repository;
import pu.jpa.dynamicquery.model.entity.Product;
import pu.jpa.dynamicquery.model.projection.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Long>, SearchableRepository<ProductProjection> {

    @Query(value = "SELECT new pu.jpa.dquery.model.projection.ProductProjection(p.is,p.name,p.createdAt,s.id, s.name) FROM products p INNER JOIN substances s ON p.substanceId = s.id",
        nativeQuery = true)
    Page<ProductProjection> search(Pageable pageable);

}


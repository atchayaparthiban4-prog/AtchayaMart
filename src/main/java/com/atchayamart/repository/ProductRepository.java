package com.atchayamart.repository;

import com.atchayamart.model.Category;
import com.atchayamart.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByName(String name);

    List<Product> findBySellerIdOrderByCreatedAtDesc(Long sellerId);

    List<Product> findByCategory(Category category);

    List<Product> findByCategoryOrderByNameAsc(Category category);

    List<Product> findByDiscountPercentGreaterThan(int discountPercent);

    @Query("select p from Product p "
            + "where p.stock > 0 and p.stock <= p.minStock "
            + "order by p.stock asc")
    List<Product> findLowStock();

    @Query("select p from Product p "
            + "where p.stock = 0 "
            + "order by p.name asc")
    List<Product> findOutOfStock();

    @Query("select p from Product p "
            + "where (:category is null or p.category = :category) "
            + "and (lower(p.name) like lower(concat('%', :q, '%')) "
            + "or lower(p.description) like lower(concat('%', :q, '%'))) "
            + "order by p.createdAt desc")
    List<Product> search(@Param("category") Category category, @Param("q") String q);
}
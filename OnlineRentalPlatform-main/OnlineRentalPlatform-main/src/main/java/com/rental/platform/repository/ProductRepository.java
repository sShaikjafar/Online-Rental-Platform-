package com.rental.platform.repository;

import com.rental.platform.model.Product;
import com.rental.platform.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByApprovedTrueAndStatus(Product.ProductStatus status, Pageable pageable);
    
    Page<Product> findByOwner(User owner, Pageable pageable);
    
    Page<Product> findByApprovedFalse(Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.approved = true AND p.status = :status " +
    	       "AND p.category.id = :categoryId ORDER BY p.createdAt DESC")
    	Page<Product> findByApprovedTrueAndStatusAndCategoryId(
    	        @Param("status") Product.ProductStatus status,
    	        @Param("categoryId") Long categoryId,
    	        Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.approved = true AND p.status = :status " +
           "AND p.pricePerDay BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByApprovedTrueAndStatusAndPricePerDayBetween(
            @Param("status") Product.ProductStatus status,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.approved = true AND p.status = :status " +
           "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> findByApprovedTrueAndStatusAndNameContainingIgnoreCase(
            @Param("status") Product.ProductStatus status,
            @Param("keyword") String keyword,
            Pageable pageable);
    
    @Query(value = "SELECT * FROM products p " +
    	       "WHERE p.approved = true AND p.status = ?4 " +
    	       "AND (6371 * acos(cos(radians(?1)) * cos(radians(p.latitude)) * " +
    	       "cos(radians(p.longitude) - radians(?2)) + sin(radians(?1)) * " +
    	       "sin(radians(p.latitude)))) <= ?3 " +
    	       "ORDER BY (6371 * acos(cos(radians(?1)) * cos(radians(p.latitude)) * " +
    	       "cos(radians(p.longitude) - radians(?2)) + sin(radians(?1)) * " +
    	       "sin(radians(p.latitude))))", nativeQuery = true)
    	List<Product> findNearbyProducts(Double latitude, Double longitude, Double distance, String status);
    
    @Query("SELECT p FROM Product p WHERE p.owner = :owner ORDER BY p.createdAt DESC")
    Page<Product> findByOwnerOrderByCreatedAtDesc(@Param("owner") User owner, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.owner.id = :ownerId ORDER BY p.createdAt DESC")
    Page<Product> findByOwnerId(@Param("ownerId") Long ownerId, Pageable pageable);
    
    @Query("SELECT p.id, p.name, c.id as categoryId, c.name as categoryName, p.approved, p.status " +
    	       "FROM Product p LEFT JOIN p.category c WHERE p.approved = true")
    	List<Object[]> findAllProductsWithCategoryInfo();
}
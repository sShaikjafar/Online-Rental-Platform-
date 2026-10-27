package com.rental.platform.repository;

import com.rental.platform.model.Order;
import com.rental.platform.model.User;
import com.rental.platform.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser(User user, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.product.owner = :owner")
    Page<Order> findByProductOwner(@Param("owner") User owner, Pageable pageable);
    
    @Query("SELECT o FROM Order o WHERE o.product = :product AND o.status IN :statuses " +
           "AND ((o.startDate <= :endDate) AND (o.endDate >= :startDate))")
    List<Order> findConflictingOrders(@Param("product") Product product,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate,
                                    @Param("statuses") List<Order.OrderStatus> statuses);
    
    List<Order> findByStatus(Order.OrderStatus status);
}
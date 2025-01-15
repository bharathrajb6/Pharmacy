package com.example.Pharmacy.repo;

import com.example.Pharmacy.model.OrderStatus;
import com.example.Pharmacy.model.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {

    /**
     * This method is used to get the orders by username
     *
     * @param username
     * @param pageable
     * @return
     */
    @Query("SELECT o from Orders o where o.username = ?1")
    Page<Orders> findByUsername(String username, Pageable pageable);

    /**
     * This method is used to get all the orders
     *
     * @param pageable
     * @return
     */
    Page<Orders> findAll(Pageable pageable);

    /**
     * This method is used to cancel the order
     *
     * @param status
     * @param orderID
     */
    @Modifying
    @Transactional
    @Query("UPDATE Orders o SET o.orderStatus = ?1 where o.orderID = ?2")
    void cancelOrder(OrderStatus status, String orderID);

    /**
     * This method is used to get all the cancelled orders
     *
     * @param orderStatus
     * @param pageable
     * @return
     */
    @Query("SELECT o from Orders o where o.orderStatus = ?1")
    Page<Orders> getAllCancelledOrders(OrderStatus orderStatus, Pageable pageable);

    /**
     * This method is used to get all the cancelled orders by username
     *
     * @param orderStatus
     * @param username
     * @param pageable
     * @return
     */
    @Query("SELECT o from Orders o where o.orderStatus = ?1 and o.username = ?2")
    Page<Orders> getAllCancelledOrdersByUsername(OrderStatus orderStatus, String username, Pageable pageable);
}

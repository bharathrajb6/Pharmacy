package com.example.Pharmacy.repo;

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

    Page<Orders> findByUsername(String username, Pageable pageable);

    Page<Orders> findAll(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Orders o SET o.orderStatus = ?1 where o.orderID = ?2")
    void cancelOrder(String status, String orderID);

}

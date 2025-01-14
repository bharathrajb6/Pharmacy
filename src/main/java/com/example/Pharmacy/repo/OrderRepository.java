package com.example.Pharmacy.repo;

import com.example.Pharmacy.model.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {

    Page<Orders> findByUsername(String username, Pageable pageable);

}

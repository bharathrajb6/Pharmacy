package com.example.Pharmacy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Orders {

    @Id
    @Column(name = "order_id")
    private String orderID;

    @Column(name = "username")
    private String username;

    @Column(name = "total_amount")
    private double totalAmount;

    @Column(name = "order_status")
    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "ordered_date")
    private LocalDate orderedDate;

    @OneToMany(mappedBy = "order")
    private List<MedicationQuantity> medicationQuantityList;
}

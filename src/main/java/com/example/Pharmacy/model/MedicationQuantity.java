package com.example.Pharmacy.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medication_quantity")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicationQuantity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "batch_number")
    private String batchNumber;

    @Column(name = "quantity")
    private int quantity;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;
}

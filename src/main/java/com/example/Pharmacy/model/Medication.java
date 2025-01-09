package com.example.Pharmacy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "medication")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Medication {
    @Id
    @Column(name = "medication_id")
    private int medicationID;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private double price;

    @OneToMany(mappedBy = "medication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Batch> batches;
}

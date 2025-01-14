package com.example.Pharmacy.repo;

import com.example.Pharmacy.model.MedicationQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationQuantityRepository extends JpaRepository<MedicationQuantity, Integer> {
}

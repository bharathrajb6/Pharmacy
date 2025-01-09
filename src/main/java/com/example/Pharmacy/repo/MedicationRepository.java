package com.example.Pharmacy.repo;

import com.example.Pharmacy.model.Medication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Integer> {

    Page<Medication> findAll(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Medication m SET m.name = ?1, m.description = ?2, m.manufacturer = ?3, m.price = ?4, m.stockQuantity = ?5, m.manufacturedDate = ?6, m.expiryDate = ?7 where m.medicationID = ?8")
    void updateMedicationDetails(String name, String description, String manufacturer, int price, int stock, Date manufacturedDate, Date expiryDate, int medicationID);
}

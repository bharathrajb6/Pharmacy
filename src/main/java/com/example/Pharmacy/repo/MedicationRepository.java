package com.example.Pharmacy.repo;

import com.example.Pharmacy.model.Medication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Integer> {

    /**
     * Find all medications
     *
     * @param pageable
     * @return
     */
    Page<Medication> findAll(Pageable pageable);

    /**
     * Update medication details
     *
     * @param name
     * @param description
     * @param price
     * @param medicationID
     */
    @Modifying
    @Transactional
    @Query("UPDATE Medication m SET m.name = ?1, m.description = ?2, m.price = ?3 where m.medicationID = ?4")
    void updateMedicationDetails(String name, String description, double price, int medicationID);
}

package com.example.Pharmacy.repo;

import com.example.Pharmacy.model.Batch;
import com.example.Pharmacy.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

    /**
     * Find a batch by its batch number
     *
     * @param batchNumber
     * @return
     */
    Optional<Batch> findByBatchNumber(String batchNumber);

    /**
     * Get all batches for a medication
     *
     * @param medication
     * @return
     */
    @Query("select b from Batch b where b.medication = ?1")
    List<Batch> getAllBatchesForMedication(Medication medication);

    @Query("select b from Batch b where b.expiryDate = ?1")
    List<Batch> getBatchesByExpiryDate(LocalDate expiryDate);
}

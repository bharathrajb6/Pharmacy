package com.example.Pharmacy.repo;

import com.example.Pharmacy.model.Batch;
import com.example.Pharmacy.model.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

    Optional<Batch> findByBatchNumber(String batchNumber);

    @Query("select b from Batch b where b.medication = ?1")
    List<Batch> getAllBatchesForMedication(Medication medication);
}

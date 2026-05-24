package com.hospital.api.repository;

import com.hospital.api.model.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<PatientEntity, Integer> {
}

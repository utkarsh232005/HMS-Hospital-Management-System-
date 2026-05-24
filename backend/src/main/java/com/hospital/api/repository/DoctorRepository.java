package com.hospital.api.repository;

import com.hospital.api.model.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<DoctorEntity, Integer> {
}

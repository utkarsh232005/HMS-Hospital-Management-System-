package com.hospital.api.repository;

import com.hospital.api.model.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Integer> {
}

package com.hospital.api;

import com.hospital.api.model.AppointmentEntity;
import com.hospital.api.model.DoctorEntity;
import com.hospital.api.model.PatientEntity;
import com.hospital.api.repository.AppointmentRepository;
import com.hospital.api.repository.DoctorRepository;
import com.hospital.api.repository.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(PatientRepository patients, DoctorRepository doctors,
            AppointmentRepository appointments) {
        return args -> {
            if (patients.count() == 0 && doctors.count() == 0 && appointments.count() == 0) {
                patients.save(patient(100, "Amara Kapoor", 34, "Cardiology", "Hypertension", "ADMITTED", "Dr. Iyer",
                        "2024-05-10"));
                patients.save(patient(101, "Ravi Sharma", 58, "ICU", "Cardiac Arrest", "CRITICAL", "Dr. Mehta",
                        "2024-05-18"));
                patients.save(patient(102, "Priya Mehta", 27, "Orthopedics", "Fracture - Tibia", "SURGERY",
                        "Dr. Kulkarni", "2024-05-17"));
                patients.save(patient(103, "Nikhil Joshi", 45, "Neurology", "Chronic Migraine", "OBSERVATION",
                        "Dr. Verma", "2024-05-19"));
                patients.save(patient(104, "Sunita Rao", 62, "General", "Diabetes - T2", "DISCHARGED", "Dr. Thomas",
                        "2024-05-05"));

                doctors.save(doctor(200, "Dr. Anand Iyer", "Cardiology", "+91-9823001122", "a.iyer@medicore.in", 12,
                        "Mon-Fri"));
                doctors.save(doctor(201, "Dr. Sonal Mehta", "ICU / CCU", "+91-9823002233", "s.mehta@medicore.in", 8,
                        "24/7 On-call"));
                doctors.save(doctor(202, "Dr. Prakash Kulkarni", "Orthopedics", "+91-9823003344",
                        "p.kulkarni@medicore.in", 10, "Mon-Sat"));
                doctors.save(doctor(203, "Dr. Arvind Verma", "Neurology", "+91-9823004455", "a.verma@medicore.in", 9,
                        "Tue-Sat"));

                appointments.save(appointment(300, "Fatima Sheikh", "Dr. Iyer", "Cardiology", "2024-05-21", "09:00",
                        "SCHEDULED"));
                appointments.save(appointment(301, "Rohan Patil", "Dr. Kulkarni", "Ophthalmology", "2024-05-21",
                        "10:30", "IN_PROGRESS"));
                appointments.save(appointment(302, "Deepa Nair", "Dr. Thomas", "Orthopedics", "2024-05-21", "12:00",
                        "SCHEDULED"));
                appointments.save(
                        appointment(303, "Arjun Singh", "Dr. Rao", "General", "2024-05-21", "14:15", "SCHEDULED"));
                appointments.save(appointment(304, "Meena Krishnan", "Dr. Verma", "Neurology", "2024-05-21", "16:00",
                        "SCHEDULED"));
            }
        };
    }

    private PatientEntity patient(int id, String name, int age, String ward, String diagnosis, String status,
            String doctor, String admissionDate) {
        PatientEntity patient = new PatientEntity();
        patient.setId(id);
        patient.setName(name);
        patient.setAge(age);
        patient.setWard(ward);
        patient.setDiagnosis(diagnosis);
        patient.setStatus(status);
        patient.setDoctor(doctor);
        patient.setAdmissionDate(admissionDate);
        return patient;
    }

    private DoctorEntity doctor(int id, String name, String specialization, String phone, String email,
            int patientsCount, String availability) {
        DoctorEntity doctor = new DoctorEntity();
        doctor.setId(id);
        doctor.setName(name);
        doctor.setSpecialization(specialization);
        doctor.setPhone(phone);
        doctor.setEmail(email);
        doctor.setPatientsCount(patientsCount);
        doctor.setAvailability(availability);
        return doctor;
    }

    private AppointmentEntity appointment(int id, String patientName, String doctorName, String department, String date,
            String time, String status) {
        AppointmentEntity appointment = new AppointmentEntity();
        appointment.setId(id);
        appointment.setPatientName(patientName);
        appointment.setDoctorName(doctorName);
        appointment.setDepartment(department);
        appointment.setDate(date);
        appointment.setTime(time);
        appointment.setStatus(status);
        return appointment;
    }
}

package com.hospital.appointment;

import com.hospital.appointment.model.AppointmentEntity;
import com.hospital.appointment.repository.AppointmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(AppointmentRepository appointments) {
        return args -> {
            if (appointments.count() == 0) {
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

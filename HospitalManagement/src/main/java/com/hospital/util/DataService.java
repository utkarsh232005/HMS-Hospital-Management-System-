package com.hospital.util;

import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataService {

    private static DataService instance;
    private final ObservableList<Patient> patients = FXCollections.observableArrayList();
    private final ObservableList<Doctor> doctors = FXCollections.observableArrayList();
    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    private int nextPatientId = 100;
    private int nextDoctorId = 200;
    private int nextApptId = 300;

    private DataService() {
        seedData();
    }

    public static DataService getInstance() {
        if (instance == null) instance = new DataService();
        return instance;
    }

    private void seedData() {
        patients.addAll(
            new Patient(nextPatientId++, "Amara Kapoor",    34, "Cardiology",   "Hypertension",       Patient.Status.ADMITTED,     "Dr. Iyer",     "2024-05-10"),
            new Patient(nextPatientId++, "Ravi Sharma",     58, "ICU",          "Cardiac Arrest",     Patient.Status.CRITICAL,     "Dr. Mehta",    "2024-05-18"),
            new Patient(nextPatientId++, "Priya Mehta",     27, "Orthopedics",  "Fracture – Tibia",   Patient.Status.SURGERY,      "Dr. Kulkarni", "2024-05-17"),
            new Patient(nextPatientId++, "Nikhil Joshi",    45, "Neurology",    "Chronic Migraine",   Patient.Status.OBSERVATION,  "Dr. Verma",    "2024-05-19"),
            new Patient(nextPatientId++, "Sunita Rao",      62, "General",      "Diabetes – T2",      Patient.Status.DISCHARGED,   "Dr. Thomas",   "2024-05-05"),
            new Patient(nextPatientId++, "Arjun Singh",     39, "Cardiology",   "Arrhythmia",         Patient.Status.ADMITTED,     "Dr. Iyer",     "2024-05-15"),
            new Patient(nextPatientId++, "Fatima Sheikh",   51, "Pulmonology",  "Asthma – Severe",    Patient.Status.ADMITTED,     "Dr. Rao",      "2024-05-12"),
            new Patient(nextPatientId++, "Deepa Nair",      33, "Gynecology",   "Prenatal Check",     Patient.Status.OBSERVATION,  "Dr. Pillai",   "2024-05-20"),
            new Patient(nextPatientId++, "Meena Krishnan",  47, "Neurology",    "Epilepsy",           Patient.Status.ADMITTED,     "Dr. Verma",    "2024-05-14"),
            new Patient(nextPatientId++, "Rohan Patil",     29, "Ophthalmology","Retinal Detachment", Patient.Status.SURGERY,      "Dr. Kulkarni", "2024-05-16")
        );

        doctors.addAll(
            new Doctor(nextDoctorId++, "Dr. Anand Iyer",      "Cardiology",   "+91-9823001122", "a.iyer@medicore.in",     12, "Mon–Fri"),
            new Doctor(nextDoctorId++, "Dr. Sonal Mehta",     "ICU / CCU",    "+91-9823002233", "s.mehta@medicore.in",    8,  "24/7 On-call"),
            new Doctor(nextDoctorId++, "Dr. Prakash Kulkarni","Orthopedics",  "+91-9823003344", "p.kulkarni@medicore.in", 10, "Mon–Sat"),
            new Doctor(nextDoctorId++, "Dr. Arvind Verma",    "Neurology",    "+91-9823004455", "a.verma@medicore.in",    9,  "Tue–Sat"),
            new Doctor(nextDoctorId++, "Dr. Grace Thomas",    "General Med.", "+91-9823005566", "g.thomas@medicore.in",   15, "Mon–Fri"),
            new Doctor(nextDoctorId++, "Dr. Suresh Rao",      "Pulmonology",  "+91-9823006677", "s.rao@medicore.in",      7,  "Mon–Thu"),
            new Doctor(nextDoctorId++, "Dr. Leena Pillai",    "Gynecology",   "+91-9823007788", "l.pillai@medicore.in",   11, "Mon–Fri")
        );

        appointments.addAll(
            new Appointment(nextApptId++, "Fatima Sheikh",   "Dr. Iyer",     "Cardiology",   "2024-05-21", "09:00", Appointment.AppStatus.SCHEDULED),
            new Appointment(nextApptId++, "Rohan Patil",    "Dr. Kulkarni", "Ophthalmology","2024-05-21", "10:30", Appointment.AppStatus.IN_PROGRESS),
            new Appointment(nextApptId++, "Deepa Nair",     "Dr. Thomas",   "Orthopedics",  "2024-05-21", "12:00", Appointment.AppStatus.SCHEDULED),
            new Appointment(nextApptId++, "Arjun Singh",    "Dr. Rao",      "General",      "2024-05-21", "14:15", Appointment.AppStatus.SCHEDULED),
            new Appointment(nextApptId++, "Meena Krishnan", "Dr. Verma",    "Neurology",    "2024-05-21", "16:00", Appointment.AppStatus.SCHEDULED),
            new Appointment(nextApptId++, "Amara Kapoor",   "Dr. Iyer",     "Cardiology",   "2024-05-22", "09:30", Appointment.AppStatus.SCHEDULED),
            new Appointment(nextApptId++, "Ravi Sharma",    "Dr. Mehta",    "ICU",          "2024-05-22", "11:00", Appointment.AppStatus.SCHEDULED),
            new Appointment(nextApptId++, "Sunita Rao",     "Dr. Thomas",   "General",      "2024-05-20", "10:00", Appointment.AppStatus.COMPLETED)
        );
    }

    public ObservableList<Patient> getPatients() { return patients; }
    public ObservableList<Doctor> getDoctors() { return doctors; }
    public ObservableList<Appointment> getAppointments() { return appointments; }

    public void addPatient(Patient p) { patients.add(p); }
    public void removePatient(Patient p) { patients.remove(p); }

    public void addDoctor(Doctor d) { doctors.add(d); }
    public void removeDoctor(Doctor d) { doctors.remove(d); }

    public void addAppointment(Appointment a) { appointments.add(a); }
    public void removeAppointment(Appointment a) { appointments.remove(a); }

    public int nextPatientId() { return nextPatientId++; }
    public int nextDoctorId()  { return nextDoctorId++; }
    public int nextApptId()    { return nextApptId++; }

    public long countByStatus(Patient.Status s) {
        return patients.stream().filter(p -> p.getStatus() == s).count();
    }
}

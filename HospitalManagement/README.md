# MediCore — JavaFX Hospital Management System

A fully functional JavaFX 21 desktop application for managing hospital operations.

## Features

| Module | Description |
|---|---|
| **Dashboard** | Live stats: total patients, admissions, appointments, critical cases |
| **Patients** | Full CRUD — add, edit, delete, filter by status or search by name |
| **Appointments** | Schedule and manage appointments with date/status filtering |
| **Doctors** | Doctor roster with specialization, contact info, availability |
| **Departments** | Visual capacity cards per department with patient counts |
| **Pharmacy** | Medication inventory overview |
| **Reports** | Donut chart (status), bar chart (ward load), summary statistics |

## Project Structure

```
HospitalManagement/
├── pom.xml
└── src/main/
    ├── java/com/hospital/
    │   ├── MainApp.java                   ← Entry point
    │   ├── model/
    │   │   ├── Patient.java
    │   │   ├── Doctor.java
    │   │   └── Appointment.java
    │   ├── util/
    │   │   └── DataService.java           ← In-memory data store + seed data
    │   └── view/
    │       ├── MainLayout.java            ← Sidebar + content router
    │       └── pages/
    │           ├── DashboardPage.java
    │           ├── PatientPage.java
    │           ├── AppointmentPage.java
    │           ├── DoctorPage.java
    │           ├── DepartmentPage.java
    │           ├── PharmacyPage.java
    │           └── ReportsPage.java
    └── resources/
        └── styles/
            └── app.css
```

## Requirements

- **Java 21** (or Java 17+)
- **Maven 3.8+**

## Run

```bash
# Clone / place project folder, then:
cd HospitalManagement
mvn javafx:run
```

## Build fat JAR

```bash
mvn clean package
java -jar target/hospital-management-1.0.0.jar
```

## Architecture Notes

- **JavaFX Properties** — All model fields use `Property<T>` for live TableView binding.
- **ObservableList** — `DataService` holds all data; tables auto-refresh on add/remove.
- **FilteredList** — Search and filter are zero-boilerplate wrappers over the live list.
- **Singleton DataService** — Single source of truth shared across all pages.
- **No FXML** — All UI is pure Java for portability and clarity.

## Extending

To add a database backend, replace `DataService.seedData()` with JDBC or JPA calls.
The `ObservableList` contract stays the same — the UI needs no changes.

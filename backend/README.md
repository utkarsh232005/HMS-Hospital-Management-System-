# MediCore Hospital Management System

A Spring Boot hospital management application with a migrated HTML, CSS, and JavaScript user interface.

## Features

| Module | Description |
|---|---|
| **Dashboard** | Live stats for patients, admissions, appointments, and critical cases |
| **Patients** | Create, edit, delete, search, and filter patient records |
| **Appointments** | Schedule and manage appointments with date filtering |
| **Doctors** | Manage doctor roster, specialization, contact info, and availability |
| **Departments** | View department capacity and patient distribution |
| **Pharmacy** | Medication inventory overview |
| **Reports** | Patient status and ward load analytics |

## Project Structure

```
HospitalManagement/
├── frontend/
│   ├── index.html
│   ├── styles.css
│   └── app.js
├── api/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/hospital/api/
│       │   ├── ApiApplication.java
│       │   ├── controller/
│       │   ├── model/
│       │   └── repository/
│       └── resources/
│           └── application.properties
```

## Requirements

- Java 17+
- Maven 3.8+
- MySQL running with the database configured in `api/src/main/resources/application.properties`

## Run

Start the backend:

```bash
cd api
mvn spring-boot:run
```

Start the frontend in another terminal:

```bash
cd frontend
python3 -m http.server 3000
```

Open:

```text
http://localhost:3000
```

The frontend calls the Spring Boot REST endpoints at `http://localhost:8080`:

- `GET/POST/PUT/DELETE /api/patients`
- `GET/POST/PUT/DELETE /api/doctors`
- `GET/POST/PUT/DELETE /api/appointments`

If your API runs on another port, open the frontend with an override:

```text
http://localhost:3000/?api=http://localhost:8081
```

## Build

```bash
cd api
mvn clean package
java -jar target/hospital-api-1.0.0.jar
```

## Notes

- The migrated UI is plain HTML, CSS, and JavaScript under `frontend`.
- The Spring Boot controllers, repositories, JPA entities, MySQL connection settings, and backend routes are preserved.
- No Docker files were changed.

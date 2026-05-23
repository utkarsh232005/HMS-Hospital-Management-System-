package com.hospital.model;

import javafx.beans.property.*;

public class Appointment {

    public enum AppStatus { SCHEDULED, COMPLETED, CANCELLED, IN_PROGRESS }

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty patientName = new SimpleStringProperty();
    private final StringProperty doctorName = new SimpleStringProperty();
    private final StringProperty department = new SimpleStringProperty();
    private final StringProperty date = new SimpleStringProperty();
    private final StringProperty time = new SimpleStringProperty();
    private final ObjectProperty<AppStatus> status = new SimpleObjectProperty<>();

    public Appointment(int id, String patientName, String doctorName,
                       String department, String date, String time, AppStatus status) {
        this.id.set(id);
        this.patientName.set(patientName);
        this.doctorName.set(doctorName);
        this.department.set(department);
        this.date.set(date);
        this.time.set(time);
        this.status.set(status);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getPatientName() { return patientName.get(); }
    public StringProperty patientNameProperty() { return patientName; }

    public String getDoctorName() { return doctorName.get(); }
    public StringProperty doctorNameProperty() { return doctorName; }

    public String getDepartment() { return department.get(); }
    public StringProperty departmentProperty() { return department; }

    public String getDate() { return date.get(); }
    public StringProperty dateProperty() { return date; }

    public String getTime() { return time.get(); }
    public StringProperty timeProperty() { return time; }

    public AppStatus getStatus() { return status.get(); }
    public ObjectProperty<AppStatus> statusProperty() { return status; }
    public void setStatus(AppStatus s) { status.set(s); }
}

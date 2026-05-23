package com.hospital.model;

import javafx.beans.property.*;

public class Patient {

    public enum Status { ADMITTED, DISCHARGED, CRITICAL, SURGERY, OBSERVATION }

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty age = new SimpleIntegerProperty();
    private final StringProperty ward = new SimpleStringProperty();
    private final StringProperty diagnosis = new SimpleStringProperty();
    private final ObjectProperty<Status> status = new SimpleObjectProperty<>();
    private final StringProperty doctor = new SimpleStringProperty();
    private final StringProperty admissionDate = new SimpleStringProperty();

    public Patient(int id, String name, int age, String ward,
                   String diagnosis, Status status, String doctor, String admissionDate) {
        this.id.set(id);
        this.name.set(name);
        this.age.set(age);
        this.ward.set(ward);
        this.diagnosis.set(diagnosis);
        this.status.set(status);
        this.doctor.set(doctor);
        this.admissionDate.set(admissionDate);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public int getAge() { return age.get(); }
    public IntegerProperty ageProperty() { return age; }

    public String getWard() { return ward.get(); }
    public StringProperty wardProperty() { return ward; }

    public String getDiagnosis() { return diagnosis.get(); }
    public StringProperty diagnosisProperty() { return diagnosis; }

    public Status getStatus() { return status.get(); }
    public ObjectProperty<Status> statusProperty() { return status; }
    public void setStatus(Status s) { status.set(s); }

    public String getDoctor() { return doctor.get(); }
    public StringProperty doctorProperty() { return doctor; }

    public String getAdmissionDate() { return admissionDate.get(); }
    public StringProperty admissionDateProperty() { return admissionDate; }
}

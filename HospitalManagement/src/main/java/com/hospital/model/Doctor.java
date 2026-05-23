package com.hospital.model;

import javafx.beans.property.*;

public class Doctor {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty specialization = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final IntegerProperty patientsCount = new SimpleIntegerProperty();
    private final StringProperty availability = new SimpleStringProperty();

    public Doctor(int id, String name, String specialization, String phone,
                  String email, int patientsCount, String availability) {
        this.id.set(id);
        this.name.set(name);
        this.specialization.set(specialization);
        this.phone.set(phone);
        this.email.set(email);
        this.patientsCount.set(patientsCount);
        this.availability.set(availability);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public String getSpecialization() { return specialization.get(); }
    public StringProperty specializationProperty() { return specialization; }

    public String getPhone() { return phone.get(); }
    public StringProperty phoneProperty() { return phone; }

    public String getEmail() { return email.get(); }
    public StringProperty emailProperty() { return email; }

    public int getPatientsCount() { return patientsCount.get(); }
    public IntegerProperty patientsCountProperty() { return patientsCount; }

    public String getAvailability() { return availability.get(); }
    public StringProperty availabilityProperty() { return availability; }
}

package com.hospital.controller;

import com.hospital.util.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class PatientController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField ageField;

    @FXML
    private TextField diseaseField;

    @FXML
    public void savePatient() {

        try {

            String name = nameField.getText();

            int age =
                    Integer.parseInt(ageField.getText());

            String disease =
                    diseaseField.getText();

            Connection conn =
                    DBConnection.getConnection();

            String query =
                    "INSERT INTO patients(name, age, disease) VALUES(?,?,?)";

            PreparedStatement ps =
                    conn.prepareStatement(query);

            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, disease);

            ps.executeUpdate();

            System.out.println("Patient Saved!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
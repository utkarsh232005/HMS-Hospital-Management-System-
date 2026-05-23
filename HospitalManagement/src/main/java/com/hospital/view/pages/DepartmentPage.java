package com.hospital.view.pages;

import com.hospital.util.DataService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class DepartmentPage extends VBox {

    private final DataService ds = DataService.getInstance();

    public DepartmentPage() {
        setSpacing(20);
        setPadding(new Insets(24));
        setStyle("-fx-background-color: #f5f4f0;");

        Label title = new Label("Departments");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

        Label sub = new Label("Patient distribution across hospital departments");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

        getChildren().addAll(title, sub, buildGrid());
    }

    private FlowPane buildGrid() {
        FlowPane flow = new FlowPane();
        flow.setHgap(14); flow.setVgap(14);

        // Count patients per ward/department
        Map<String, long[]> depts = new LinkedHashMap<>();
        String[][] deptData = {
            {"Cardiology",   "#185FA5", "#E6F1FB"},
            {"ICU",          "#A32D2D", "#FCEBEB"},
            {"Orthopedics",  "#633806", "#FAEEDA"},
            {"Neurology",    "#3C3489", "#EEEDFE"},
            {"General",      "#3B6D11", "#EAF3DE"},
            {"Pulmonology",  "#0F6E56", "#E1F5EE"},
            {"Gynecology",   "#993556", "#FBEAF0"},
            {"Ophthalmology","#185FA5", "#E6F1FB"}
        };

        for (String[] d : deptData) {
            String name = d[0];
            long count = ds.getPatients().stream()
                .filter(p -> p.getWard().equalsIgnoreCase(name)).count();
            long doctorCount = ds.getDoctors().stream()
                .filter(doc -> doc.getSpecialization().toLowerCase().contains(name.toLowerCase())).count();
            flow.getChildren().add(deptCard(name, (int) count, (int) doctorCount, d[1], d[2]));
        }
        return flow;
    }

    private VBox deptCard(String name, int patients, int doctors, String accent, String bg) {
        VBox card = new VBox(10);
        card.setPrefWidth(220);
        card.setPadding(new Insets(18));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 12; " +
                "-fx-border-color: #e0ddd5; -fx-border-radius: 12; -fx-border-width: 1;");

        Label icon = new Label("🏥");
        icon.setStyle("-fx-font-size: 22px;");

        Label deptName = new Label(name);
        deptName.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

        HBox stats = new HBox(16);
        stats.setAlignment(Pos.CENTER_LEFT);

        VBox patBox = new VBox(1);
        Label patCount = new Label(String.valueOf(patients));
        patCount.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + accent + ";");
        Label patLabel = new Label("patients");
        patLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");
        patBox.getChildren().addAll(patCount, patLabel);

        VBox docBox = new VBox(1);
        Label docCount = new Label(String.valueOf(doctors));
        docCount.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #444;");
        Label docLabel = new Label("doctors");
        docLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");
        docBox.getChildren().addAll(docCount, docLabel);

        stats.getChildren().addAll(patBox, docBox);

        // Capacity bar background
        StackPane barBg = new StackPane();
        barBg.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 4;");
        barBg.setMinHeight(6); barBg.setMaxHeight(6);

        double pct = Math.min(1.0, patients / 15.0);
        HBox barFill = new HBox();
        barFill.setStyle("-fx-background-color: " + accent + "; -fx-background-radius: 4;");
        barFill.setMaxHeight(6);
        StackPane.setAlignment(barFill, Pos.CENTER_LEFT);

        // We fake the bar width with a Region
        Region fill = new Region();
        fill.setPrefWidth(200 * pct);
        fill.setPrefHeight(6);
        fill.setStyle("-fx-background-color: " + accent + "; -fx-background-radius: 4;");

        barBg.getChildren().add(fill);
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);

        Label capLbl = new Label(String.format("%.0f%% capacity", pct * 100));
        capLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");

        card.getChildren().addAll(icon, deptName, stats, barBg, capLbl);
        return card;
    }
}

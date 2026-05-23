package com.hospital.view.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class PharmacyPage extends VBox {
    public PharmacyPage() {
        setSpacing(16);
        setPadding(new Insets(24));
        setStyle("-fx-background-color: #f5f4f0;");
        setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Pharmacy");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

        FlowPane meds = new FlowPane();
        meds.setHgap(14); meds.setVgap(14);

        String[][] inventory = {
            {"Paracetamol 500mg",  "Analgesic",    "840 units", "#EAF3DE", "#3B6D11"},
            {"Amoxicillin 250mg",  "Antibiotic",   "320 units", "#E6F1FB", "#185FA5"},
            {"Metformin 500mg",    "Antidiabetic", "512 units", "#EEEDFE", "#3C3489"},
            {"Atenolol 50mg",      "Beta-blocker", "275 units", "#FAEEDA", "#633806"},
            {"Omeprazole 20mg",    "PPI",          "430 units", "#E1F5EE", "#0F6E56"},
            {"Aspirin 75mg",       "Antiplatelet", "980 units", "#FCEBEB", "#791F1F"},
            {"Amlodipine 5mg",     "CCB",          "190 units", "#FBEAF0", "#72243E"},
            {"Cetirizine 10mg",    "Antihistamine","650 units", "#E6F1FB", "#0C447C"},
        };

        for (String[] m : inventory) {
            VBox card = new VBox(6);
            card.setPrefWidth(180);
            card.setPadding(new Insets(14));
            card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; " +
                    "-fx-border-color: #e0ddd5; -fx-border-radius: 10; -fx-border-width: 1;");

            Label name = new Label(m[0]);
            name.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
            name.setWrapText(true);

            Label cat = new Label(m[1]);
            cat.setStyle("-fx-background-color: " + m[3] + "; -fx-text-fill: " + m[4] +
                    "; -fx-background-radius: 6; -fx-padding: 2 7 2 7; -fx-font-size: 10px;");

            Label stock = new Label("📦  " + m[2]);
            stock.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");

            card.getChildren().addAll(name, cat, stock);
            meds.getChildren().add(card);
        }

        getChildren().addAll(title, meds);
    }
}

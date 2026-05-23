package com.hospital.view.pages;

import com.hospital.model.Patient;
import com.hospital.util.DataService;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Map;
import java.util.TreeMap;

public class ReportsPage extends VBox {

    private final DataService ds = DataService.getInstance();

    public ReportsPage() {
        setSpacing(20);
        setPadding(new Insets(24));
        setStyle("-fx-background-color: #f5f4f0;");

        Label title = new Label("Reports & Analytics");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

        HBox charts = new HBox(16);
        charts.getChildren().addAll(statusChart(), wardChart());

        getChildren().addAll(title, charts, summaryTable());
    }

    private VBox statusChart() {
        VBox card = card("Patient Status Breakdown");

        Canvas canvas = new Canvas(320, 220);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        long[] counts = {
            ds.countByStatus(Patient.Status.ADMITTED),
            ds.countByStatus(Patient.Status.CRITICAL),
            ds.countByStatus(Patient.Status.SURGERY),
            ds.countByStatus(Patient.Status.OBSERVATION),
            ds.countByStatus(Patient.Status.DISCHARGED)
        };
        Color[] colors = {
            Color.web("#185FA5"), Color.web("#A32D2D"), Color.web("#D85A30"),
            Color.web("#7F77DD"), Color.web("#3B6D11")
        };
        String[] labels = {"Admitted", "Critical", "Surgery", "Observation", "Discharged"};
        long total = ds.getPatients().size();

        double startAngle = 0;
        double cx = 100, cy = 110, r = 80;

        for (int i = 0; i < counts.length; i++) {
            double arc = 360.0 * counts[i] / total;
            gc.setFill(colors[i]);
            gc.fillArc(cx - r, cy - r, r * 2, r * 2, startAngle, arc, javafx.scene.shape.ArcType.ROUND);
            startAngle += arc;
        }

        // Center hole for donut
        gc.setFill(Color.WHITE);
        gc.fillOval(cx - 40, cy - 40, 80, 80);

        gc.setFill(Color.web("#1a1a1a"));
        gc.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 16));
        gc.fillText(String.valueOf(total), cx - 10, cy + 6);
        gc.setFont(javafx.scene.text.Font.font("System", 10));
        gc.setFill(Color.web("#888"));
        gc.fillText("patients", cx - 17, cy + 18);

        // Legend
        double lx = 210, ly = 40;
        for (int i = 0; i < labels.length; i++) {
            gc.setFill(colors[i]);
            gc.fillRoundRect(lx, ly + i * 28, 12, 12, 3, 3);
            gc.setFill(Color.web("#1a1a1a"));
            gc.setFont(javafx.scene.text.Font.font("System", 12));
            gc.fillText(labels[i], lx + 18, ly + i * 28 + 11);
            gc.setFill(Color.web("#888"));
            gc.setFont(javafx.scene.text.Font.font("System", 10));
            gc.fillText(counts[i] + " patients", lx + 18, ly + i * 28 + 22);
        }

        card.getChildren().add(canvas);
        return card;
    }

    private VBox wardChart() {
        VBox card = card("Patients per Ward");

        Canvas canvas = new Canvas(320, 220);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Map<String, Long> wardCounts = new TreeMap<>();
        ds.getPatients().forEach(p -> wardCounts.merge(p.getWard(), 1L, Long::sum));

        String[] wards = wardCounts.keySet().toArray(new String[0]);
        long[] vals    = wardCounts.values().stream().mapToLong(Long::longValue).toArray();
        long   maxVal  = wardCounts.values().stream().mapToLong(Long::longValue).max().orElse(1);

        Color[] barColors = {
            Color.web("#185FA5"), Color.web("#3B6D11"), Color.web("#A32D2D"),
            Color.web("#633806"), Color.web("#3C3489"), Color.web("#0F6E56"),
            Color.web("#993556"), Color.web("#0C447C")
        };

        double barW = 28, gap = 10, startX = 30, chartH = 160, baseY = 190;

        for (int i = 0; i < wards.length; i++) {
            double barH = (double) vals[i] / maxVal * chartH;
            double x = startX + i * (barW + gap);
            gc.setFill(barColors[i % barColors.length]);
            gc.fillRoundRect(x, baseY - barH, barW, barH, 4, 4);

            gc.setFill(Color.web("#1a1a1a"));
            gc.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 10));
            gc.fillText(String.valueOf(vals[i]), x + 8, baseY - barH - 4);

            gc.setFill(Color.web("#888"));
            gc.setFont(javafx.scene.text.Font.font("System", 8));
            String wLabel = wards[i].length() > 6 ? wards[i].substring(0, 6) : wards[i];
            gc.fillText(wLabel, x + 2, baseY + 12);
        }

        // Baseline
        gc.setStroke(Color.web("#e0ddd5"));
        gc.setLineWidth(1);
        gc.strokeLine(20, baseY, 310, baseY);

        card.getChildren().add(canvas);
        return card;
    }

    private VBox summaryTable() {
        VBox card = card("Summary Statistics");

        GridPane grid = new GridPane();
        grid.setHgap(40); grid.setVgap(10);
        grid.setPadding(new Insets(16));

        String[][] rows = {
            {"Total Patients Registered",   String.valueOf(ds.getPatients().size())},
            {"Total Doctors on Staff",       String.valueOf(ds.getDoctors().size())},
            {"Total Appointments",           String.valueOf(ds.getAppointments().size())},
            {"Admitted Patients",            String.valueOf(ds.countByStatus(Patient.Status.ADMITTED))},
            {"Critical Cases",               String.valueOf(ds.countByStatus(Patient.Status.CRITICAL))},
            {"Pending Surgeries",            String.valueOf(ds.countByStatus(Patient.Status.SURGERY))},
        };

        for (int i = 0; i < rows.length; i++) {
            Label lbl = new Label(rows[i][0]);
            lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #555;");
            Label val = new Label(rows[i][1]);
            val.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #185FA5;");
            grid.add(lbl, 0, i);
            grid.add(val, 1, i);
        }

        card.getChildren().add(grid);
        return card;
    }

    private VBox card(String title) {
        VBox card = new VBox(0);
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; " +
                "-fx-border-color: #e0ddd5; -fx-border-radius: 10; -fx-border-width: 1;");
        HBox header = new HBox();
        header.setPadding(new Insets(12, 14, 10, 14));
        header.setStyle("-fx-border-color: #e0ddd5; -fx-border-width: 0 0 1 0;");
        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        header.getChildren().add(lbl);
        card.getChildren().add(header);
        return card;
    }
}

package com.hospital.view.pages;

import com.hospital.model.Appointment;
import com.hospital.model.Patient;
import com.hospital.util.DataService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class DashboardPage extends VBox {

    private final DataService ds = DataService.getInstance();

    public DashboardPage() {
        setSpacing(20);
        setPadding(new Insets(24));
        setStyle("-fx-background-color: #f5f4f0;");

        getChildren().addAll(
            pageHeader(),
            statsRow(),
            bottomPanels()
        );
    }

    private HBox pageHeader() {
        HBox hb = new HBox();
        hb.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Dashboard Overview");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        Label date = new Label("Today: May 21, 2026");
        date.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");
        HBox.setHgrow(title, Priority.ALWAYS);
        hb.getChildren().addAll(title, date);
        return hb;
    }

    private HBox statsRow() {
        HBox row = new HBox(14);
        long total      = ds.getPatients().size();
        long admitted   = ds.countByStatus(Patient.Status.ADMITTED);
        long critical   = ds.countByStatus(Patient.Status.CRITICAL);
        long todayAppts = ds.getAppointments().stream()
                            .filter(a -> a.getDate().equals("2024-05-21")).count();

        row.getChildren().addAll(
            statCard("Total Patients",       String.valueOf(total),    "+6.2% this month", "#185FA5", "#E6F1FB"),
            statCard("Admitted",             String.valueOf(admitted), "Currently in ward", "#3B6D11", "#EAF3DE"),
            statCard("Today's Appointments", String.valueOf(todayAppts),"Scheduled today",  "#633806", "#FAEEDA"),
            statCard("Critical Cases",       String.valueOf(critical), "Needs attention",  "#A32D2D", "#FCEBEB")
        );
        row.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
        return row;
    }

    private VBox statCard(String label, String value, String sub, String accent, String bg) {
        VBox card = new VBox(4);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 10;");

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + accent + ";");
        Label sublbl = new Label(sub);
        sublbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + accent + ";");

        card.getChildren().addAll(lbl, val, sublbl);
        return card;
    }

    private HBox bottomPanels() {
        HBox hb = new HBox(14);
        VBox.setVgrow(hb, Priority.ALWAYS);
        hb.getChildren().addAll(recentPatients(), todayAppointments());
        HBox.setHgrow(recentPatients(), Priority.ALWAYS);
        return hb;
    }

    private VBox recentPatients() {
        VBox panel = card("Recent Patients");

        for (Patient p : ds.getPatients().subList(0, Math.min(5, ds.getPatients().size()))) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8, 12, 8, 12));
            row.setStyle("-fx-border-color: #e0ddd5; -fx-border-width: 0 0 1 0;");

            Label avatar = makeAvatar(p.getName());
            VBox info = new VBox(1);
            Label name = new Label(p.getName());
            name.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
            Label detail = new Label(p.getWard() + " · " + p.getDiagnosis());
            detail.setStyle("-fx-font-size: 11px; -fx-text-fill: #888;");
            info.getChildren().addAll(name, detail);
            HBox.setHgrow(info, Priority.ALWAYS);

            Label status = statusPill(p.getStatus());
            row.getChildren().addAll(avatar, info, status);
            panel.getChildren().add(row);
        }
        return panel;
    }

    private VBox todayAppointments() {
        VBox panel = card("Today's Appointments");
        panel.setPrefWidth(280);

        ds.getAppointments().stream()
            .filter(a -> a.getDate().equals("2024-05-21"))
            .forEach(a -> {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(8, 12, 8, 12));
                row.setStyle("-fx-border-color: #e0ddd5; -fx-border-width: 0 0 1 0;");

                VBox timeBox = new VBox();
                timeBox.setAlignment(Pos.CENTER);
                timeBox.setMinWidth(44);
                Label time = new Label(a.getTime());
                time.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #185FA5;");
                timeBox.getChildren().add(time);

                VBox info = new VBox(1);
                Label patient = new Label(a.getPatientName());
                patient.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
                Label doc = new Label(a.getDoctorName() + " · " + a.getDepartment());
                doc.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");
                info.getChildren().addAll(patient, doc);
                HBox.setHgrow(info, Priority.ALWAYS);

                Label statusLbl = apptStatusPill(a.getStatus());
                row.getChildren().addAll(timeBox, info, statusLbl);
                panel.getChildren().add(row);
            });
        return panel;
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

    private Label makeAvatar(String name) {
        String initials = name.length() >= 2
                ? String.valueOf(name.charAt(0)) + name.split(" ")[name.split(" ").length - 1].charAt(0)
                : name.substring(0, 1);
        Label av = new Label(initials.toUpperCase());
        av.setMinSize(30, 30);
        av.setMaxSize(30, 30);
        av.setAlignment(Pos.CENTER);
        av.setStyle("-fx-background-color: #B5D4F4; -fx-text-fill: #0C447C; " +
                "-fx-background-radius: 15; -fx-font-size: 10px; -fx-font-weight: bold;");
        return av;
    }

    private Label statusPill(Patient.Status s) {
        String[] style = switch (s) {
            case ADMITTED    -> new String[]{"Admitted",    "#E6F1FB", "#0C447C"};
            case DISCHARGED  -> new String[]{"Discharged",  "#EAF3DE", "#27500A"};
            case CRITICAL    -> new String[]{"Critical",    "#FCEBEB", "#791F1F"};
            case SURGERY     -> new String[]{"Surgery",     "#FAEEDA", "#633806"};
            case OBSERVATION -> new String[]{"Observation", "#EEEDFE", "#3C3489"};
        };
        Label pill = new Label(style[0]);
        pill.setStyle("-fx-background-color: " + style[1] + "; -fx-text-fill: " + style[2] + "; " +
                "-fx-background-radius: 8; -fx-padding: 2 8 2 8; -fx-font-size: 10px; -fx-font-weight: bold;");
        return pill;
    }

    private Label apptStatusPill(Appointment.AppStatus s) {
        String[] style = switch (s) {
            case SCHEDULED   -> new String[]{"Scheduled",   "#E6F1FB", "#0C447C"};
            case COMPLETED   -> new String[]{"Done",        "#EAF3DE", "#27500A"};
            case CANCELLED   -> new String[]{"Cancelled",   "#FCEBEB", "#791F1F"};
            case IN_PROGRESS -> new String[]{"In progress", "#FAEEDA", "#633806"};
        };
        Label pill = new Label(style[0]);
        pill.setStyle("-fx-background-color: " + style[1] + "; -fx-text-fill: " + style[2] + "; " +
                "-fx-background-radius: 8; -fx-padding: 2 8 2 8; -fx-font-size: 10px; -fx-font-weight: bold;");
        return pill;
    }
}

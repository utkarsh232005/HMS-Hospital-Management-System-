package com.hospital.view;

import com.hospital.view.pages.*;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class MainLayout extends HBox {

    private BorderPane contentArea;

    public MainLayout() {
        setStyle("-fx-background-color: #f5f4f0;");

        VBox sidebar = buildSidebar();
        contentArea = new BorderPane();
        contentArea.setStyle("-fx-background-color: #f5f4f0;");
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        getChildren().addAll(sidebar, contentArea);
        showPage("dashboard");
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.setPrefWidth(210);
        sidebar.setMinWidth(210);
        sidebar.setMaxWidth(210);
        sidebar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0ddd5; -fx-border-width: 0 1 0 0;");

        // Logo
        HBox logoBox = new HBox(10);
        logoBox.setPadding(new Insets(18, 16, 18, 16));
        logoBox.setStyle("-fx-border-color: #e0ddd5; -fx-border-width: 0 0 1 0;");
        Label icon = new Label("✚");
        icon.setStyle("-fx-background-color: #185FA5; -fx-text-fill: #E6F1FB; -fx-font-size: 14px; " +
                "-fx-padding: 6 8 6 8; -fx-background-radius: 6;");
        VBox logoText = new VBox(1);
        Label appName = new Label("MediCore");
        appName.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        Label appSub = new Label("Hospital System");
        appSub.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");
        logoText.getChildren().addAll(appName, appSub);
        logoBox.getChildren().addAll(icon, logoText);

        // Nav
        VBox nav = new VBox(2);
        nav.setPadding(new Insets(10, 8, 10, 8));
        VBox.setVgrow(nav, Priority.ALWAYS);

        nav.getChildren().addAll(
            sectionLabel("MAIN"),
            navBtn("🏠  Dashboard",    "dashboard"),
            navBtn("👥  Patients",     "patients"),
            navBtn("📅  Appointments", "appointments"),
            navBtn("🩺  Doctors",      "doctors"),
            sectionLabel("HOSPITAL"),
            navBtn("🏥  Departments",  "departments"),
            navBtn("💊  Pharmacy",     "pharmacy"),
            sectionLabel("ADMIN"),
            navBtn("📊  Reports",      "reports")
        );

        // Footer
        HBox footer = new HBox(10);
        footer.setPadding(new Insets(12, 16, 14, 16));
        footer.setStyle("-fx-border-color: #e0ddd5; -fx-border-width: 1 0 0 0;");
        Label avatar = new Label("DR");
        avatar.setStyle("-fx-background-color: #B5D4F4; -fx-text-fill: #0C447C; " +
                "-fx-background-radius: 14; -fx-font-size: 11px; -fx-font-weight: bold; " +
                "-fx-padding: 7 9 7 9;");
        VBox userInfo = new VBox(1);
        Label userName = new Label("Dr. Rajan");
        userName.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        Label userRole = new Label("Administrator");
        userRole.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");
        userInfo.getChildren().addAll(userName, userRole);
        footer.getChildren().addAll(avatar, userInfo);

        sidebar.getChildren().addAll(logoBox, nav, footer);
        return sidebar;
    }

    private Label sectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #aaa; -fx-padding: 12 8 4 8; -fx-font-weight: bold;");
        return lbl;
    }

    private Button navBtn(String text, String pageKey) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #555; " +
                "-fx-font-size: 13px; -fx-padding: 8 10 8 10; -fx-background-radius: 7; " +
                "-fx-cursor: hand; -fx-border-width: 0;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #111; " +
                "-fx-font-size: 13px; -fx-padding: 8 10 8 10; -fx-background-radius: 7; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #555; " +
                "-fx-font-size: 13px; -fx-padding: 8 10 8 10; -fx-background-radius: 7; -fx-cursor: hand;"));
        btn.setOnAction(e -> showPage(pageKey));
        return btn;
    }

    private void showPage(String key) {
        switch (key) {
            case "dashboard"    -> contentArea.setCenter(new DashboardPage());
            case "patients"     -> contentArea.setCenter(new PatientPage());
            case "appointments" -> contentArea.setCenter(new AppointmentPage());
            case "doctors"      -> contentArea.setCenter(new DoctorPage());
            case "departments"  -> contentArea.setCenter(new DepartmentPage());
            case "pharmacy"     -> contentArea.setCenter(new PharmacyPage());
            case "reports"      -> contentArea.setCenter(new ReportsPage());
            default             -> contentArea.setCenter(new DashboardPage());
        }
    }
}

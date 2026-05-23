package com.hospital;

import com.hospital.view.MainLayout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainLayout layout = new MainLayout();
        Scene scene = new Scene(layout, 1200, 700);

        // Load CSS — works with both module and non-module builds
        String css = getClass().getResource("/styles/app.css") != null
                ? getClass().getResource("/styles/app.css").toExternalForm()
                : null;
        if (css != null) scene.getStylesheets().add(css);

        primaryStage.setTitle("MediCore — Hospital Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

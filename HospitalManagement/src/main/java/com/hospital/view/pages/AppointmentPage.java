package com.hospital.view.pages;

import com.hospital.model.Appointment;
import com.hospital.util.DataService;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.Optional;

public class AppointmentPage extends VBox {

    private final DataService ds = DataService.getInstance();
    private FilteredList<Appointment> filtered;

    public AppointmentPage() {
        setSpacing(16);
        setPadding(new Insets(24));
        setStyle("-fx-background-color: #f5f4f0;");
        filtered = new FilteredList<>(ds.getAppointments(), a -> true);
        getChildren().addAll(topBar(), buildTable());
    }

    private HBox topBar() {
        Label title = new Label("Appointments");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        HBox.setHgrow(title, Priority.ALWAYS);

        TextField search = new TextField();
        search.setPromptText("🔍  Search…");
        search.setPrefWidth(200);
        search.textProperty().addListener((obs, o, n) ->
            filtered.setPredicate(a -> n == null || n.isEmpty() ||
                a.getPatientName().toLowerCase().contains(n.toLowerCase()) ||
                a.getDoctorName().toLowerCase().contains(n.toLowerCase()))
        );

        ComboBox<String> filter = new ComboBox<>();
        filter.getItems().addAll("All Dates", "Today", "Upcoming");
        filter.setValue("All Dates");
        filter.setOnAction(e -> {
            String sel = filter.getValue();
            filtered.setPredicate(a -> switch (sel) {
                case "Today"    -> a.getDate().equals("2024-05-21");
                case "Upcoming" -> a.getDate().compareTo("2024-05-21") > 0;
                default -> true;
            });
        });

        Button addBtn = new Button("+ New Appointment");
        addBtn.setStyle("-fx-background-color: #185FA5; -fx-text-fill: white; " +
                "-fx-background-radius: 7; -fx-font-size: 12px; -fx-padding: 7 14 7 14; -fx-cursor: hand;");
        addBtn.setOnAction(e -> showDialog(null));

        HBox bar = new HBox(10, title, search, filter, addBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    @SuppressWarnings("unchecked")
    private TableView<Appointment> buildTable() {
        TableView<Appointment> table = new TableView<>(filtered);
        table.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Appointment, Integer> idCol     = col("ID",         "id",          55);
        TableColumn<Appointment, String>  patCol    = col("Patient",    "patientName", 160);
        TableColumn<Appointment, String>  docCol    = col("Doctor",     "doctorName",  150);
        TableColumn<Appointment, String>  deptCol   = col("Department", "department",  130);
        TableColumn<Appointment, String>  dateCol   = col("Date",       "date",        110);
        TableColumn<Appointment, String>  timeCol   = col("Time",       "time",         80);

        TableColumn<Appointment, Appointment.AppStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(110);
        statusCol.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Appointment.AppStatus s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setGraphic(null); return; }
                String[] st = switch (s) {
                    case SCHEDULED   -> new String[]{"Scheduled",   "#E6F1FB", "#0C447C"};
                    case COMPLETED   -> new String[]{"Completed",   "#EAF3DE", "#27500A"};
                    case CANCELLED   -> new String[]{"Cancelled",   "#FCEBEB", "#791F1F"};
                    case IN_PROGRESS -> new String[]{"In Progress", "#FAEEDA", "#633806"};
                };
                Label pill = new Label(st[0]);
                pill.setStyle("-fx-background-color:" + st[1] + ";-fx-text-fill:" + st[2] +
                        ";-fx-background-radius:8;-fx-padding:2 8 2 8;-fx-font-size:10px;-fx-font-weight:bold;");
                setGraphic(pill); setText(null);
            }
        });

        TableColumn<Appointment, Void> actCol = new TableColumn<>("Actions");
        actCol.setPrefWidth(120);
        actCol.setCellFactory(tc -> new TableCell<>() {
            Button edit = new Button("Edit");
            Button del  = new Button("Delete");
            HBox box    = new HBox(6, edit, del);
            {
                edit.setStyle("-fx-background-color:#E6F1FB;-fx-text-fill:#0C447C;-fx-background-radius:5;-fx-font-size:10px;-fx-cursor:hand;");
                del.setStyle("-fx-background-color:#FCEBEB;-fx-text-fill:#791F1F;-fx-background-radius:5;-fx-font-size:10px;-fx-cursor:hand;");
                box.setAlignment(Pos.CENTER_LEFT);
                edit.setOnAction(e -> showDialog(getTableView().getItems().get(getIndex())));
                del.setOnAction(e -> {
                    Appointment a = getTableView().getItems().get(getIndex());
                    Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Remove appointment?", ButtonType.YES, ButtonType.NO);
                    c.setHeaderText(null);
                    c.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> ds.removeAppointment(a));
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(idCol, patCol, docCol, deptCol, dateCol, timeCol, statusCol, actCol);
        return table;
    }

    private <T> TableColumn<Appointment, T> col(String title, String prop, int width) {
        TableColumn<Appointment, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    private void showDialog(Appointment existing) {
        Dialog<Appointment> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "New Appointment" : "Edit Appointment");
        dlg.setHeaderText(null);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField patFld  = new TextField(existing != null ? existing.getPatientName() : "");
        TextField docFld  = new TextField(existing != null ? existing.getDoctorName()  : "");
        TextField deptFld = new TextField(existing != null ? existing.getDepartment()  : "");
        TextField dateFld = new TextField(existing != null ? existing.getDate()        : "2024-05-21");
        TextField timeFld = new TextField(existing != null ? existing.getTime()        : "09:00");
        ComboBox<Appointment.AppStatus> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(Appointment.AppStatus.values());
        statusBox.setValue(existing != null ? existing.getStatus() : Appointment.AppStatus.SCHEDULED);

        String[] labels = {"Patient", "Doctor", "Department", "Date", "Time", "Status"};
        Control[] ctrls = {patFld, docFld, deptFld, dateFld, timeFld, statusBox};
        for (int i = 0; i < labels.length; i++) {
            grid.add(new Label(labels[i] + ":"), 0, i);
            grid.add(ctrls[i], 1, i);
            if (ctrls[i] instanceof TextField tf) tf.setPrefWidth(200);
        }

        dlg.getDialogPane().setContent(grid);
        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                if (existing != null) ds.removeAppointment(existing);
                return new Appointment(ds.nextApptId(), patFld.getText(), docFld.getText(),
                        deptFld.getText(), dateFld.getText(), timeFld.getText(), statusBox.getValue());
            }
            return null;
        });
        dlg.showAndWait().ifPresent(ds::addAppointment);
    }
}

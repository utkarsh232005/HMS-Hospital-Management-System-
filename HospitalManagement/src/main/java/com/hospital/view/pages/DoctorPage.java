package com.hospital.view.pages;

import com.hospital.model.Doctor;
import com.hospital.util.DataService;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class DoctorPage extends VBox {

    private final DataService ds = DataService.getInstance();

    public DoctorPage() {
        setSpacing(16);
        setPadding(new Insets(24));
        setStyle("-fx-background-color: #f5f4f0;");

        FilteredList<Doctor> filtered = new FilteredList<>(ds.getDoctors(), d -> true);

        Label title = new Label("Doctors");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");

        TextField search = new TextField();
        search.setPromptText("🔍  Search doctors…");
        search.setPrefWidth(200);
        search.textProperty().addListener((obs, o, n) ->
            filtered.setPredicate(d -> n == null || n.isEmpty() ||
                d.getName().toLowerCase().contains(n.toLowerCase()) ||
                d.getSpecialization().toLowerCase().contains(n.toLowerCase()))
        );

        Button addBtn = new Button("+ Add Doctor");
        addBtn.setStyle("-fx-background-color: #185FA5; -fx-text-fill: white; " +
                "-fx-background-radius: 7; -fx-font-size: 12px; -fx-padding: 7 14 7 14; -fx-cursor: hand;");
        addBtn.setOnAction(e -> showDialog(null, filtered));

        HBox.setHgrow(title, Priority.ALWAYS);
        HBox bar = new HBox(10, title, search, addBtn);
        bar.setAlignment(Pos.CENTER_LEFT);

        TableView<Doctor> table = buildTable(filtered);
        getChildren().addAll(bar, table);
    }

    @SuppressWarnings("unchecked")
    private TableView<Doctor> buildTable(FilteredList<Doctor> filtered) {
        TableView<Doctor> table = new TableView<>(filtered);
        table.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Doctor, Integer> idCol   = col("ID",            "id",             55);
        TableColumn<Doctor, String>  nmCol   = col("Doctor Name",   "name",          180);
        TableColumn<Doctor, String>  spCol   = col("Specialization","specialization", 150);
        TableColumn<Doctor, String>  phCol   = col("Phone",         "phone",         140);
        TableColumn<Doctor, String>  emCol   = col("Email",         "email",         190);
        TableColumn<Doctor, Integer> ptCol   = col("Patients",      "patientsCount",  75);
        TableColumn<Doctor, String>  avCol   = col("Availability",  "availability",  110);

        TableColumn<Doctor, Void> actCol = new TableColumn<>("Actions");
        actCol.setPrefWidth(110);
        actCol.setCellFactory(tc -> new TableCell<>() {
            Button edit = new Button("Edit");
            Button del  = new Button("Delete");
            HBox box    = new HBox(6, edit, del);
            {
                edit.setStyle("-fx-background-color:#E6F1FB;-fx-text-fill:#0C447C;-fx-background-radius:5;-fx-font-size:10px;-fx-cursor:hand;");
                del.setStyle("-fx-background-color:#FCEBEB;-fx-text-fill:#791F1F;-fx-background-radius:5;-fx-font-size:10px;-fx-cursor:hand;");
                box.setAlignment(Pos.CENTER_LEFT);
                edit.setOnAction(e -> showDialog(getTableView().getItems().get(getIndex()), filtered));
                del.setOnAction(e -> {
                    Doctor d = getTableView().getItems().get(getIndex());
                    Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Remove " + d.getName() + "?", ButtonType.YES, ButtonType.NO);
                    c.setHeaderText(null);
                    c.showAndWait().filter(r -> r == ButtonType.YES).ifPresent(r -> ds.removeDoctor(d));
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(idCol, nmCol, spCol, phCol, emCol, ptCol, avCol, actCol);
        return table;
    }

    private <T> TableColumn<Doctor, T> col(String title, String prop, int width) {
        TableColumn<Doctor, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    private void showDialog(Doctor existing, FilteredList<Doctor> filtered) {
        Dialog<Doctor> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "Add Doctor" : "Edit Doctor");
        dlg.setHeaderText(null);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameFld  = new TextField(existing != null ? existing.getName()           : "Dr. ");
        TextField specFld  = new TextField(existing != null ? existing.getSpecialization() : "");
        TextField phoneFld = new TextField(existing != null ? existing.getPhone()          : "");
        TextField emailFld = new TextField(existing != null ? existing.getEmail()          : "");
        TextField availFld = new TextField(existing != null ? existing.getAvailability()   : "Mon–Fri");

        String[] labels = {"Name", "Specialization", "Phone", "Email", "Availability"};
        TextField[] fields = {nameFld, specFld, phoneFld, emailFld, availFld};
        for (int i = 0; i < labels.length; i++) {
            grid.add(new Label(labels[i] + ":"), 0, i);
            fields[i].setPrefWidth(220);
            grid.add(fields[i], 1, i);
        }

        dlg.getDialogPane().setContent(grid);
        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                if (existing != null) ds.removeDoctor(existing);
                return new Doctor(ds.nextDoctorId(), nameFld.getText(), specFld.getText(),
                        phoneFld.getText(), emailFld.getText(), 0, availFld.getText());
            }
            return null;
        });
        dlg.showAndWait().ifPresent(ds::addDoctor);
    }
}

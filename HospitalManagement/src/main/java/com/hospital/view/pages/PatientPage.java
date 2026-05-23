package com.hospital.view.pages;

import com.hospital.model.Patient;
import com.hospital.util.DataService;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Optional;

public class PatientPage extends VBox {

    private final DataService ds = DataService.getInstance();
    private TableView<Patient> table;
    private FilteredList<Patient> filtered;

    public PatientPage() {
        setSpacing(16);
        setPadding(new Insets(24));
        setStyle("-fx-background-color: #f5f4f0;");

        filtered = new FilteredList<>(ds.getPatients(), p -> true);

        getChildren().addAll(topBar(), buildTable());
        VBox.setVgrow(buildTable(), Priority.ALWAYS);
    }

    private HBox topBar() {
        Label title = new Label("Patient Records");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        HBox.setHgrow(title, Priority.ALWAYS);

        TextField search = new TextField();
        search.setPromptText("🔍  Search patients…");
        search.setPrefWidth(220);
        search.setStyle("-fx-background-radius: 7; -fx-border-radius: 7; -fx-font-size: 12px;");
        search.textProperty().addListener((obs, o, n) ->
            filtered.setPredicate(p -> n == null || n.isEmpty() ||
                p.getName().toLowerCase().contains(n.toLowerCase()) ||
                p.getWard().toLowerCase().contains(n.toLowerCase()) ||
                p.getDiagnosis().toLowerCase().contains(n.toLowerCase()))
        );

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Admitted", "Critical", "Surgery", "Observation", "Discharged");
        statusFilter.setValue("All");
        statusFilter.setStyle("-fx-font-size: 12px;");
        statusFilter.setOnAction(e -> {
            String sel = statusFilter.getValue();
            filtered.setPredicate(p -> "All".equals(sel) ||
                p.getStatus().name().equalsIgnoreCase(sel));
        });

        Button addBtn = new Button("+ Add Patient");
        addBtn.setStyle("-fx-background-color: #185FA5; -fx-text-fill: white; " +
                "-fx-background-radius: 7; -fx-font-size: 12px; -fx-padding: 7 14 7 14; -fx-cursor: hand;");
        addBtn.setOnAction(e -> showPatientDialog(null));

        HBox bar = new HBox(10, title, search, statusFilter, addBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    @SuppressWarnings("unchecked")
    private TableView<Patient> buildTable() {
        table = new TableView<>(filtered);
        table.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Patient, Integer> idCol = col("ID", "id", 55);
        TableColumn<Patient, String> nameCol = col("Patient Name", "name", 170);
        TableColumn<Patient, Integer> ageCol = col("Age", "age", 55);
        TableColumn<Patient, String> wardCol = col("Ward", "ward", 120);
        TableColumn<Patient, String> diagCol = col("Diagnosis", "diagnosis", 180);
        TableColumn<Patient, String> docCol  = col("Doctor", "doctor", 130);
        TableColumn<Patient, String> dateCol = col("Admitted", "admissionDate", 100);

        TableColumn<Patient, Patient.Status> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(110);
        statusCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Patient.Status s, boolean empty) {
                super.updateItem(s, empty);
                if (empty || s == null) { setGraphic(null); return; }
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
                setGraphic(pill);
                setText(null);
            }
        });

        TableColumn<Patient, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(100);
        actionsCol.setCellFactory(tc -> new TableCell<>() {
            final Button edit = new Button("Edit");
            final Button del  = new Button("Delete");
            final HBox box    = new HBox(6, edit, del);
            {
                edit.setStyle("-fx-background-color: #E6F1FB; -fx-text-fill: #0C447C; " +
                        "-fx-background-radius: 5; -fx-font-size: 10px; -fx-cursor: hand;");
                del.setStyle("-fx-background-color: #FCEBEB; -fx-text-fill: #791F1F; " +
                        "-fx-background-radius: 5; -fx-font-size: 10px; -fx-cursor: hand;");
                box.setAlignment(Pos.CENTER_LEFT);
                edit.setOnAction(e -> showPatientDialog(getTableView().getItems().get(getIndex())));
                del.setOnAction(e -> {
                    Patient p = getTableView().getItems().get(getIndex());
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Remove " + p.getName() + "?", ButtonType.YES, ButtonType.NO);
                    confirm.setHeaderText(null);
                    Optional<ButtonType> res = confirm.showAndWait();
                    if (res.isPresent() && res.get() == ButtonType.YES)
                        ds.removePatient(p);
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(idCol, nameCol, ageCol, wardCol, diagCol, docCol, dateCol, statusCol, actionsCol);
        return table;
    }

    private <T> TableColumn<Patient, T> col(String title, String prop, int width) {
        TableColumn<Patient, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    private void showPatientDialog(Patient existing) {
        Dialog<Patient> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "Add Patient" : "Edit Patient");
        dlg.setHeaderText(null);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameFld  = new TextField(existing != null ? existing.getName()      : "");
        TextField ageFld   = new TextField(existing != null ? String.valueOf(existing.getAge()) : "");
        TextField wardFld  = new TextField(existing != null ? existing.getWard()      : "");
        TextField diagFld  = new TextField(existing != null ? existing.getDiagnosis() : "");
        TextField docFld   = new TextField(existing != null ? existing.getDoctor()    : "");
        TextField dateFld  = new TextField(existing != null ? existing.getAdmissionDate() : "2024-05-21");
        ComboBox<Patient.Status> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(Patient.Status.values());
        statusBox.setValue(existing != null ? existing.getStatus() : Patient.Status.ADMITTED);

        String[][] fields = {{"Name", null}, {"Age", null}, {"Ward", null},
                {"Diagnosis", null}, {"Doctor", null}, {"Admitted", null}, {"Status", null}};
        Control[] controls = {nameFld, ageFld, wardFld, diagFld, docFld, dateFld, statusBox};

        for (int i = 0; i < controls.length; i++) {
            grid.add(new Label(fields[i][0] + ":"), 0, i);
            grid.add(controls[i], 1, i);
            if (controls[i] instanceof TextField tf) tf.setPrefWidth(220);
        }

        dlg.getDialogPane().setContent(grid);
        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                if (existing != null) {
                    // Update not supported fully for immutable fields — create new for demo
                    ds.removePatient(existing);
                }
                return new Patient(
                    ds.nextPatientId(), nameFld.getText(), Integer.parseInt(ageFld.getText()),
                    wardFld.getText(), diagFld.getText(), statusBox.getValue(),
                    docFld.getText(), dateFld.getText()
                );
            }
            return null;
        });

        Optional<Patient> result = dlg.showAndWait();
        result.ifPresent(ds::addPatient);
    }
}

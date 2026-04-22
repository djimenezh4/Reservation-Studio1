package com.reservationstudio.controller;

import com.reservationstudio.model.Reservation;
import com.reservationstudio.model.Reservation.Status;
import com.reservationstudio.model.Table;
import com.reservationstudio.model.Table.TableStatus;
import com.reservationstudio.util.CsvManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainController implements Initializable {

    //  Left panel
    @FXML private TextField searchField;
    @FXML private ListView<Reservation> seatedListView;
    @FXML private ListView<Reservation> reservationListView;
    @FXML private Label statusBar;

    // Date / service bar
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label serviceLabel;
    @FXML private Label pageLabel;

    // Floor plan
    @FXML private GridPane floorGrid;

    // Add Reservation dialog fields
    @FXML private TextField addNameField;
    @FXML private TextField addTimeField;
    @FXML private Spinner<Integer> addGuestsSpinner;
    @FXML private Spinner<Integer> addTableSpinner;
    @FXML private ToggleGroup statusToggleGroup;
    @FXML private TextField addNotesField;

    // Data
    private final CsvManager csvManager = new CsvManager("reservations.csv");
    private ObservableList<Reservation> allReservations;
    private FilteredList<Reservation> filteredSeated;
    private FilteredList<Reservation> filteredReservations;

    private final List<Table> tables = new ArrayList<>();
    private final Map<Integer, StackPane> tableNodes = new HashMap<>();

    // Table capacities (tables 1-6)
    private static final int[] TABLE_CAPS = {4, 6, 3, 4, 4, 6};

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTables();
        setupDateBar();
        loadData();
        setupListViews();
        setupSearch();
        setupFloorPlan();
        refreshFloorPlan();
        startClock();
    }

    // Setup

    private void setupTables() {
        for (int i = 0; i < 6; i++) {
            tables.add(new Table(i + 1, TABLE_CAPS[i]));
        }
    }

    private void setupDateBar() {
        dateLabel.setText("DATE: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        serviceLabel.setText("Lunch");
        pageLabel.setText("1/2 ▶");
    }

    private void startClock() {
        javafx.animation.Timeline clock = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> {
                    timeLabel.setText(java.time.LocalTime.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("h:mm a")));
                })
        );
        clock.setCycleCount(javafx.animation.Animation.INDEFINITE);
        clock.play();
        timeLabel.setText(java.time.LocalTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("h:mm a")));
    }

    private void loadData() {
        allReservations = csvManager.loadAll();
        filteredSeated = new FilteredList<>(allReservations,
                r -> r.getStatus() == Status.SEATED);
        filteredReservations = new FilteredList<>(allReservations,
                r -> r.getStatus() == Status.RESERVATION);
    }

    private void setupListViews() {
        seatedListView.setItems(filteredSeated);
        seatedListView.setCellFactory(lv -> new ReservationCell(true));

        reservationListView.setItems(filteredReservations);
        reservationListView.setCellFactory(lv -> new ReservationCell(false));

        // Click to select table on floor plan
        seatedListView.getSelectionModel().selectedItemProperty().addListener((obs, old, res) -> {
            if (res != null) highlightTable(res.getTableNumber());
        });
        reservationListView.getSelectionModel().selectedItemProperty().addListener((obs, old, res) -> {
            if (res != null) highlightTable(res.getTableNumber());
        });
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, old, text) -> {
            String lower = text.toLowerCase();
            filteredSeated.setPredicate(r ->
                    r.getStatus() == Status.SEATED &&
                    (text.isEmpty() || r.getName().toLowerCase().contains(lower)
                            || r.getTime().contains(lower)));
            filteredReservations.setPredicate(r ->
                    r.getStatus() == Status.RESERVATION &&
                    (text.isEmpty() || r.getName().toLowerCase().contains(lower)
                            || r.getTime().contains(lower)));
        });
    }

    private void setupFloorPlan() {
        // 3 rows × 2 cols of tables
        int[][] layout = {{1,2},{3,4},{5,6}};
        for (int row = 0; row < layout.length; row++) {
            for (int col = 0; col < layout[row].length; col++) {
                int tNum = layout[row][col];
                Table t = tables.get(tNum - 1);
                StackPane pane = buildTableNode(t);
                tableNodes.put(tNum, pane);
                floorGrid.add(pane, col, row);
            }
        }
    }

    private StackPane buildTableNode(Table table) {
        StackPane pane = new StackPane();
        pane.getStyleClass().add("table-node");
        pane.setUserData(table);

        Rectangle rect = new Rectangle();
        rect.setArcWidth(30);
        rect.setArcHeight(30);
        rect.widthProperty().bind(pane.widthProperty().subtract(20));
        rect.heightProperty().bind(pane.heightProperty().subtract(20));

        VBox content = new VBox(4);
        content.setAlignment(Pos.CENTER);
        Label numLabel = new Label(String.valueOf(table.getNumber()));
        numLabel.getStyleClass().add("table-number");
        Label capLabel = new Label("Cap: " + table.getCapacity());
        capLabel.getStyleClass().add("table-cap");
        Label nameLabel = new Label();
        nameLabel.getStyleClass().add("table-name");
        nameLabel.textProperty().bind(table.occupantNameProperty());

        content.getChildren().addAll(numLabel, capLabel, nameLabel);
        pane.getChildren().addAll(rect, content);

        // Color binding
        table.statusProperty().addListener((obs, old, s) -> applyTableStyle(pane, rect, s));
        applyTableStyle(pane, rect, table.getStatus());

        // seat/release
        pane.setOnMouseClicked(e -> handleTableClick(table));

        return pane;
    }

    private void applyTableStyle(StackPane pane, Rectangle rect, TableStatus status) {
        switch (status) {
            case AVAILABLE -> rect.setFill(Color.web("#b08060"));
            case SEATED    -> rect.setFill(Color.web("#7a2030"));
            case RESERVED  -> rect.setFill(Color.web("#6b5c4e"));
        }
    }

    private void refreshFloorPlan() {
        // Reset all
        tables.forEach(t -> { t.setStatus(TableStatus.AVAILABLE); t.setOccupantName(""); });

        allReservations.forEach(r -> {
            int tNum = r.getTableNumber();
            if (tNum < 1 || tNum > tables.size()) return;
            Table t = tables.get(tNum - 1);
            if (r.getStatus() == Status.SEATED) {
                t.setStatus(TableStatus.SEATED);
                t.setOccupantName(r.getName());
            } else if (r.getStatus() == Status.RESERVATION && t.getStatus() == TableStatus.AVAILABLE) {
                t.setStatus(TableStatus.RESERVED);
                t.setOccupantName(r.getName());
            }
        });
    }

    private void highlightTable(int tableNum) {
        tableNodes.forEach((num, pane) -> pane.setEffect(null));
        StackPane target = tableNodes.get(tableNum);
        if (target != null) {
            javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
            glow.setColor(Color.web("#f0c040"));
            glow.setRadius(20);
            glow.setSpread(0.5);
            target.setEffect(glow);
        }
    }

    private void handleTableClick(Table table) {
        // Show a context menu
        ContextMenu menu = new ContextMenu();
        MenuItem seatItem = new MenuItem("Seat a Party at Table " + table.getNumber());
        MenuItem clearItem = new MenuItem("Clear Table " + table.getNumber());

        seatItem.setOnAction(e -> showQuickSeatDialog(table));
        clearItem.setOnAction(e -> clearTable(table));

        menu.getItems().addAll(seatItem, clearItem);
        StackPane node = tableNodes.get(table.getNumber());
        menu.show(node, javafx.geometry.Side.BOTTOM, 0, 0);
    }

    private void clearTable(Table table) {
        allReservations.removeIf(r ->
                r.getTableNumber() == table.getNumber() && r.getStatus() == Status.SEATED);
        saveAndRefresh();
    }

    // Add Reservation

    @FXML
    private void handleAddReservation() {
        String name = addNameField.getText().trim();
        String time = addTimeField.getText().trim();
        int guests = addGuestsSpinner.getValue();
        int table = addTableSpinner.getValue();

        if (name.isEmpty() || time.isEmpty()) {
            showAlert("Validation", "Name and time are required.");
            return;
        }

        RadioButton selected = (RadioButton) statusToggleGroup.getSelectedToggle();
        Status status = (selected != null && selected.getText().equalsIgnoreCase("Seated"))
                ? Status.SEATED : Status.RESERVATION;

        String notes = addNotesField.getText().trim();
        Reservation r = new Reservation(name, time, guests, table, status, notes);
        allReservations.add(r);
        saveAndRefresh();

        // Clear form
        addNameField.clear();
        addTimeField.clear();
        addNotesField.clear();
        addGuestsSpinner.getValueFactory().setValue(2);
        addTableSpinner.getValueFactory().setValue(1);
        statusBar.setText("✓ Added: " + name + " at " + time);
    }

    @FXML
    private void handleDeleteSelected() {
        Reservation r = seatedListView.getSelectionModel().getSelectedItem();
        if (r == null) r = reservationListView.getSelectionModel().getSelectedItem();
        if (r == null) { showAlert("Delete", "Please select a reservation first."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete reservation for " + r.getName() + "?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        Reservation finalR = r;
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                allReservations.remove(finalR);
                saveAndRefresh();
                statusBar.setText("✓ Deleted: " + finalR.getName());
            }
        });
    }

    @FXML
    private void handleMarkSeated() {
        Reservation r = reservationListView.getSelectionModel().getSelectedItem();
        if (r == null) { showAlert("Mark Seated", "Select a reservation first."); return; }
        r.setStatus(Status.SEATED);
        saveAndRefresh();
        statusBar.setText("✓ Seated: " + r.getName());
    }

    @FXML
    private void handleExportCsv() {
        statusBar.setText("✓ Saved to: " + csvManager.getFilePath());
    }

    // Quick seat dialog

    private void showQuickSeatDialog(Table table) {
        Dialog<Reservation> dlg = new Dialog<>();
        dlg.setTitle("Seat Party — Table " + table.getNumber());
        dlg.setHeaderText("Enter party details for Table " + table.getNumber()
                + " (Cap: " + table.getCapacity() + ")");

        ButtonType saveBtn = new ButtonType("Seat", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(12));

        TextField nameF = new TextField(); nameF.setPromptText("Party name");
        TextField timeF = new TextField(java.time.LocalTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("h:mm a")));
        Spinner<Integer> guestSp = new Spinner<>(1, table.getCapacity(), Math.min(2, table.getCapacity()));
        ToggleGroup tg = new ToggleGroup();
        RadioButton seatRb = new RadioButton("Seated"); seatRb.setToggleGroup(tg); seatRb.setSelected(true);
        RadioButton resRb  = new RadioButton("Reservation"); resRb.setToggleGroup(tg);
        HBox radioBox = new HBox(12, seatRb, resRb);

        grid.add(new Label("Name:"),   0, 0); grid.add(nameF,    1, 0);
        grid.add(new Label("Time:"),   0, 1); grid.add(timeF,    1, 1);
        grid.add(new Label("Guests:"), 0, 2); grid.add(guestSp,  1, 2);
        grid.add(new Label("Status:"), 0, 3); grid.add(radioBox, 1, 3);

        dlg.getDialogPane().setContent(grid);
        dlg.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Status st = seatRb.isSelected() ? Status.SEATED : Status.RESERVATION;
                return new Reservation(nameF.getText().trim(), timeF.getText().trim(),
                        guestSp.getValue(), table.getNumber(), st, "");
            }
            return null;
        });

        dlg.showAndWait().ifPresent(r -> {
            if (!r.getName().isEmpty()) {
                allReservations.add(r);
                saveAndRefresh();
                statusBar.setText("✓ Added: " + r.getName() + " → Table " + table.getNumber());
            }
        });
    }

    // Helpers

    private void saveAndRefresh() {
        csvManager.saveAll(allReservations);
        refreshFloorPlan();
        seatedListView.refresh();
        reservationListView.refresh();
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setTitle(title);
        a.showAndWait();
    }

    // Custom list cell

    private static class ReservationCell extends ListCell<Reservation> {
        private final boolean seated;
        ReservationCell(boolean seated) { this.seated = seated; }

        @Override
        protected void updateItem(Reservation r, boolean empty) {
            super.updateItem(r, empty);
            if (empty || r == null) { setGraphic(null); return; }

            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new javafx.geometry.Insets(4, 8, 4, 8));

            Label name  = new Label(r.getName().toUpperCase());
            name.setMinWidth(80);
            name.getStyleClass().add("cell-name");

            Label time  = new Label(r.getTime());
            time.setMinWidth(55);
            time.getStyleClass().add("cell-info");

            Label table = new Label(r.getTableNumber() + "G");
            table.getStyleClass().add("cell-info");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            row.getChildren().addAll(name, time, spacer, table);
            setGraphic(row);
            getStyleClass().removeAll("cell-seated", "cell-reservation");
            getStyleClass().add(seated ? "cell-seated" : "cell-reservation");
        }
    }
}

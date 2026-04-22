package com.reservationstudio;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MainView {

    //  Data
    private final CsvService csvService = new CsvService("reservations.csv");
    private final ObservableList<Reservation> allReservations = FXCollections.observableArrayList();

    // Tables layout (matches screenshot 6-table layout)
    private final List<RestaurantTable> tables = List.of(
            new RestaurantTable(1, 4, 60,  110, 200, 110),
            new RestaurantTable(2, 6, 320, 110, 240, 110),
            new RestaurantTable(3, 3, 60,  280, 160, 140),
            new RestaurantTable(4, 5, 280, 280, 260, 130),
            new RestaurantTable(5, 4, 60,  460, 160, 160),
            new RestaurantTable(6, 6, 280, 460, 260, 150)
    );

    // UI components
    private FloorPlanView floorPlanView;
    private VBox leftPanel;
    private TextField searchField;
    private VBox seatedList;
    private VBox reservationList;
    private HBox root;

    // Top bar
    private Label dateLabel;
    private Label timeLabel;
    private Label mealLabel;

    public MainView() {
        build();
        loadData();
    }

    private void build() {
        root = new HBox();
        root.setStyle("-fx-background-color: #111111;");

        buildTopBar();
        buildLeftPanel();
        buildFloorPanel();
    }

    //  TOP BAR
    private void buildTopBar() {
        // Top bar is embedded in the right panel header
    }

    //LEFT PANEL
    private void buildLeftPanel() {
        leftPanel = new VBox(10);
        leftPanel.setPrefWidth(320);
        leftPanel.setMinWidth(280);
        leftPanel.setStyle("-fx-background-color: #111111;");
        leftPanel.setPadding(new Insets(18, 14, 18, 14));

        // Search
        searchField = new TextField();
        searchField.setPromptText("Search guests...");
        searchField.setStyle("-fx-background-color: #2a2a2a; -fx-text-fill: white; " +
                "-fx-prompt-text-fill: #777777; -fx-border-color: #555555; " +
                "-fx-border-radius: 20; -fx-background-radius: 20; " +
                "-fx-font-family: 'Georgia'; -fx-font-size: 13px; -fx-padding: 8 14 8 14;");
        searchField.textProperty().addListener((obs, o, n) -> filterLists(n));

        Label searchTitle = styledSectionHeader("Search", "#8a7a70");

        // Seated section
        Label seatedHeader = styledSectionHeader("Seated", "#c0392b");
        seatedList = new VBox(2);
        seatedList.setStyle("-fx-background-color: #1e1e1e; -fx-background-radius: 6;");
        seatedList.setPadding(new Insets(4));

        // Reservation section
        Label reservationHeader = styledSectionHeader("Reservation", "#c0392b");
        reservationList = new VBox(2);
        reservationList.setStyle("-fx-background-color: #1e1e1e; -fx-background-radius: 6;");
        reservationList.setPadding(new Insets(4));

        // Add Reservation button
        Button addBtn = new Button("Add Reservation");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setStyle("-fx-background-color: #2a2a2a; -fx-text-fill: #cccccc; " +
                "-fx-font-family: 'Georgia'; -fx-font-size: 13px; " +
                "-fx-background-radius: 20; -fx-border-color: #555555; " +
                "-fx-border-radius: 20; -fx-padding: 8 20 8 20; -fx-cursor: hand;");
        addBtn.setOnAction(e -> showAddDialog());
        addBtn.setOnMouseEntered(e -> addBtn.setStyle(addBtn.getStyle()
                .replace("#2a2a2a", "#3a3a3a")));
        addBtn.setOnMouseExited(e -> addBtn.setStyle(addBtn.getStyle()
                .replace("#3a3a3a", "#2a2a2a")));

        // CSV file path label
        Label csvInfo = new Label("Data: reservations.csv");
        csvInfo.setStyle("-fx-text-fill: #555555; -fx-font-size: 10px; -fx-font-family: 'Georgia';");

        leftPanel.getChildren().addAll(
                searchTitle, searchField,
                seatedHeader, seatedList,
                reservationHeader, reservationList,
                addBtn, csvInfo
        );
        root.getChildren().add(leftPanel);
    }

    //RIGHT / FLOOR PANEL
    private void buildFloorPanel() {
        // Top bar
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("M/d/yyyy"));
        java.time.LocalTime now = java.time.LocalTime.now();
        String timeStr = now.format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"));

        HBox topBar = new HBox();
        topBar.setStyle("-fx-background-color: #1a1a1a; -fx-border-color: #333333; " +
                "-fx-border-width: 0 0 1 0;");
        topBar.setPadding(new Insets(8, 16, 8, 16));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(30);

        // Calendar icon placeholder
        Label calIcon = new Label("📅");
        calIcon.setStyle("-fx-font-size: 18px;");

        dateLabel = new Label("DATE: " + today);
        dateLabel.setStyle("-fx-text-fill: white; -fx-font-family: 'Georgia'; " +
                "-fx-font-weight: bold; -fx-font-size: 14px;");

        timeLabel = new Label(timeStr);
        timeLabel.setStyle("-fx-text-fill: white; -fx-font-family: 'Georgia'; " +
                "-fx-font-size: 14px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        mealLabel = new Label("Lunch");
        mealLabel.setStyle("-fx-text-fill: white; -fx-font-family: 'Georgia'; " +
                "-fx-font-size: 14px;");

        Label pageLabel = new Label("1/2 ▶");
        pageLabel.setStyle("-fx-text-fill: white; -fx-font-family: 'Georgia'; -fx-font-size: 14px;");

        topBar.getChildren().addAll(calIcon, dateLabel, timeLabel, spacer, mealLabel,
                new Separator(javafx.geometry.Orientation.VERTICAL), pageLabel);

        // Floor plan
        floorPlanView = new FloorPlanView(tables);

        VBox rightPanel = new VBox(topBar, floorPlanView.getRoot());
        VBox.setVgrow(floorPlanView.getRoot(), Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);
        rightPanel.setStyle("-fx-background-color: #3d8fa1;");

        root.getChildren().add(rightPanel);
    }

    // DATA
    private void loadData() {
        List<Reservation> loaded = csvService.loadAll();
        if (loaded.isEmpty()) {
            // Seed with sample data matching screenshot
            loaded = sampleData();
            csvService.saveAll(loaded);
        }
        allReservations.setAll(loaded);
        refreshLists(allReservations);
        floorPlanView.refresh(allReservations);
    }

    private List<Reservation> sampleData() {
        return new ArrayList<>(Arrays.asList(
                new Reservation("JOHN",   "12:30", 5, 1, Reservation.Status.SEATED),
                new Reservation("MARCUS", "12:45", 3, 2, Reservation.Status.SEATED),
                new Reservation("ALEX",   "12:30", 6, 3, Reservation.Status.SEATED),
                new Reservation("ROBERT", "12:30", 4, 4, Reservation.Status.SEATED),
                new Reservation("ELAINE", "12:30", 4, 5, Reservation.Status.SEATED),
                new Reservation("DAVID",  "2:30",  2, 6, Reservation.Status.RESERVATION),
                new Reservation("RACHEL", "2:40",  5, 1, Reservation.Status.RESERVATION),
                new Reservation("DARRON", "2:30",  5, 2, Reservation.Status.RESERVATION)
        ));
    }

    private void refreshLists(List<Reservation> toShow) {
        seatedList.getChildren().clear();
        reservationList.getChildren().clear();

        for (Reservation r : toShow) {
            HBox row = buildRow(r);
            if (r.getStatus() == Reservation.Status.SEATED) {
                seatedList.getChildren().add(row);
            } else {
                reservationList.getChildren().add(row);
            }
        }
        if (seatedList.getChildren().isEmpty()) {
            seatedList.getChildren().add(emptyLabel("No seated guests"));
        }
        if (reservationList.getChildren().isEmpty()) {
            reservationList.getChildren().add(emptyLabel("No reservations"));
        }
    }

    private void filterLists(String query) {
        if (query == null || query.isBlank()) {
            refreshLists(allReservations);
            return;
        }
        String q = query.toLowerCase();
        List<Reservation> filtered = allReservations.stream()
                .filter(r -> r.getName().toLowerCase().contains(q)
                        || r.getTime().contains(q))
                .collect(Collectors.toList());
        refreshLists(filtered);
    }

    //ROW BUILDER
    private static final String[] ROW_COLORS = {
            "#C19A6B", "#D2793A", "#8B2635", "#5C4033", "#9E8E80", "#D8A0A8"
    };

    private HBox buildRow(Reservation r) {
        int idx = (r.getTableNumber() - 1) % ROW_COLORS.length;
        String bg = ROW_COLORS[idx];

        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(7, 12, 7, 12));
        row.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 4;");

        Label name = new Label(r.getName());
        name.setFont(Font.font("Georgia", FontWeight.BOLD, 12));
        name.setTextFill(Color.WHITE);
        name.setPrefWidth(80);

        Label time = new Label(r.getTime());
        time.setFont(Font.font("Georgia", 12));
        time.setTextFill(Color.web("#f5ede0"));
        time.setPrefWidth(50);

        Label guests = new Label(r.getGuests() + "G");
        guests.setFont(Font.font("Georgia", 12));
        guests.setTextFill(Color.web("#f5ede0"));
        guests.setPrefWidth(30);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Delete button
        Button del = new Button("✕");
        del.setStyle("-fx-background-color: transparent; -fx-text-fill: rgba(255,255,255,0.6); " +
                "-fx-font-size: 11px; -fx-cursor: hand; -fx-padding: 0 2 0 2;");
        del.setOnAction(e -> deleteReservation(r));

        // Status toggle button
        Button toggle = new Button(r.getStatus() == Reservation.Status.SEATED ? "⬇ Reserve" : "⬆ Seat");
        toggle.setStyle("-fx-background-color: rgba(0,0,0,0.25); -fx-text-fill: white; " +
                "-fx-font-size: 10px; -fx-background-radius: 4; -fx-cursor: hand; -fx-padding: 2 6 2 6;");
        toggle.setOnAction(e -> toggleStatus(r));

        row.getChildren().addAll(name, time, guests, spacer, toggle, del);
        return row;
    }

    private Label emptyLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #555555; -fx-font-family: 'Georgia'; " +
                "-fx-font-size: 11px; -fx-padding: 6 10 6 10;");
        return l;
    }

    // ACTIONS
    private void showAddDialog() {
        AddReservationDialog dialog = new AddReservationDialog(tables);
        Optional<Reservation> result = dialog.showAndWait();
        result.ifPresent(r -> {
            allReservations.add(r);
            csvService.saveAll(allReservations);
            refreshLists(allReservations);
            floorPlanView.refresh(allReservations);
        });
    }

    private void deleteReservation(Reservation r) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Remove " + r.getName() + " from the list?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Remove Reservation");
        confirm.setHeaderText(null);
        confirm.getDialogPane().setStyle("-fx-background-color: #1a1a1a;");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                allReservations.remove(r);
                csvService.saveAll(allReservations);
                refreshLists(allReservations);
                floorPlanView.refresh(allReservations);
            }
        });
    }

    private void toggleStatus(Reservation r) {
        if (r.getStatus() == Reservation.Status.SEATED) {
            r.setStatus(Reservation.Status.RESERVATION);
        } else {
            r.setStatus(Reservation.Status.SEATED);
        }
        csvService.saveAll(allReservations);
        refreshLists(allReservations);
        floorPlanView.refresh(allReservations);
    }

    // HELPER
    private Label styledSectionHeader(String text, String bgColor) {
        Label l = new Label(text);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setAlignment(Pos.CENTER);
        l.setFont(Font.font("Georgia", FontWeight.BOLD, 14));
        l.setTextFill(Color.WHITE);
        l.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 20; " +
                "-fx-padding: 6 20 6 20;");
        return l;
    }

    public HBox getRoot() { return root; }
}

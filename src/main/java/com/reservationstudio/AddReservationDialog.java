package com.reservationstudio;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Optional;

public class AddReservationDialog extends Dialog<Reservation> {

    private final TextField nameField = new TextField();
    private final TextField timeField = new TextField();
    private final Spinner<Integer> guestSpinner = new Spinner<>(1, 20, 2);
    private final ComboBox<String> tableCombo = new ComboBox<>();
    private final ComboBox<String> statusCombo = new ComboBox<>();

    public AddReservationDialog(List<RestaurantTable> tables) {
        setTitle("Add Reservation");
        setHeaderText(null);

        // Style the dialog pane
        DialogPane dp = getDialogPane();
        dp.setStyle("-fx-background-color: #1a1a1a;");

        // Title label
        Label title = new Label("NEW RESERVATION");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#e8c5a0"));

        // Form grid
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 30, 10, 30));

        // Style labels
        String labelStyle = "-fx-text-fill: #aaaaaa; -fx-font-family: 'Georgia'; -fx-font-size: 12px;";
        String fieldStyle = "-fx-background-color: #2d2d2d; -fx-text-fill: white; " +
                "-fx-border-color: #555555; -fx-border-radius: 4; -fx-background-radius: 4; " +
                "-fx-font-size: 13px;";

        Label nameLabel = new Label("Guest Name:");
        nameLabel.setStyle(labelStyle);
        nameField.setPromptText("e.g. JOHN");
        nameField.setStyle(fieldStyle);
        nameField.setPrefWidth(200);

        Label timeLabel = new Label("Time:");
        timeLabel.setStyle(labelStyle);
        timeField.setPromptText("e.g. 2:30");
        timeField.setStyle(fieldStyle);

        Label guestLabel = new Label("Guests:");
        guestLabel.setStyle(labelStyle);
        guestSpinner.setStyle(fieldStyle);
        guestSpinner.setEditable(true);

        Label tableLabel = new Label("Table:");
        tableLabel.setStyle(labelStyle);
        for (RestaurantTable t : tables) {
            tableCombo.getItems().add("Table " + t.getNumber() + " (cap: " + t.getCapacity() + ")");
        }
        if (!tableCombo.getItems().isEmpty()) tableCombo.getSelectionModel().selectFirst();
        tableCombo.setStyle(fieldStyle + "-fx-mark-color: white;");
        tableCombo.setPrefWidth(200);

        Label statusLabel = new Label("Status:");
        statusLabel.setStyle(labelStyle);
        statusCombo.getItems().addAll("RESERVATION", "SEATED");
        statusCombo.getSelectionModel().selectFirst();
        statusCombo.setStyle(fieldStyle);
        statusCombo.setPrefWidth(200);

        grid.add(nameLabel,   0, 0); grid.add(nameField,   1, 0);
        grid.add(timeLabel,   0, 1); grid.add(timeField,   1, 1);
        grid.add(guestLabel,  0, 2); grid.add(guestSpinner,1, 2);
        grid.add(tableLabel,  0, 3); grid.add(tableCombo,  1, 3);
        grid.add(statusLabel, 0, 4); grid.add(statusCombo, 1, 4);

        VBox content = new VBox(14, title, new Separator(), grid);
        content.setPadding(new Insets(10));
        dp.setContent(content);

        // Buttons
        ButtonType addBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dp.getButtonTypes().addAll(addBtn, cancelBtn);

        // Style buttons
        Button addButton = (Button) dp.lookupButton(addBtn);
        addButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; " +
                "-fx-font-family: 'Georgia'; -fx-font-weight: bold; -fx-font-size: 13px; " +
                "-fx-background-radius: 6;");

        Button cancelButton = (Button) dp.lookupButton(cancelBtn);
        cancelButton.setStyle("-fx-background-color: #555555; -fx-text-fill: white; " +
                "-fx-font-family: 'Georgia'; -fx-font-size: 13px; -fx-background-radius: 6;");

        // Result converter
        setResultConverter(dialogButton -> {
            if (dialogButton == addBtn) {
                String name = nameField.getText().trim().toUpperCase();
                String time = timeField.getText().trim();
                int guests = guestSpinner.getValue();
                int tableIdx = tableCombo.getSelectionModel().getSelectedIndex();
                int tableNum = (tableIdx >= 0 && tableIdx < tables.size())
                        ? tables.get(tableIdx).getNumber() : 1;
                Reservation.Status status = statusCombo.getValue().equals("SEATED")
                        ? Reservation.Status.SEATED : Reservation.Status.RESERVATION;
                if (!name.isEmpty() && !time.isEmpty()) {
                    return new Reservation(name, time, guests, tableNum, status);
                }
            }
            return null;
        });
    }
}

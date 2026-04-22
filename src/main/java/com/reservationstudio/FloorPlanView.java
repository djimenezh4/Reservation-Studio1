package com.reservationstudio;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FloorPlanView {

    // Table colors matching screenshot palette
    private static final String[] TABLE_COLORS = {
            "#C19A6B", // Table 1 – tan
            "#D2793A", // Table 2 – orange-brown
            "#8B2635", // Table 3 – deep red
            "#5C4033", // Table 4 – dark brown
            "#9E8E80", // Table 5 – grey
            "#D8A0A8"  // Table 6 – pink
    };

    private static final String FREE_OVERLAY   = "rgba(255,255,255,0.15)";
    private static final String SEATED_BORDER  = "#FF5555";
    private static final String RESERVED_BORDER = "#FFDD55";

    private final Pane canvas = new Pane();
    private final List<RestaurantTable> tables;
    // Map: tableNumber -> StackPane node
    private final Map<Integer, StackPane> tableNodes = new HashMap<>();

    public FloorPlanView(List<RestaurantTable> tables) {
        this.tables = tables;
        buildLayout();
    }

    private void buildLayout() {
        // Teal background
        canvas.setStyle("-fx-background-color: #3d8fa1;");
        canvas.setPrefSize(680, 700);

        // Floor Plan pill header
        Label header = new Label("Floor Plan");
        header.setFont(Font.font("Georgia", FontWeight.NORMAL, 18));
        header.setTextFill(Color.WHITE);
        header.setStyle("-fx-background-color: #8a7a70; -fx-background-radius: 20; " +
                "-fx-padding: 6 30 6 30;");
        header.setLayoutX(200);
        header.setLayoutY(20);
        canvas.getChildren().add(header);

        // Front Desk label
        Label frontDesk = new Label("Front Desk");
        frontDesk.setFont(Font.font("Georgia", FontWeight.NORMAL, 14));
        frontDesk.setTextFill(Color.WHITE);
        frontDesk.setStyle("-fx-background-color: #8a7a70; -fx-background-radius: 14; " +
                "-fx-padding: 8 16 8 16;");
        frontDesk.setRotate(90);
        frontDesk.setLayoutX(610);
        frontDesk.setLayoutY(430);
        canvas.getChildren().add(frontDesk);

        // Draw tables
        for (int i = 0; i < tables.size(); i++) {
            RestaurantTable t = tables.get(i);
            String color = TABLE_COLORS[Math.min(i, TABLE_COLORS.length - 1)];
            StackPane node = createTableNode(t, color);
            node.setLayoutX(t.getLayoutX());
            node.setLayoutY(t.getLayoutY());
            tableNodes.put(t.getNumber(), node);
            canvas.getChildren().add(node);
        }
    }

    private StackPane createTableNode(RestaurantTable t, String color) {
        StackPane sp = new StackPane();
        sp.setPrefSize(t.getWidth(), t.getHeight());

        Rectangle bg = new Rectangle(t.getWidth(), t.getHeight());
        bg.setFill(Color.web(color));
        bg.setArcWidth(30);
        bg.setArcHeight(30);

        Label numLabel = new Label(String.valueOf(t.getNumber()));
        numLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        numLabel.setTextFill(Color.web("#f5f0eb"));

        sp.getChildren().addAll(bg, numLabel);
        return sp;
    }

    // Refresh table based on current reservation list
    public void refresh(List<Reservation> reservations) {
        // Reset all tables to base color
        for (int i = 0; i < tables.size(); i++) {
            RestaurantTable t = tables.get(i);
            StackPane sp = tableNodes.get(t.getNumber());
            if (sp == null) continue;
            Rectangle bg = (Rectangle) sp.getChildren().get(0);
            String baseColor = TABLE_COLORS[Math.min(i, TABLE_COLORS.length - 1)];
            bg.setFill(Color.web(baseColor));
            bg.setStroke(null);
            bg.setStrokeWidth(0);
        }

        // Apply reservation state
        for (Reservation r : reservations) {
            StackPane sp = tableNodes.get(r.getTableNumber());
            if (sp == null) continue;
            Rectangle bg = (Rectangle) sp.getChildren().get(0);
            if (r.getStatus() == Reservation.Status.SEATED) {
                bg.setStroke(Color.web(SEATED_BORDER));
                bg.setStrokeWidth(4);
            } else {
                bg.setStroke(Color.web(RESERVED_BORDER));
                bg.setStrokeWidth(3);
            }
        }
    }

    public Pane getRoot() { return canvas; }
}

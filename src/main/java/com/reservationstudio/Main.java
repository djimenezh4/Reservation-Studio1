package com.reservationstudio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/reservationstudio/main.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 750);
        scene.getStylesheets().add(getClass().getResource("/com/reservationstudio/style.css").toExternalForm());
        primaryStage.setTitle("Reservation Studio");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(650);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

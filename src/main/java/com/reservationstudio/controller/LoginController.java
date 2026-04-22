package com.reservationstudio.controller;

import com.reservationstudio.data.DataLoader;
import com.reservationstudio.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.util.List;

import static com.reservationstudio.data.DataLoader.loadUsers;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin(){
        //1.get username and password from the fields
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean found = false;

        //2.load users from Users.csv
        List<User> users = DataLoader.loadUsers("Users.csv");

        //3. check if any user matches

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                found = true;

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/reservationstudio/main.fxml"));
                    Stage mainStage = new Stage();
                    mainStage.setScene(new Scene(loader.load(), 1200, 750));
                    mainStage.setTitle("Reservation Studio");
                    mainStage.show();

                    //close login window
                    Stage loginStage = (Stage) usernameField.getScene().getWindow();
                    loginStage.close();
                } catch (Exception e) {
                    errorLabel.setText(("Error opening dashboard!"));
                }
                break;
            }
        }
        if (!found) {
                errorLabel.setText("Error: Invalid username or password");
            }
        }
        //4. if yes -> open main dashboard
        //5. if no -> show error message
    }

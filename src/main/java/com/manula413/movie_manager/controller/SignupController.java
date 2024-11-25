package com.manula413.movie_manager.controller;

import com.manula413.movie_manager.database.DatabaseConnection;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Scene;


import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mindrot.jbcrypt.BCrypt;



public class SignupController {

    private static final Logger logger = LoggerFactory.getLogger(SignupController.class);


    @FXML
    private Button cancelButton;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField displaynNmeTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private PasswordField confirmPasswordPasswordField;
    @FXML
    private Hyperlink loginHyperlink;
    @FXML
    private Label signUpMessageLabel;


    public void cancelButtonAction() {

        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }


    public void loginHyperlinkAction() {
        // Create a ProgressIndicator
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(50, 50);

        // Get the current stage
        Stage stage = (Stage) loginHyperlink.getScene().getWindow();

        // Show the loading indicator on a temporary scene
        BorderPane loadingPane = new BorderPane(loadingIndicator);
        Scene loadingScene = new Scene(loadingPane, stage.getScene().getWidth(), stage.getScene().getHeight());
        stage.setScene(loadingScene);

        // Load the new scene in a background thread
        new Thread(() -> {
            try {
                // Simulate a delay (e.g., 2 seconds)
                Thread.sleep(200);

                // Load the new scene (login.fxml)
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/manula413/movie_manager/login.fxml"));
                BorderPane loginPage = fxmlLoader.load();

                // Switch back to the new scene on the JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    Scene scene = new Scene(loginPage);
                    stage.setScene(scene);
                });

            } catch (InterruptedException | IOException ex) {
                logger.error("Error loading Sign-Up page.", ex);

                // Handle InterruptedException properly by preserving the interrupt status
                if (ex instanceof InterruptedException) {
                    Thread.currentThread().interrupt();  // Restore the interrupt status
                }

                javafx.application.Platform.runLater(() -> {
                    // Handle the error (e.g., show an alert)
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load the login page.");
                    alert.showAndWait();
                    // Reset the scene to the original scene (with the loading indicator still visible)
                    stage.setScene(stage.getScene());
                });
            }
        }).start();
    }


    public void submitButtonAction() {

        String username = usernameTextField.getText();
        String displayName = displaynNmeTextField.getText();
        String password = passwordPasswordField.getText();
        String confirmPassword = confirmPasswordPasswordField.getText();

        if (username.isEmpty() || displayName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            signUpMessageLabel.setText("All fields must be filled! ");
            return;
        }

        if (username.length() > 20) {
            signUpMessageLabel.setText("Username must not exceed 20 characters.");
            return;
        }

        if (!username.matches("\\w+")) {
            signUpMessageLabel.setText("Username can only contain letters, numbers, and underscores.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            signUpMessageLabel.setText("Password do not match! ");
            return;
        }


        validateSignUp(username, displayName, password);

    }

    public void validateSignUp(String username, String displayName, String password) {
        DatabaseConnection connectNow = new DatabaseConnection();

        String checkUserQuery = "SELECT COUNT(*) FROM user WHERE userName = ?";
        String insertUserQuery = "INSERT INTO user (userName, displayName, password) VALUES (?, ?, ?)";

        try (Connection connectDB = connectNow.getConnection();
             PreparedStatement checkUserStmt = connectDB.prepareStatement(checkUserQuery);
             PreparedStatement insertUserStmt = connectDB.prepareStatement(insertUserQuery)) {

            // Check if the username already exists
            checkUserStmt.setString(1, username);
            ResultSet resultSet = checkUserStmt.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                logger.info("Username '{}' already exists.", username);
                signUpMessageLabel.setText("Username already exists! Please choose another.");
                return;
            }

            // Hash the password
            String hashedPassword = hashPassword(password);

            // Insert the new user
            insertUserStmt.setString(1, username);
            insertUserStmt.setString(2, displayName);
            insertUserStmt.setString(3, hashedPassword);
            int rowInserted = insertUserStmt.executeUpdate();

            if (rowInserted > 0) {
                logger.info("User '{}' signed up successfully.", username);
                signUpMessageLabel.setText("Sign-up Successful!");
                signUpMessageLabel.setStyle("-fx-text-fill: green;");
            } else {
                logger.warn("Failed to insert user '{}'.", username);
                signUpMessageLabel.setText("Sign-up failed. Please try again.");
            }

        } catch (SQLException e) {
            logger.error("Database error occurred during sign-up for username '{}'.", username, e);
            signUpMessageLabel.setText("Sign-up failed due to a server issue. Please try again later.");
        }
    }

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }





}

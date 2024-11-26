package com.manula413.movie_manager.controller;

import com.manula413.movie_manager.database.DatabaseConnection;
import com.manula413.movie_manager.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private Button cancelButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField usernameTextFeild;
    @FXML
    private PasswordField passwordPasswordFeild;
    @FXML
    private Hyperlink signUpHyperLink;


    public void signUpHyperLinkAction() {
        // Create a ProgressIndicator
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(50, 50);

        // Get the current stage
        Stage stage = (Stage) signUpHyperLink.getScene().getWindow();

        // Show the loading indicator on a temporary scene
        BorderPane loadingPane = new BorderPane(loadingIndicator);
        Scene loadingScene = new Scene(loadingPane, stage.getScene().getWidth(), stage.getScene().getHeight());
        stage.setScene(loadingScene);

        // Load the new scene in a background thread
        Thread thread = new Thread(() -> {
            try {
                // Simulate a delay (e.g., 2 seconds)
                Thread.sleep(200);

                // Load the new scene (login.fxml)
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/manula413/movie_manager/signUp.fxml"));
                BorderPane signUpPage = fxmlLoader.load();

                // Switch back to the new scene on the JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    Scene scene = new Scene(signUpPage);
                    stage.setScene(scene);
                });

            } catch (InterruptedException ex) {
                // Re-interrupt the thread to preserve the interrupted status
                Thread.currentThread().interrupt();
                logger.error("Thread was interrupted while loading Sign-Up page.", ex);
                javafx.application.Platform.runLater(() -> {
                    // Handle the error (e.g., show an alert)
                    Alert alert = new Alert(Alert.AlertType.ERROR, "The operation was interrupted.");
                    alert.showAndWait();
                });
            } catch (IOException ex) {
                logger.error("Error loading Sign-Up page.", ex);
                javafx.application.Platform.runLater(() -> {
                    // Handle the error (e.g., show an alert)
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load the signUpPage page.");
                    alert.showAndWait();
                    stage.setScene(stage.getScene());
                });
            }
        });

        // Mark thread as a daemon thread to avoid blocking application shutdown
        thread.setDaemon(true);
        thread.start();
    }



    public void loginButtonAction() {

        if (!usernameTextFeild.getText().isBlank() && !passwordPasswordFeild.getText().isBlank()) {
            validateLogin();
        } else {
            loginMessageLabel.setText("Enter Valid username and password!");
        }
    }

    public void cancelButtonAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void validateLogin() {

        DatabaseConnection connectNow = new DatabaseConnection();
        String fetchPasswordQuery = "SELECT password FROM moviedb.user WHERE userName = ?";

        try (Connection connectDB = connectNow.getConnection(); PreparedStatement preparedStatement = connectDB.prepareStatement(fetchPasswordQuery)) {

            preparedStatement.setString(1, usernameTextFeild.getText());

            try (ResultSet queryResult = preparedStatement.executeQuery()) {
                if (queryResult.next()) {
                    String hashedPassword = queryResult.getString("password");
                    String plainPassword = passwordPasswordFeild.getText();

                    if (verifyPassword(plainPassword, hashedPassword)) {

                        Session.getInstance().setUsername(usernameTextFeild.getText()); // Store username in session

                        Stage stage = (Stage) loginMessageLabel.getScene().getWindow();
                        MainPanelController mainpanel = new MainPanelController();
                        mainpanel.loadMainPanelDefault(stage);

                    } else {
                        loginMessageLabel.setText("Invalid Username or Password!");
                    }
                } else {
                    loginMessageLabel.setText("Invalid Username or Password!");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            logger.error("Database connection or query error.", e);
            loginMessageLabel.setText("An error occurred. Please try again.");
        }
    }


    // Method to verify a password
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }


}

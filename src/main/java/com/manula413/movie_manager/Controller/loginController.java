package com.manula413.movie_manager.Controller;

import com.manula413.movie_manager.database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class loginController {

    private static final Logger logger = LoggerFactory.getLogger(loginController.class);

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


    public void signUpHyperLinkAction(ActionEvent e) {
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
        new Thread(() -> {
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

            } catch (InterruptedException | IOException ex) {
                logger.error("Error loading Sign-Up page.", ex);
                javafx.application.Platform.runLater(() -> {
                    // Handle the error (e.g., show an alert)
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load the signUpPage page.");
                    alert.showAndWait();
                    // Reset the scene to the original scene (with the loading indicator still visible)
                    stage.setScene(stage.getScene());
                });
            }
        }).start();
    }


    public void loginButtonAction(ActionEvent e) {

        if (usernameTextFeild.getText().isBlank() == false && passwordPasswordFeild.getText().isBlank() == false) {


            validateLogin();
        } else {
            loginMessageLabel.setText("Enter Valid username and password!");
        }
    }

    public void cancelButtonAction(ActionEvent e) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void validateLogin() {

        DatabaseConnection connectNow = new DatabaseConnection();
        try (Connection connectDB = connectNow.getConnection()) {


            String verifyLogin = "SELECT count(1) FROM moviedb.user WHERE userName = ? AND password = ?";

            try (PreparedStatement preparedStatement = connectDB.prepareStatement(verifyLogin)) {
                preparedStatement.setString(1, usernameTextFeild.getText());
                preparedStatement.setString(2, passwordPasswordFeild.getText());

                try (ResultSet queryResult = preparedStatement.executeQuery()) {

                    while (queryResult.next()) {
                        if (queryResult.getInt(1) == 1) {
                            loginMessageLabel.setText("Welcome!");
                        } else {
                            loginMessageLabel.setText("Invalid Username or Password!");
                        }
                    }

                }

            } catch (SQLException e) {

                logger.error("Error while executing the query.", e);

            }

        } catch (
                SQLException e) {
            logger.error("Database connection error.", e);

        }


    }


}

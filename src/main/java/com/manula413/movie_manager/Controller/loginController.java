package com.manula413.movie_manager.Controller;

import com.manula413.movie_manager.database.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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


    public void loginButtonAction(ActionEvent e) {
        loginMessageLabel.setText("You Tried to Login!");
        if (usernameTextFeild.getText().isBlank() == false && passwordPasswordFeild.getText().isBlank() == false) {
            //loginMessageLabel.setText("Good Login!");

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
            ;

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

package com.manula413.movie_manager.controller;

import com.manula413.movie_manager.database.DatabaseConnection;
import com.manula413.movie_manager.model.MovieDetails;
import com.manula413.movie_manager.util.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class WatchedListController implements Initializable {

    @FXML
    private TableView<MovieDetails> watchedListTableView; // Correct generic type
    @FXML
    private Button addMoviesNavButton;
    @FXML
    private Button watchedListNavButton;
    @FXML
    private Button watchLaterNavButton;
    @FXML
    private Label usernameLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
     //   setupTableColumns();
        String username = Session.getInstance().getUsername();
        setUsernameLabel(username);
     //   populateWatchedMoviesTable();
    }

    //



    public void navigateTo(String fxmlPath, String title, ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane panel = loader.load();
            Scene scene = new Scene(panel, 1300, 800);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addMoviesNavButtonAction(ActionEvent event) {
        navigateTo("/com/manula413/movie_manager/mainPanel.fxml", "Add Movie", event);
    }

    public void watchedListNavButtonAction(ActionEvent event) {
        navigateTo("/com/manula413/movie_manager/watchedList.fxml", "Watched List", event);
    }

    public void watchLaterNavButtonAction(ActionEvent event) {
        navigateTo("/com/manula413/movie_manager/watchLaterList.fxml", "Watch Later List", event);
    }

    public void setUsernameLabel(String username) {
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + username + "!");
        }
    }
}

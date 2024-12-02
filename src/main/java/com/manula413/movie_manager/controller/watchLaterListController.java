package com.manula413.movie_manager.controller;

import com.manula413.movie_manager.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class watchLaterListController implements Initializable {
    @FXML
    private TableView<Object> watchedListTableView; // Use generic type for type safety

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
        // Create TableColumn instances
        TableColumn<Object, String> column1 = new TableColumn<>("Title");
        TableColumn<Object, String> column2 = new TableColumn<>("Year");
        TableColumn<Object, String> column3 = new TableColumn<>("Genre");
        TableColumn<Object, String> column4 = new TableColumn<>("IMDB");
        TableColumn<Object, String> column5 = new TableColumn<>("Comment");
        TableColumn<Object, String> column6 = new TableColumn<>("Actions");

        // Add columns to the TableView
        watchedListTableView.getColumns().addAll(column1, column2,column3,column4,column5,column6);

        String username = Session.getInstance().getUsername();
        setUsernameLabel(username);
    }

    public void navigateTo(String fxmlPath, String title, ActionEvent event) {
        try {
            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Load the specified FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane panel = loader.load();

            // Set the new scene
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

package com.manula413.movie_manager.controller;

import com.manula413.movie_manager.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SidebarController {

    @FXML
    private Button watchLaterNavButton;

    @FXML
    private Button addMoviesNavButton;

    @FXML
    private Button watchedListNavButton;

    private MainPanelController mainPanelController; // Reference to the MainPanelController
    private Stage primaryStage; // Reference to the application's primary stage

    public SidebarController() {
        // Constructor for any initialization logic (if needed)
    }

    @FXML
    private void initialize() {
        // This method is automatically called after FXML elements are loaded
    }

    public void setMainPanelController(MainPanelController mainPanelController) {
        this.mainPanelController = mainPanelController;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    // Method to load both sidebar and main panel
    public void loadMainPanelWithSidebar(Stage stage) throws IOException {
        // Load the main panel
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/com/manula413/movie_manager/mainPanel.fxml"));
        AnchorPane mainPanel = mainLoader.load();

        // Load the sidebar
        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/com/manula413/movie_manager/sidebar.fxml"));
        VBox sidebar = sidebarLoader.load(); // Ensure this matches the root element in sidebar.fxml

        // Combine them in a BorderPane
        BorderPane rootLayout = new BorderPane();
        rootLayout.setCenter(mainPanel); // Main area
        rootLayout.setLeft(sidebar);     // Sidebar

        // Initialize controllers
        MainPanelController mainController = mainLoader.getController();
        SidebarController sidebarController = sidebarLoader.getController();

        // Pass data to controllers if needed
        String displayName = Session.getInstance().getDisplayName();
        mainController.setDisplayNameLabel(displayName);

        // Create and show the scene
        Scene scene = new Scene(rootLayout);
        stage.setTitle("Main Application");
        stage.setScene(scene);
        stage.show();
    }

    // Navigation actions
    public void addMoviesNavButtonAction(ActionEvent event) {
        navigateTo("/com/manula413/movie_manager/mainPanel.fxml", "Add Movie", event);
    }

    public void watchedListNavButtonAction(ActionEvent event) {
        navigateTo("/com/manula413/movie_manager/watchedList.fxml", "Watched List", event);
    }

    public void watchLaterNavButtonAction(ActionEvent event) {
        navigateTo("/com/manula413/movie_manager/watchLaterList.fxml", "Watch Later List", event);
    }

    public void navigateTo(String fxmlPath, String title, ActionEvent event) {
        try {
            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Load the specified FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane panel = loader.load();

            // Load the sidebar as well
            FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/com/manula413/movie_manager/sidebar.fxml"));
            VBox sidebar = sidebarLoader.load(); // Load sidebar

            // Combine the main panel and sidebar into a BorderPane
            BorderPane rootLayout = new BorderPane();
            rootLayout.setCenter(panel);    // Set the main panel
            rootLayout.setLeft(sidebar);    // Set the sidebar

            // Set the new scene with the BorderPane layout
            Scene scene = new Scene(rootLayout, 1500, 800);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

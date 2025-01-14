package com.manula413.movie_manager.controller;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class SidebarController {

    @FXML
    private Button watchLaterNavButton;

    @FXML
    private Button addMoviesNavButton;

    @FXML
    private Button watchedListNavButton;

    @FXML
    private Button hideSidebarButton;  // Added the hide sidebar button

    @FXML
    private VBox sidebarContainer; // This links to the VBox in the FXML

    private MainPanelController mainPanelController; // Reference to the MainPanelController
    private Stage primaryStage; // Reference to the application's primary stage

    public SidebarController() {
        // Constructor for any initialization logic (if needed)
    }

    @FXML
    private void initialize() {
        if (sidebarContainer != null) {
            System.out.println("Sidebar Container Initialized");
        } else {
            System.out.println("Sidebar Container is null");
        }
    }

    public void setMainPanelController(MainPanelController mainPanelController) {
        this.mainPanelController = mainPanelController;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    /*
    public void loadMainPanelWithSidebar(Stage stage) throws IOException {
        // Load the main panel
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/com/manula413/movie_manager/mainPanel.fxml"));
        AnchorPane mainPanel = mainLoader.load();
        mainPanel.setPrefSize(1300, 800);

        // Load the sidebar
        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/com/manula413/movie_manager/sidebar.fxml"));
        VBox sidebar = sidebarLoader.load();
        sidebar.setPrefWidth(300);

        // Wrap sidebar in a Pane
        Pane sidebarContainer = new Pane(sidebar);
        sidebarContainer.setPrefSize(300, 800);
        sidebarContainer.setTranslateX(0);  // Show sidebar by default
        sidebarContainer.setMouseTransparent(false); // Allow interaction when shown

        // Toggle Button
        Button toggleSidebarButton = new Button("☰");
        toggleSidebarButton.setStyle("-fx-font-size: 18px;");
        toggleSidebarButton.setOnAction(event -> toggleSidebar(sidebarContainer));

        // Place the button on the main panel
        AnchorPane.setTopAnchor(toggleSidebarButton, 10.0);
        AnchorPane.setLeftAnchor(toggleSidebarButton, 10.0);
        mainPanel.getChildren().add(toggleSidebarButton);

        // StackPane to layer components
        StackPane rootLayout = new StackPane();
        rootLayout.getChildren().addAll(mainPanel, sidebarContainer);

        // Align sidebar to the left
        StackPane.setAlignment(sidebarContainer, Pos.CENTER_LEFT);

        // Scene setup
        Scene scene = new Scene(rootLayout, 1300, 800);
        stage.setTitle("Main Application");
        stage.setScene(scene);
        stage.show();

        // Handle clicks outside sidebar to hide it
        handleOutsideClickToHideSidebar(rootLayout, sidebarContainer);
    }

     */

    private void toggleSidebar(Pane sidebarContainer) {
        double currentX = sidebarContainer.getTranslateX();
        double targetX = (currentX == 0) ? -300 : 0;  // Slide the sidebar off-screen if it's visible

        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebarContainer);
        transition.setToX(targetX);

        transition.setOnFinished(event -> {
            // Toggle interactivity based on the sidebar's visibility
            sidebarContainer.setMouseTransparent(targetX == -300);  // Make it non-interactive when hidden
        });

        transition.play();
    }


    // Hide sidebar button action (move sidebar off-screen)
    @FXML
    private void hideSidebarButtonAction(ActionEvent event) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebarContainer);
        transition.setToX(-300); // Slide sidebar out

        transition.setOnFinished(e -> {
            sidebarContainer.setVisible(false); // Hide sidebar completely
        });

        transition.play();
    }


/*
    private void setMainUIInteractable(ActionEvent event) {
        // Get the current stage
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Create the scene and set the stage (re-apply the scene with the sidebar hidden)
        Scene scene = new Scene(stage.getScene().getRoot(), 1300, 800); // Reuse root to keep UI state
        stage.setScene(scene);
        stage.show();
    }

 */
    

    // Handle outside clicks to hide the sidebar
    private void handleOutsideClickToHideSidebar(StackPane rootLayout, Pane sidebarContainer) {
        rootLayout.setOnMouseClicked(event -> {
            // Check if the click is outside the sidebar
            if (!sidebarContainer.contains(event.getSceneX(), event.getSceneY())) {
                // Hide the sidebar by translating it to the left (out of view)
                TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebarContainer);
                transition.setToX(-300); // Moves the sidebar out of view
                transition.setOnFinished(e -> sidebarContainer.setMouseTransparent(true)); // Disable interaction when hidden
                transition.play();
            }
        });
    }

    public void navigateTo(String fxmlPath, String title, ActionEvent event) {
        try {
            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Load the target FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane panel = loader.load();

            // Access controller to get btnSidebar
            Object controller = loader.getController();
            Button btnSidebar = null;

            if (controller instanceof MainPanelController) {
                btnSidebar = ((MainPanelController) controller).getBtnSidebar();
            }

            // Load the sidebar
            FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/com/manula413/movie_manager/sidebar.fxml"));
            VBox sidebar = sidebarLoader.load();

            // Sidebar container
            Pane sidebarContainer = new Pane(sidebar);
            sidebarContainer.setPrefSize(300, 800);
            sidebarContainer.setTranslateX(-300);
            sidebarContainer.setMouseTransparent(true);

            // Bind toggle action to existing button
            if (btnSidebar != null) {
                btnSidebar.setOnAction(e -> toggleSidebar(sidebarContainer));
            }

            // StackPane setup
            StackPane rootLayout = new StackPane();
            rootLayout.getChildren().addAll(panel, sidebarContainer);
            StackPane.setAlignment(sidebarContainer, Pos.CENTER_LEFT);

            // Scene setup
            Scene scene = new Scene(rootLayout, 1300, 800);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
}

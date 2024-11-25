package com.manula413.movie_manager;

import com.manula413.movie_manager.controller.MainPanelController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class TestApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load mainPanel.fxml
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/com/manula413/movie_manager/mainPanel.fxml"));
        Parent mainPanel = mainLoader.load(); // The root layout of mainPanel.fxml

        // Load movieDetails.fxml
        FXMLLoader movieLoader = new FXMLLoader(getClass().getResource("/com/manula413/movie_manager/movieDetails.fxml"));
        AnchorPane movieDetailsAnchorPane = movieLoader.load(); // Root layout of movieDetails.fxml

        // Get the MainPanelController to access the loadingBorderPane
        MainPanelController mainController = mainLoader.getController();
        mainController.getLoadingBorderPane().setCenter(movieDetailsAnchorPane); // Inject movieDetails into the BorderPane

        // Set up the scene and stage
        Scene scene = new Scene(mainPanel, 1300, 830);
        primaryStage.setTitle("MainPanel with MovieDetails");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

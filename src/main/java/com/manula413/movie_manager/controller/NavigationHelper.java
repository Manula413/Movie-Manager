package com.manula413.movie_manager.controller;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.io.IOException;

public class NavigationHelper {

    public static void navigateTo(String fxmlPath, String title, ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Load the main content (MainPanel or other UIs)
            FXMLLoader mainLoader = new FXMLLoader(NavigationHelper.class.getResource(fxmlPath));
            AnchorPane mainContent = mainLoader.load();

            // Load the sidebar
            FXMLLoader sidebarLoader = new FXMLLoader(NavigationHelper.class.getResource("/com/manula413/movie_manager/sidebar.fxml"));
            VBox sidebarPane = sidebarLoader.load();
            sidebarPane.setPrefSize(300, 800);
            sidebarPane.setTranslateX(-300);
            sidebarPane.setMouseTransparent(true);

            // Pass sidebar to SidebarController
            SidebarController sidebarController = sidebarLoader.getController();
            sidebarController.setSidebarContainer(sidebarPane);

            // Pass sidebar to MainPanelController if applicable
            if (mainLoader.getController() instanceof MainPanelController mainController) {
                mainController.setSidebarContainer(sidebarPane);
            }

            // Root layout
            StackPane rootLayout = new StackPane();
            rootLayout.getChildren().addAll(mainContent, sidebarPane);
            StackPane.setAlignment(sidebarPane, Pos.CENTER_LEFT);

            Scene scene = new Scene(rootLayout, 1300, 800);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package com.manula413.movie_manager.util;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;

public class SidebarUtil {

    // Method to initialize sidebar and bind toggle to a button
    public static Pane initializeSidebar(Button triggerButton) throws IOException {
        // Load the sidebar
        FXMLLoader sidebarLoader = new FXMLLoader(SidebarUtil.class.getResource("/com/manula413/movie_manager/sidebar.fxml"));
        VBox sidebar = sidebarLoader.load();

        // Sidebar container setup
        Pane sidebarContainer = new Pane(sidebar);
        sidebarContainer.setPrefSize(300, 800);
        sidebarContainer.setTranslateX(-300);  // Initially hidden
        sidebarContainer.setMouseTransparent(true);

        // Bind toggle to the provided button
        triggerButton.setOnAction(event -> toggleSidebar(sidebarContainer));

        return sidebarContainer;
    }

    // Sidebar toggle animation
    private static void toggleSidebar(Pane sidebarContainer) {
        double currentX = sidebarContainer.getTranslateX();
        double targetX = (currentX == 0) ? -300 : 0;

        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebarContainer);
        transition.setToX(targetX);
        transition.setOnFinished(event -> {
            sidebarContainer.setMouseTransparent(targetX == -300);
        });
        transition.play();
    }
}

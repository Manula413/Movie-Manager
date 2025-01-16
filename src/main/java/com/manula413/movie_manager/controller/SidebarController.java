package com.manula413.movie_manager.controller;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SidebarController {

    @FXML
    private VBox sidebarContainer;

    @FXML
    private Button hideSidebarButton;

    @FXML
    private Button watchLaterNavButton;

    @FXML
    private Button addMoviesNavButton;

    @FXML
    private Button watchedListNavButton;


    @FXML
    private VBox sidebarPane;

    @FXML
    public void setSidebarContainer(Pane sidebarContainer) {
        this.sidebarContainer = (VBox) sidebarContainer;
    }

    private void toggleSidebar() {
        if (sidebarPane == null) return;

        double currentX = sidebarPane.getTranslateX();
        double targetX = (currentX == 0) ? -300 : 0;

        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebarPane);
        transition.setToX(targetX);
        transition.setOnFinished(e -> sidebarPane.setMouseTransparent(targetX == -300));
        transition.play();
    }

    @FXML
    private void addMoviesNavButtonAction(ActionEvent event) {
        NavigationHelper.navigateTo("/com/manula413/movie_manager/mainPanel.fxml", "Add Movie", event);
    }

    @FXML
    private void watchedListNavButtonAction(ActionEvent event) {
        NavigationHelper.navigateTo("/com/manula413/movie_manager/watchedList.fxml", "Watched List", event);
    }

    @FXML
    private void watchLaterNavButtonAction(ActionEvent event) {
        NavigationHelper.navigateTo("/com/manula413/movie_manager/watchLaterList.fxml", "Watch Later List", event);
    }
}

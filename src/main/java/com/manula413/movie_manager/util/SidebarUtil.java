package com.manula413.movie_manager.util;

import javafx.animation.TranslateTransition;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class SidebarUtil {

    public static void attachSidebarBehavior(Button btnSidebar, Pane sidebarContainer) {
        btnSidebar.setOnAction(event -> toggleSidebar(sidebarContainer));
    }

    private static void toggleSidebar(Pane sidebarContainer) {
        double targetX = (sidebarContainer.getTranslateX() == 0) ? -300 : 0;

        // Smooth sliding animation
        TranslateTransition slide = new TranslateTransition(Duration.millis(300), sidebarContainer);
        slide.setToX(targetX);
        slide.play();

        // Disable interaction when hidden
        sidebarContainer.setMouseTransparent(targetX != 0);
    }
}

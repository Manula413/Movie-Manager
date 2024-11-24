package com.manula413.movie_manager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MovieManagerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MovieManagerApplication.class.getResource("/com/manula413/movie_manager/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 550, 400);
        stage.initStyle(StageStyle.UNDECORATED);
        //  stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

package com.manula413.movie_manager.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class watchedListController implements Initializable {

    @FXML
    private TableView<Object> watchedListTableView; // Use generic type for type safety

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
    }
}

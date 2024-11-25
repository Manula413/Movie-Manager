package com.manula413.movie_manager.controller;

import com.manula413.movie_manager.TestApp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import javafx.scene.control.*;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class MainPanelController {


    // @FXML for MainPanel.fxml

    @FXML
    private BorderPane loadingBorderPane;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    @FXML
    private Button addMoviesButton;

    @FXML
    private Button watchedListButton;

    @FXML
    private Button watchLaterButton;

    @FXML
    private Button extraButton;


    // @FXML for MovieDetails.fxml

    @FXML
    private AnchorPane movieDetailsAnchorPane;

    @FXML
    private Label movieNameLabel;

    @FXML
    private Label movieYear;

    @FXML
    private Label movieGenre;

    @FXML
    private Label ratingIMBDLabel;

    @FXML
    private Label ratingRTLabel;

    @FXML
    private Label moviePlotLabel;

    @FXML
    private RadioButton watchedRadioButton;

    @FXML
    private RadioButton watchLaterRadioButton;


    @FXML
    private Button addButton;



    public BorderPane getLoadingBorderPane() {
        return loadingBorderPane;
    }


    public static void main(String[] args) {
        javafx.application.Application.launch(TestApp.class);
    }


}

package com.manula413.movie_manager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


public class MovieDetailsController {

    @FXML
    private AnchorPane movieDetailsAnchorPane;

    @FXML
    private Label movieNameLabel;

    @FXML
    private Label movieYearLabel;

    @FXML
    private Label movieGenreLabel;

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


    @FXML
    private ImageView moviePosterImageView;

    @FXML
    private Label testUserLabel;


    public void setTestUsernameLabel(String username) {
        if (testUserLabel != null) {
            testUserLabel.setText("Welcome, " + username + "!");
        } else {
            System.out.println("Username label is null");
        }
    }

    // This method will be called from MainPanelController to set the movie details


}

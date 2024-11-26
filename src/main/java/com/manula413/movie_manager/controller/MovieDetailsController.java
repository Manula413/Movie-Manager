package com.manula413.movie_manager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    public void setMovieDetails(String title, String year, String genre, String imdbRating, String rtRating, String plot, String posterUrl) {
        // Update the labels with the movie data
        movieNameLabel.setText(title);
        movieYearLabel.setText(year);
        movieGenreLabel.setText(genre);
        ratingIMBDLabel.setText("IMDb Rating: " + imdbRating);
        ratingRTLabel.setText("Rotten Tomatoes Rating: " + rtRating);
        moviePlotLabel.setText(plot);

        // If a poster URL is available, load the poster image
        if (posterUrl != null && !posterUrl.equals("N/A")) {
            try {
                Image posterImage = new Image(posterUrl);  // Create a new Image using the URL
                moviePosterImageView.setImage(posterImage);  // Set the image to the ImageView
            } catch (Exception e) {
                e.printStackTrace();  // Handle image loading failure (e.g., malformed URL)
                // Optionally, set a default poster image
                moviePosterImageView.setImage(new Image("path/to/defaultPoster.jpg"));
            }
        }
    }

}

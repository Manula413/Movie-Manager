package com.manula413.movie_manager.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.manula413.movie_manager.database.DatabaseConnection;
import com.manula413.movie_manager.model.MovieDetails;
import com.manula413.movie_manager.services.MovieService;
import com.manula413.movie_manager.util.Session;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.util.Properties;

@SuppressWarnings("ALL")
public class MainPanelController {


    @FXML
    private TextField searchTextField;

    @FXML
    private RadioButton watchLaterRadioButton;

    @FXML
    private RadioButton watchedRadioButton;

    @FXML
    private Button addMoviesNavButton;

    @FXML
    private Button watchedListNavButton;

    @FXML
    private Button watchLaterNavButton;

    @FXML
    private Label displayNameLabel;

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
    private Label tvSeriesLabel;

    @FXML
    private Label seasonsLabel;

    @FXML
    private Label searchErrorLabel;

    @FXML
    private ImageView moviePosterImageView;

    @FXML
    private ToggleGroup watchListRadioGroup;

    @FXML
    private ComboBox<String> userRatingComboBox;

    @FXML
    private ImageView imdbLogoImageView;

    @FXML
    private ImageView rtLogoImageView;

    String userId = Session.getInstance().getUserId();
    private static final Logger logger = LoggerFactory.getLogger(MainPanelController.class);


    private MovieDetails movieDetails;
    private static final String MOVIE_NOT_FOUND = "Movie Not Found";
    private static final String ENTER_BOTH_FIELDS = "Please enter both the movie name and release year to perform a search";
    private static final String RATINGS_KEY = "Ratings";



    public void loadMainPanelDefault(Stage stage) throws IOException {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/com/manula413/movie_manager/mainPanel.fxml"));
        AnchorPane mainPanel = mainLoader.load();

        MainPanelController mainController = mainLoader.getController();
        String displayName = Session.getInstance().getDisplayName();

        mainController.setDisplayNameLabel(displayName);

        Scene scene = new Scene(mainPanel, 1300, 800);
        stage.setTitle("Add Movie");
        stage.setScene(scene);
        stage.show();
    }

    public void navigateTo(String fxmlPath, String title, ActionEvent event) {
        try {
            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Load the specified FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane panel = loader.load();

            // Set the new scene
            Scene scene = new Scene(panel, 1300, 800);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private final MovieService movieService = new MovieService();

    @FXML
    private void initialize(){}

    @FXML
    public void searchMovie() {}// Call `movieService.fetchMovieData` and update the UI

    public void addMoviesNavButtonAction(ActionEvent event){}
    public void watchedListNavButtonAction(ActionEvent event){}
    public void watchLaterNavButtonAction(ActionEvent event){}

    public void setDisplayNameLabel(String displayName){}

    private void setMovieTitle(String title){}
    private void setMovieYear(String year){}
    private void setMovieGenre(String genre){}
    private void setImdbRating(String imdbRating){}
    private void setRtRating(String rtRating){}
    private void setMoviePlot(String plot){}
    private void setMoviePoster(String posterUrl){}
    private void setTvSeriesDetails(String type, String totalSeasons){}


    public class MovieDataParseException extends Exception {
    }
}

package com.manula413.movie_manager.controller;

import com.manula413.movie_manager.model.MovieDetails;
import com.manula413.movie_manager.util.Session;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MainPanelController {

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
    private Label usernameLabel;

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
    private Label tvSeriesLabel;

    @FXML
    private Label seasonsLabel;

    @FXML
    private ImageView moviePosterImageView;

    private static final String DEFAULT_POSTER = "path/to/defaultPoster.jpg";


    public void loadMainPanelDefault(Stage stage) throws Exception {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/com/manula413/movie_manager/mainPanel.fxml"));
        AnchorPane mainPanel = mainLoader.load();

        MainPanelController mainController = mainLoader.getController();
        String username = Session.getInstance().getUsername();
        mainController.setUsernameLabel(username);

        Scene scene = new Scene(mainPanel, 1300, 800);
        stage.setTitle("Movie Info");
        stage.setScene(scene);
        stage.show();
    }

    public void setUsernameLabel(String username) {
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + username + "!");
        }
    }

    @FXML
    public void searchMovie() {
        String movieInput = searchTextField.getText().trim();
        System.out.println("User input: " + movieInput);

        // Run fetch in a separate thread
        new Thread(() -> {
            try {
                MovieDetails movieDetails = fetchMovieData(movieInput);

                // Update UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    if (movieDetails != null) {
                        setMovieDetails(
                                movieDetails.getTitle(),
                                movieDetails.getYear(),
                                movieDetails.getGenre(),
                                movieDetails.getImdbRating(),
                                movieDetails.getRtRating(),
                                movieDetails.getPlot(),
                                movieDetails.getPosterUrl(),
                                movieDetails.getType(),
                                movieDetails.getTotalSeasons()
                        );
                        System.out.println("Movie details displayed on UI.");
                    } else {
                        setMovieDetails("Movie Not Found", "", "", "", "", "No details available", null, "", "");
                        System.out.println("No movie details available.");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    setMovieDetails("Error", "", "", "", "", "An error occurred while fetching movie details.", null, "", "");
                    System.err.println("Exception caught during searchMovie: " + e.getMessage());
                });
            }
        }).start();
    }


    public MovieDetails fetchMovieData(String movieInput) throws Exception {
        System.out.println("Starting fetchMovieData with input: " + movieInput);

        String apiKey = getAPIKey();
        String[] parts = movieInput.split("\\s(?=\\d{4}$)");

        if (parts.length != 2) {
            System.out.println("Invalid format. Use 'Movie Name Year'.");
            return null;
        }

        String movieName = parts[0].trim();
        String movieYear = parts[1].trim();
        System.out.println("Parsed movie name: " + movieName + ", year: " + movieYear);

        String url = "http://www.omdbapi.com/?t=" + movieName.replace(" ", "+") + "&y=" + movieYear + "&apikey=" + apiKey;
        System.out.println("Constructed URL: " + url);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                System.out.println("HTTP response status: " + response.getCode());

                String responseString = EntityUtils.toString(response.getEntity());
                System.out.println("Response received: " + responseString);

                JsonObject json = JsonParser.parseString(responseString).getAsJsonObject();

                if (json.has("Response") && json.get("Response").getAsString().equalsIgnoreCase("False")) {
                    System.out.println("Movie not found: " + json.get("Error").getAsString());
                    return null;
                }

                String title = json.has("Title") ? json.get("Title").getAsString() : "N/A";
                String year = json.has("Year") ? json.get("Year").getAsString() : "N/A";
                String genre = json.has("Genre") ? json.get("Genre").getAsString() : "N/A";
                String imdbRating = json.has("imdbRating") ? json.get("imdbRating").getAsString() : "N/A";
                String rtRating = json.has("Ratings") && json.getAsJsonArray("Ratings").size() > 1
                        ? json.getAsJsonArray("Ratings").get(1).getAsJsonObject().get("Value").getAsString()
                        : "N/A";
                String plot = json.has("Plot") ? json.get("Plot").getAsString() : "N/A";
                String posterUrl = json.has("Poster") ? json.get("Poster").getAsString() : null;
                String type = json.has("Type") ? json.get("Type").getAsString() : "N/A";
                String totalSeasons = "N/A";

                // Check if it's a series and get the total number of seasons
                if ("series".equalsIgnoreCase(type) && json.has("totalSeasons")) {
                    totalSeasons = json.get("totalSeasons").getAsString();
                }

                System.out.println("Movie details fetched successfully.");
                return new MovieDetails(title, year, genre, imdbRating, rtRating, plot, posterUrl, type, totalSeasons);
            }
        } catch (IOException e) {
            System.err.println("Error during HTTP request: " + e.getMessage());
            throw e;
        }
    }


    private static String getAPIKey() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/resources/keys.properties")) {
            properties.load(input);
        }
        return properties.getProperty("api.key");
    }

    public void setMovieDetails(String title, String year, String genre, String imdbRating, String rtRating, String plot, String posterUrl, String type, String totalSeasons) {
        movieNameLabel.setText(title != null ? title : "N/A");
        movieYearLabel.setText(year != null ? year : "N/A");
        movieGenreLabel.setText(genre != null ? genre : "N/A");

        // Display only the numeric value for IMDb rating
        String imdbRatingDisplay = (imdbRating != null && !imdbRating.equals("N/A")) ? imdbRating : "N/A";
        ratingIMBDLabel.setText(imdbRatingDisplay);
        System.out.println(imdbRatingDisplay);

        // Display only the numeric value for Rotten Tomatoes rating with a percentage symbol
        String rtRatingDisplay = (rtRating != null && !rtRating.equals("N/A")) ? rtRating : "N/A";
        if (!rtRatingDisplay.equals("N/A")) {
            // Ensure we only display the numeric value with a '%' symbol, e.g., '66%'
            rtRatingDisplay = rtRatingDisplay.replaceAll("[^0-9]", "") + "%"; // Remove non-numeric characters and append '%'
        }
        ratingRTLabel.setText(rtRatingDisplay);
        System.out.println(rtRatingDisplay);

        moviePlotLabel.setText(plot != null ? plot : "N/A");

        try {
            if (posterUrl != null && !posterUrl.equals("N/A")) {
                moviePosterImageView.setImage(new Image(posterUrl, true));
            } else {
                moviePosterImageView.setImage(new Image(DEFAULT_POSTER));
            }
        } catch (Exception e) {
            e.printStackTrace();
            moviePosterImageView.setImage(new Image(DEFAULT_POSTER));
        }

        // Check if it's a TV series and set the labels accordingly
        if ("series".equalsIgnoreCase(type)) {
            tvSeriesLabel.setText("TV - Series");
            seasonsLabel.setText(totalSeasons != null && !totalSeasons.equals("N/A") ? "Seasons: " + totalSeasons : "Seasons: N/A");
        } else {
            tvSeriesLabel.setText(""); // Clear TV Series label if it's a movie
            seasonsLabel.setText(""); // Clear Seasons label if it's a movie
        }


    }


}

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
    private void initialize(){
        // Populate the ComboBox with values

        userRatingComboBox.setItems(FXCollections.observableArrayList("Great", "Good", "Okay", "Mediocre", "Poor"));
        //userRatingComboBox.setValue("3"); // Optional: Set a default value
        watchListRadioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == watchLaterRadioButton) {
                userRatingComboBox.setDisable(true); // Disable ComboBox
                userRatingComboBox.setValue("N/A"); // Set the default value as "N/A"
            } else {
                userRatingComboBox.setDisable(false); // Enable ComboBox
                userRatingComboBox.setValue(null); // Clear any default value
            }
        });
    }

    @FXML
    public void searchMovie() {
        String movieInput = searchTextField.getText().trim();
        logger.info("User input: {}", movieInput);

        // Run fetch in a separate thread
        new Thread(() -> {
            try {
                // Fetch movie details using the existing fetchMovieData method
                MovieDetails fetchedMovieDetails = movieService.fetchMovieData(movieInput);

                // Update UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    if (fetchedMovieDetails != null) {
                        this.movieDetails = fetchedMovieDetails;  // Assign the fetched data to the class field
                        setMovieDetails(fetchedMovieDetails);  // Pass the MovieDetails object
                        logger.info("Movie details displayed on UI.");
                    } else {
                        setMovieDetails(new MovieDetails(MOVIE_NOT_FOUND, "", "", "", "", ENTER_BOTH_FIELDS, null, "", "",null));
                        logger.info("No movie details available.");
                    }
                });
            } catch (Exception e) {
                logger.error("Exception caught during searchMovie: ", e);
                Platform.runLater(() -> setMovieDetails(new MovieDetails(MOVIE_NOT_FOUND, "", "", "", "", ENTER_BOTH_FIELDS, null, "", "",null)));
            }
        }).start();
    }// Call `movieService.fetchMovieData` and update the UI

    public void addMoviesNavButtonAction(ActionEvent event) {
        navigateTo("/com/manula413/movie_manager/mainPanel.fxml", "Add Movie", event);
    }

    public void watchedListNavButtonAction(ActionEvent event) {
        navigateTo("/com/manula413/movie_manager/watchedList.fxml", "Watched List", event);
    }

    public void watchLaterNavButtonAction(ActionEvent event) {
        navigateTo("/com/manula413/movie_manager/watchLaterList.fxml", "Watch Later List", event);
    }

    public void setDisplayNameLabel(String displayName){
        if (displayNameLabel != null) {
            displayNameLabel.setText("Welcome, " + displayName + "!");
        }
    }
    public void setMovieDetails(MovieDetails movieDetails) {
        setMovieTitle(movieDetails.getTitle());
        setMovieYear(movieDetails.getYear());
        setMovieGenre(movieDetails.getGenre());
        setImdbRating(movieDetails.getImdbRating());
        setRtRating(movieDetails.getRtRating());
        setMoviePlot(movieDetails.getPlot());
        setMoviePoster(movieDetails.getPosterUrl());
        setTvSeriesDetails(movieDetails.getType(), movieDetails.getTotalSeasons());
    }

    private void setMovieTitle(String title) {
        movieNameLabel.setText(title != null ? title : MOVIE_NOT_FOUND);
    }

    private void setMovieYear(String year) {
        movieYearLabel.setText(year != null ? year : " ");
    }

    private void setMovieGenre(String genre) {
        if (genre != null && !genre.isEmpty()) {
            String[] genres = genre.split(", ");
            String displayGenre = genres.length > 1 ? genres[0] + ", " + genres[1] : genres[0];
            movieGenreLabel.setText(displayGenre);
        } else {
            movieGenreLabel.setText(" ");
        }
    }


    private void setImdbRating(String imdbRating) {
        String imdbRatingDisplay = (imdbRating != null && !imdbRating.equals(" ")) ? imdbRating : " ";
        ratingIMBDLabel.setText(imdbRatingDisplay);
        imdbLogoImageView.setVisible(!" ".equals(imdbRatingDisplay)); // Show only if the rating is not empty
    }


    private void setRtRating(String rtRating) {
        String rtRatingDisplay;
        if (rtRating == null || rtRating.trim().isEmpty()) {
            rtRatingDisplay = "";  // Set to empty string if rtRating is null or empty
        } else if ("N/A".equals(rtRating)) {
            rtRatingDisplay = "N/A"; // Keep it as "N/A" if it's exactly "N/A"
        } else {
            rtRatingDisplay = rtRating.replaceAll("\\D", "") + "%"; // Remove non-numeric characters and append '%'

        }
        ratingRTLabel.setText(rtRatingDisplay);
        rtLogoImageView.setVisible(!rtRatingDisplay.trim().isEmpty());

    }

    private void setMoviePlot(String plot) {
        moviePlotLabel.setText(plot != null ? plot : "No movie found matching the title and year. Please verify the details and try again.");
    }

    private void setMoviePoster(String posterUrl) {
        URL resource = getClass().getResource("/images/image-not-found.png");
        try {
            if (posterUrl != null && !posterUrl.equals("N/A")) {
                moviePosterImageView.setImage(new Image(posterUrl, true));
            } else {
                moviePosterImageView.setImage(new Image(resource.toExternalForm()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            moviePosterImageView.setImage(new Image(resource.toExternalForm()));
        }
    }

    private void setTvSeriesDetails(String type, String totalSeasons) {
        if ("series".equalsIgnoreCase(type)) {
            tvSeriesLabel.setText("TV - Series");
            seasonsLabel.setText(totalSeasons != null && !totalSeasons.equals("N/A") ? "Seasons: " + totalSeasons : "Seasons: N/A");
        } else {
            tvSeriesLabel.setText(""); // Clear TV Series label if it's a movie
            seasonsLabel.setText(""); // Clear Seasons label if it's a movie
        }
    }

    private String[] parseMovieInput(String movieInput) {
        String[] parts = movieInput.split("\\s(?=\\d{4}$)");
        if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            return new String[0];  // Return null if invalid input format or empty fields
        }
        return parts;
    }

    private JsonObject fetchMovieJsonResponse(String url) throws IOException, MovieDataParseException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                String responseString = EntityUtils.toString(response.getEntity());
                return JsonParser.parseString(responseString).getAsJsonObject();
            } catch (ParseException e) {
                // Throw a custom exception instead of a generic RuntimeException
                throw new MovieDataParseException("Error parsing the movie data response.", e);
            }
        }
    }

    public class MovieDataParseException extends Exception {
        public MovieDataParseException(String message) {
            super(message);
        }

        public MovieDataParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static String getAPIKey() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/resources/keys.properties")) {
            properties.load(input);
        }
        return properties.getProperty("api.key");
    }



}

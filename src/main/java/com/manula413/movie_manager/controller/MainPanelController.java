package com.manula413.movie_manager.controller;

import com.manula413.movie_manager.database.MovieRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

import static com.manula413.movie_manager.services.MovieService.setMovieDetails;

@SuppressWarnings("ALL")
public class MainPanelController {


    public static final String MOVIE_NOT_FOUND = "Movie Not Found";
    public static final String RATINGS_KEY = "Ratings";
    private static final Logger logger = LoggerFactory.getLogger(MainPanelController.class);
    private static final String ENTER_BOTH_FIELDS = "Please enter both the movie name and release year to perform a search";
    private final MovieService movieService;

    String userId = Session.getInstance().getUserId();
    @FXML
    private TextField searchTextField;

    @FXML
    private  RadioButton watchLaterRadioButton;

    @FXML
    private  RadioButton watchedRadioButton;

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
    private  ToggleGroup watchListRadioGroup;

    @FXML
    private  ComboBox<String> userRatingComboBox;

    @FXML
    private ImageView imdbLogoImageView;

    @FXML
    private ImageView rtLogoImageView;

    private MovieDetails movieDetails;

    public MainPanelController() {
        // Pass the current controller instance to MovieService
        this.movieService = new MovieService(this);
    }


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


    @FXML
    private void initialize() {
        // Populate the ComboBox with values
        userRatingComboBox.setItems(FXCollections.observableArrayList("Great", "Good", "Okay", "Mediocre", "Poor"));
        userRatingComboBox.setValue("Great"); // Optional: Set a default value (or leave as null if no default)

        // Listen for changes in the selected radio button
        watchListRadioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == watchLaterRadioButton) {
                // Disable ComboBox and set a default value when Watch Later is selected
                userRatingComboBox.setDisable(true);
                userRatingComboBox.setValue("N/A");  // Ensure the value matches the ComboBox options
            } else {
                // Enable ComboBox when other radio button is selected
                userRatingComboBox.setDisable(false);
                userRatingComboBox.setValue(null); // Optionally clear the value when enabling
            }
        });
    }


    @FXML
    public void searchMovie() {
        String movieInput = searchTextField.getText().trim();
        logger.info("User input: {}", movieInput);
        System.out.println("User input: " + movieInput); // Added Sysout

        // Run fetch in a separate thread
        new Thread(() -> {
            try {
                // Fetch movie details using the existing fetchMovieData method
                MovieDetails fetchedMovieDetails = MovieService.fetchMovieData(movieInput);

                // Update UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    if (fetchedMovieDetails != null) {
                        this.movieDetails = fetchedMovieDetails;  // Assign the fetched data to the class field
                        System.out.println("Fetched Movie Details: " + fetchedMovieDetails); //
                        // Added Sysout
                        setMovieDetails(fetchedMovieDetails);  // Pass the MovieDetails object
                        logger.info("Movie details displayed on UI.");
                    } else {
                        setMovieDetails(new MovieDetails(MOVIE_NOT_FOUND, "", "", "", "", ENTER_BOTH_FIELDS, null, "", "",null));
                        logger.info("No movie details available.");
                        System.out.println("No movie details found, setting default values."); // Added Sysout
                    }
                });
            } catch (Exception e) {
                logger.error("Exception caught during searchMovie: ", e);
                Platform.runLater(() -> {
                    setMovieDetails(new MovieDetails(MOVIE_NOT_FOUND, "", "", "", "", ENTER_BOTH_FIELDS, null, "", "",null));
                    System.out.println("Exception occurred, setting default MovieDetails."); // Added Sysout
                });
            }
        }).start();
    }


    public void addMovieToDatabase() {

        MovieRepository.addMovieToDatabase();

    }

    public String determineMovieStatus() {
        // Get the selected toggle from the group
        Toggle selectedToggle = watchListRadioGroup.getSelectedToggle();

        if (selectedToggle == watchLaterRadioButton) {
            return "watchLater"; // Watch Later selected
        } else if (selectedToggle == watchedRadioButton) {
            return "watched"; // Watched selected
        }

        return null; // No valid selection
    }

    public String determineUserRating() {
        // Get the selected item from the ComboBox
        String selectedRating = userRatingComboBox.getValue();

        if (selectedRating != null) {
            return selectedRating; // Return the selected rating
        }

        return null; // No selection made
    }

    /*
    Navigation Buttons
     */

    public void addMoviesNavButtonAction(ActionEvent event) {
        navigateTo("/com/manula413/movie_manager/mainPanel.fxml", "Add Movie", event);
    }

    public void watchedListNavButtonAction(ActionEvent event) {
        navigateTo("/com/manula413/movie_manager/watchedList.fxml", "Watched List", event);
    }

    public void watchLaterNavButtonAction(ActionEvent event) {
        navigateTo("/com/manula413/movie_manager/watchLaterList.fxml", "Watch Later List", event);
    }



    /*
    Setting UI Elements
     */

    public void setDisplayNameLabel(String displayName) {
        if (displayNameLabel != null) {
            displayNameLabel.setText("Welcome, " + displayName + "!");
        }
    }

    public void setMovieTitle(String title) {
        movieNameLabel.setText(title != null ? title : "MOVIE_NOT_FOUND");
    }


    public void setMovieYear(String year) {
        movieYearLabel.setText(year != null ? year : " ");
    }

    public void setMovieGenre(String genre) {
        if (genre != null && !genre.isEmpty()) {
            String[] genres = genre.split(", ");
            String displayGenre = genres.length > 1 ? genres[0] + ", " + genres[1] : genres[0];
            movieGenreLabel.setText(displayGenre);
        } else {
            movieGenreLabel.setText(" ");
        }
    }


    public void setImdbRating(String imdbRating) {
        String imdbRatingDisplay = (imdbRating != null && !imdbRating.equals(" ")) ? imdbRating : " ";
        ratingIMBDLabel.setText(imdbRatingDisplay);
        imdbLogoImageView.setVisible(!" ".equals(imdbRatingDisplay)); // Show only if the rating is not empty
    }


    public void setRtRating(String rtRating) {
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

    public void setMoviePlot(String plot) {
        moviePlotLabel.setText(plot != null ? plot : "No movie found matching the title and year. Please verify the details and try again.");
    }

    public void setMoviePoster(String posterUrl) {
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

    public void setTvSeriesDetails(String type, String totalSeasons) {
        if ("series".equalsIgnoreCase(type)) {
            tvSeriesLabel.setText("TV - Series");
            seasonsLabel.setText(totalSeasons != null && !totalSeasons.equals("N/A") ? "Seasons: " + totalSeasons : "Seasons: N/A");
        } else {
            tvSeriesLabel.setText(""); // Clear TV Series label if it's a movie
            seasonsLabel.setText(""); // Clear Seasons label if it's a movie
        }
    }


    /*
    Exeption Methods
     */

    public static class MovieDataParseException extends Exception {
        public MovieDataParseException(String message) {
            super(message);
        }

        public MovieDataParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }


}



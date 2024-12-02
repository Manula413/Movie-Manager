package com.manula413.movie_manager.controller;

import com.manula413.movie_manager.database.DatabaseConnection;
import com.manula413.movie_manager.model.MovieDetails;
import com.manula413.movie_manager.util.Session;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
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
    private Label usernameLabel;

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
        String username = Session.getInstance().getUsername();

        mainController.setUsernameLabel(username);

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

    public void addMoviesNavButtonAction(ActionEvent event) {
        navigateTo("/com/manula413/movie_manager/mainPanel.fxml", "Add Movie", event);
    }

    public void watchedListNavButtonAction(ActionEvent event) {
        navigateTo("/com/manula413/movie_manager/watchedList.fxml", "Watched List", event);
    }

    public void watchLaterNavButtonAction(ActionEvent event) {
        navigateTo("/com/manula413/movie_manager/watchLaterList.fxml", "Watch Later List", event);
    }




    @FXML
    private void initialize() {
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


    public void setUsernameLabel(String username) {
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + username + "!");
        }
    }

    @FXML
    public void searchMovie() {
        String movieInput = searchTextField.getText().trim();
        logger.info("User input: {}", movieInput);

        // Run fetch in a separate thread
        new Thread(() -> {
            try {
                // Fetch movie details using the existing fetchMovieData method
                MovieDetails fetchedMovieDetails = fetchMovieData(movieInput);

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
    }



    public MovieDetails fetchMovieData(String movieInput) throws Exception {
        System.out.println("Starting fetchMovieData with input: " + movieInput);

        // Clear any previous error messages
        Platform.runLater(() -> searchErrorLabel.setText(""));

        String apiKey = getAPIKey();
        String[] parts = parseMovieInput(movieInput);

        // Validate input format: movie name and year should be provided
        if (parts.length == 0) {
            Platform.runLater(() -> searchErrorLabel.setText("Please use 'Movie Name Year'."));
            return null;
        }

        String movieName = parts[0].trim();
        String movieYear = parts[1].trim();
        System.out.println("Parsed movie name: " + movieName + ", year: " + movieYear);

        // Construct the URL to query the OMDB API
        String url = "http://www.omdbapi.com/?t=" + movieName.replace(" ", "+") + "&y=" + movieYear + "&apikey=" + apiKey;
        System.out.println("Constructed URL: " + url);

        try {
            JsonObject json = fetchMovieJsonResponse(url);

            // Handle response if the movie is not found
            if (json.has("Response") && json.get("Response").getAsString().equalsIgnoreCase("False")) {
                return handleMovieNotFound(json);
            }

            // Parse the movie details from the JSON response
            String title = json.has("Title") ? json.get("Title").getAsString() : MOVIE_NOT_FOUND;
            String year = json.has("Year") ? json.get("Year").getAsString() : "N/A";
            String genre = json.has("Genre") ? json.get("Genre").getAsString() : "N/A";
            String imdbRating = json.has("imdbRating") ? json.get("imdbRating").getAsString() : "N/A";
            String rtRating = json.has(RATINGS_KEY) && json.getAsJsonArray(RATINGS_KEY).size() > 1
                    ? json.getAsJsonArray(RATINGS_KEY).get(1).getAsJsonObject().get("Value").getAsString()
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
            return new MovieDetails(title, year, genre, imdbRating, rtRating, plot, posterUrl, type, totalSeasons,null);
        } catch (IOException e) {
            System.err.println("Error during HTTP request: " + e.getMessage());
            throw e;
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


    private MovieDetails handleMovieNotFound(JsonObject json) {
        String errorMessage = json.has("Error") ? json.get("Error").getAsString() : "Unknown error";
        System.out.println("Movie not found: " + errorMessage);
        return new MovieDetails(null, null, null, null, null, null, null, null, null,null);
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
        movieGenreLabel.setText(genre != null ? genre : " ");
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




    public void addMovieToDatabase() {
        System.out.println("Worked till here 1");

        // Check if movieDetails is not null
        if (movieDetails != null) {
            // Retrieve movie details
            String title = movieDetails.getTitle();
            String year = movieDetails.getYear();
            String genre = movieDetails.getGenre();
            String imdbRating = movieDetails.getImdbRating();
            String rtRating = movieDetails.getRtRating();
            String type = movieDetails.getType();
            String totalSeasons = movieDetails.getTotalSeasons();

            // Debugging output
            System.out.println("Worked till here, movie title: " + title);

            // Database connection setup
            DatabaseConnection connectNow = new DatabaseConnection();
            try (Connection connectDB = connectNow.getConnection()) {
                // Normalize movie name by trimming and converting to lowercase
                title = title.trim().toLowerCase();

                // Step 1: Check if the movie exists in the database or add it if missing
                int movieId = getOrInsertMovie(connectDB, title, year, genre, type, imdbRating, rtRating, totalSeasons);

                // If movieId is -1, it means insertion or retrieval failed
                if (movieId == -1) {
                    System.out.println("Failed to retrieve or insert movie: " + title);
                    return;
                }

                // Step 2: Check if the user already has this movie in their list
                if (isMovieInUserList(connectDB, Integer.parseInt(userId), movieId)) {
                    System.out.println("User " + userId + " already has the movie " + title + " in their list.");
                    return;
                }


                // Step 3: Determine movie status from radio buttons and insert into user_movies
                String status = determineMovieStatus();
                System.out.println(status);
                if (status == null) {
                    System.out.println("Invalid status selection. Please select either 'watched' or 'watch later'.");
                    return;
                }

                // Default comment (can be customized or taken from a user input field)
                String userRating = determineUserRating();

                // Step 4: Insert the user movie entry into the database
                boolean inserted = addUserMovie(connectDB, Integer.parseInt(userId), movieId, userRating, status);
                if (inserted) {
                    System.out.println("User " + userId + " successfully added movie " + title + " with status " + status);
                } else {
                    System.out.println("Failed to add movie " + title + " for user " + userId);
                }

            } catch (SQLException e) {
                // Handle SQL exception properly
                System.out.println("Database error occurred: " + e.getMessage());
                e.printStackTrace(); // Print stack trace for debugging
            } catch (Exception e) {
                // Catch any other exceptions
                System.out.println("An unexpected error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Movie details are null. Cannot add to database.");
        }
    }

    private int getOrInsertMovie(Connection connectDB, String movieName, String movieYear, String genre,
                                 String type, String imdbRating, String rtRating, String seasons) throws SQLException {
        String checkMovieQuery = "SELECT movieid FROM movies WHERE movieName = ? AND year = ? AND genre = ?";
        String insertMovieQuery = "INSERT INTO movies (movieName, year, genre, type, seasons, imdb_Rating, rt_Rating) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement checkMovieStmt = connectDB.prepareStatement(checkMovieQuery);
             PreparedStatement insertMovieStmt = connectDB.prepareStatement(insertMovieQuery, Statement.RETURN_GENERATED_KEYS)) {

            checkMovieStmt.setString(1, movieName);
            checkMovieStmt.setString(2, movieYear);
            checkMovieStmt.setString(3, genre);
            ResultSet movieResultSet = checkMovieStmt.executeQuery();

            if (movieResultSet.next()) {
                return movieResultSet.getInt("movieid"); // Movie already exists
            }

            // Insert movie into the database
            insertMovieStmt.setString(1, movieName);
            insertMovieStmt.setString(2, movieYear);
            insertMovieStmt.setString(3, genre);
            insertMovieStmt.setString(4, type != null ? type : "N/A");
            insertMovieStmt.setString(5, seasons != null ? seasons : "N/A");
            insertMovieStmt.setString(6, imdbRating != null ? imdbRating : "0");
            insertMovieStmt.setString(7, rtRating != null ? rtRating : "0");

            int rowsInserted = insertMovieStmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = insertMovieStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Return the generated movie ID
                }
            }
        }
        return -1; // Indicate failure
    }

    private boolean isMovieInUserList(Connection connectDB, int userId, int movieId) throws SQLException {
        String checkUserMovieQuery = "SELECT COUNT(*) FROM user_movies WHERE userid = ? AND movieid = ?";
        try (PreparedStatement checkUserMovieStmt = connectDB.prepareStatement(checkUserMovieQuery)) {
            checkUserMovieStmt.setInt(1, userId);
            checkUserMovieStmt.setInt(2, movieId);
            ResultSet resultSet = checkUserMovieStmt.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }


    private String determineMovieStatus() {
        // Get the selected toggle from the group
        Toggle selectedToggle = watchListRadioGroup.getSelectedToggle();

        if (selectedToggle == watchLaterRadioButton) {
            return "watchLater"; // Watch Later selected
        } else if (selectedToggle == watchedRadioButton) {
            return "watched"; // Watched selected
        }

        return null; // No valid selection
    }

    private String determineUserRating() {
        // Get the selected item from the ComboBox
        String selectedRating = userRatingComboBox.getValue();

        if (selectedRating != null) {
            return selectedRating; // Return the selected rating
        }

        return null; // No selection made
    }


    public boolean isValidURL(String urlString) {
        try {
            new URL(urlString).toURI();  // Try to convert the string to a URI
            return true;
        } catch (URISyntaxException | MalformedURLException e) {
            return false;  // Invalid URL
        }
    }


    private boolean addUserMovie(Connection connectDB, int userId, int movieId, String userComment, String status) throws SQLException {
        String insertUserMovieQuery = "INSERT INTO user_movies (userid, movieid, userComment, watched_Status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insertUserMovieStmt = connectDB.prepareStatement(insertUserMovieQuery)) {
            insertUserMovieStmt.setInt(1, userId);
            insertUserMovieStmt.setInt(2, movieId);
            insertUserMovieStmt.setString(3, userComment != null ? userComment : "");
            insertUserMovieStmt.setString(4, status);
            return insertUserMovieStmt.executeUpdate() > 0;
        }
    }


}

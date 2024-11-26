package com.manula413.movie_manager.controller;

import com.manula413.movie_manager.model.MovieDetails;
import com.manula413.movie_manager.util.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;



import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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

    @FXML
    private Label usernameLabel;


    // @FXML for MovieDetails.fxml

    @FXML
    private AnchorPane movieDetailsAnchorPane;

    private MovieDetailsController movieDetailsController;



    public BorderPane getLoadingBorderPane() {
        return loadingBorderPane;
    }


    public void loadMainPanelDefault(Stage stage) throws Exception {
        // Load the main panel FXML
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/com/manula413/movie_manager/mainPanel.fxml"));
        Parent mainPanel = mainLoader.load();

        MainPanelController mainController = mainLoader.getController();

        // Set username after the main panel is fully loaded
        String username = Session.getInstance().getUsername();
        mainController.setUsernameLabel(username);

        // Load the movie details FXML and set it inside the main panel
        FXMLLoader movieLoader = new FXMLLoader(getClass().getResource("/com/manula413/movie_manager/movieDetails.fxml"));
        movieDetailsAnchorPane = movieLoader.load(); // Load movie details FXML
        mainController.getLoadingBorderPane().setCenter(movieDetailsAnchorPane); // Inject details into the main panel

        // Get MovieDetailsController instance from FXMLLoader
        MovieDetailsController movieDetailsController = movieLoader.getController();  // Get the controller here

        // Set movie details
        MovieDetails movieDetails = fetchMovieData("Some Movie Name 2020");  // Replace with actual movie input
        if (movieDetails != null) {
            movieDetailsController.setMovieDetails(
                    movieDetails.getTitle(),
                    movieDetails.getYear(),
                    movieDetails.getGenre(),
                    movieDetails.getImdbRating(),
                    movieDetails.getRtRating(),
                    movieDetails.getPlot(),
                    movieDetails.getPosterUrl()
            );
        }

        // Set scene and show stage
        Scene scene = new Scene(mainPanel, 1300, 830);
        stage.setTitle("MainPanel with MovieDetails");
        stage.setScene(scene);
        stage.show();
    }




    public void setUsernameLabel(String username) {
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + username + "!");
        } else {
            System.out.println("Username label is null");
        }
    }





    @FXML
    public void searchMovie() {
        String movieInput = searchTextField.getText().trim();

        try {
            // Show loading indicator
            loadingBorderPane.setVisible(true);

            // Fetch movie data
            MovieDetails movieDetails = fetchMovieData(movieInput);

            // Hide loading indicator
            loadingBorderPane.setVisible(false);

            // Ensure movie details are not null before setting them
            if (movieDetails != null) {
                movieDetailsController.setMovieDetails(
                        movieDetails.getTitle(),
                        movieDetails.getYear(),
                        movieDetails.getGenre(),
                        movieDetails.getImdbRating(),
                        movieDetails.getRtRating(),
                        movieDetails.getPlot(),
                        movieDetails.getPosterUrl()
                );
            } else {
                // Handle the case where the movie wasn't found
                movieDetailsController.setMovieDetails("Movie Not Found", "", "", "", "", "No details available", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Handle any error (e.g., network issue, API failure)
            loadingBorderPane.setVisible(false); // Hide loading indicator
            // Display an error message to the user
            movieDetailsController.setMovieDetails("Error", "", "", "", "", "An error occurred", null);
        }
    }


    // Method to get the API key from the properties file
    private static String getAPIKey() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/resources/keys.properties")) {
            properties.load(input);
        }
        return properties.getProperty("api.key");
    }

    // Method to fetch movie data from the OMDB API
    public static MovieDetails fetchMovieData(String movieInput) throws Exception {
        String movieName = "";
        String movieYear = "";

        // Regular expression to match a four-digit year at the end of the string
        String regex = "(.*)(\\d{4})$";  // Match everything before the last 4 digits

        if (movieInput.matches(regex)) {
            movieName = movieInput.replaceAll(regex, "$1").trim();  // Everything before the 4 digits
            movieYear = movieInput.replaceAll(regex, "$2").trim();  // The 4 digits (year)
        } else {
            System.out.println("Invalid input format. Please enter in the format: Movie Name Year");
            return null;
        }

        String apiKey = getAPIKey();
        String url = "http://www.omdbapi.com/?t=" + movieName.replace(" ", "+") + "&y=" + movieYear + "&apikey=" + apiKey;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                String responseString = EntityUtils.toString(response.getEntity());

                // Parse the JSON response using Gson
                JsonObject json = JsonParser.parseString(responseString).getAsJsonObject();

                String title = json.has("Title") ? json.get("Title").getAsString() : "N/A";
                String year = json.has("Year") ? json.get("Year").getAsString() : "N/A";
                String genre = json.has("Genre") ? json.get("Genre").getAsString() : "N/A";
                String imdbRating = json.has("imdbRating") ? json.get("imdbRating").getAsString() : "N/A";
                String rtRating = json.has("Ratings") && json.getAsJsonArray("Ratings").size() > 1
                        ? json.getAsJsonArray("Ratings").get(1).getAsJsonObject().get("Value").getAsString() : "N/A";
                String plot = json.has("Plot") ? json.get("Plot").getAsString() : "N/A";
                String posterUrl = json.has("Poster") ? json.get("Poster").getAsString() : null;

                return new MovieDetails(title, year, genre, imdbRating, rtRating, plot, posterUrl);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



}

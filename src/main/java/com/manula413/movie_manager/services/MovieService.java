package com.manula413.movie_manager.services;

import com.google.gson.JsonObject;
import com.manula413.movie_manager.controller.MainPanelController;
import com.manula413.movie_manager.model.MovieDetails;
import javafx.application.Platform;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class MovieService {

    public MovieDetails fetchMovieData(String movieInput) throws Exception {
        System.out.println("Starting fetchMovieData with input: " + movieInput);

        // Clear any previous error messages
        Platform.runLater(() -> searchErrorLabel.setText(""));

        String apiKey = getAPIKey();
        String[] parts = parseMovieInput(movieInput);

        // Validate input format: movie name and year should be provided
        if (parts.length == 0) {
            Platform.runLater(() -> MasearchErrorLabel.setText("Please use 'Movie Name Year'."));
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
        return new String[0]; // Placeholder return
    }

    private JsonObject fetchMovieJsonResponse(String url) throws IOException, MainPanelController.MovieDataParseException {
        return new JsonObject(); // Placeholder return
    }

    private MovieDetails handleMovieNotFound(JsonObject json) {
        return null; // Placeholder return
    }

    public void addMovieToDatabase() {
        // Placeholder method
    }

    private int getOrInsertMovie(Connection connectDB, String movieName, String movieYear, String genre,
                                 String type, String imdbRating, String rtRating, String seasons) throws SQLException {
        return 0; // Placeholder return
    }

    private boolean isMovieInUserList(Connection connectDB, int userId, int movieId) throws SQLException {
        return false; // Placeholder return
    }

    private String getFirstTwoGenres(String genre) {
        return ""; // Placeholder return
    }

    private String determineMovieStatus() {
        return ""; // Placeholder return
    }

    private String determineUserRating() {
        return ""; // Placeholder return
    }
}

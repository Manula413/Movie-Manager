package com.manula413.movie_manager.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.manula413.movie_manager.controller.MainPanelController;
import com.manula413.movie_manager.database.MovieRepository;
import com.manula413.movie_manager.model.MovieDetails;
import static com.manula413.movie_manager.util.MovieUtils.getAPIKey;

import javafx.application.Platform;
import javafx.scene.control.Toggle;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

public class MovieService {


    private static MainPanelController mainPanelController = new MainPanelController();
    // Callback for clearing or setting error messages
    private static Runnable onClearErrorCallback;
    private static Consumer<String> onErrorCallback;
    private final MovieRepository movieRepository;

    public MovieService(MainPanelController mainPanelController) {
        this.mainPanelController = mainPanelController;
        this.movieRepository = new MovieRepository();
    }

    public static MovieDetails fetchMovieData(String movieInput) throws Exception {
        System.out.println("Starting fetchMovieData with input: " + movieInput);

        // Clear previous errors using the callback
        if (onClearErrorCallback != null) {
            onClearErrorCallback.run();
        }

        String apiKey = getAPIKey();
        String[] parts = parseMovieInput(movieInput);

        // Validate input format
        if (parts.length == 0) {
            if (onErrorCallback != null) {
                onErrorCallback.accept("Please use 'Movie Name Year'.");
            }
            return null;
        }

        String movieName = parts[0].trim();
        String movieYear = parts[1].trim();
        System.out.println("Parsed movie name: " + movieName + ", year: " + movieYear);

        String url = "http://www.omdbapi.com/?t=" + movieName.replace(" ", "+") + "&y=" + movieYear + "&apikey=" + apiKey;
        System.out.println("Constructed URL: " + url);

        try {
            JsonObject json = fetchMovieJsonResponse(url);

            // Handle movie not found
            if (json.has("Response") && json.get("Response").getAsString().equalsIgnoreCase("False")) {
                if (onErrorCallback != null) {
                    onErrorCallback.accept("Movie not found. Please check the name and year.");
                }
                return null;
            }

            // Parse movie details
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
            String totalSeasons = json.has("totalSeasons") ? json.get("totalSeasons").getAsString() : "N/A";

            System.out.println("Movie details fetched successfully.");
            return new MovieDetails(title, year, genre, imdbRating, rtRating, plot, posterUrl, type, totalSeasons, null);

        } catch (IOException e) {
            System.err.println("Error during HTTP request: " + e.getMessage());
            if (onErrorCallback != null) {
                onErrorCallback.accept("Network error occurred. Please try again later.");
            }
            throw e;
        }
    }
    public static void setMovieDetails(MovieDetails movieDetails) {
        mainPanelController.setMovieTitle(movieDetails.getTitle());
        mainPanelController.setMovieYear(movieDetails.getYear());
        mainPanelController.setMovieGenre(movieDetails.getGenre());
        mainPanelController.setImdbRating(movieDetails.getImdbRating());
        mainPanelController.setRtRating(movieDetails.getRtRating());
        mainPanelController.setMoviePlot(movieDetails.getPlot());
        mainPanelController.setMoviePoster(movieDetails.getPosterUrl());
        mainPanelController.setTvSeriesDetails(movieDetails.getType(), movieDetails.getTotalSeasons());
    }

    private static String[] parseMovieInput(String movieInput) {
        String[] parts = movieInput.split("\\s(?=\\d{4}$)");
        if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            return new String[0];  // Return null if invalid input format or empty fields
        }
        return parts;
    }

    private static JsonObject fetchMovieJsonResponse(String url) throws IOException, MainPanelController.MovieDataParseException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                String responseString = EntityUtils.toString(response.getEntity());
                return JsonParser.parseString(responseString).getAsJsonObject();
            } catch (ParseException e) {
                // Throw a custom exception instead of a generic RuntimeException
                throw new MainPanelController.MovieDataParseException("Error parsing the movie data response.", e);
            }
        }
    }

    private MovieDetails handleMovieNotFound(JsonObject json) {
        String errorMessage = json.has("Error") ? json.get("Error").getAsString() : "Unknown error";
        System.out.println("Movie not found: " + errorMessage);
        return new MovieDetails(null, null, null, null, null, null, null, null, null,null);
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

    public static String getFirstTwoGenres(String genre) {
        if (genre != null && !genre.isEmpty()) {
            String[] genres = genre.split(", ");
            return genres.length > 1 ? genres[0] + ", " + genres[1] : genres[0];
        }
        return "Unknown"; // Fallback if genre is null or empty
    }

    public static String handleMovieStatus(String status) {
        if ("watchLater".equals(status)) {
            return "watchLater";
        } else if ("watched".equals(status)) {
            return "watched";
        } else {
            System.out.println("Invalid movie status provided.");
            return "invalid";
        }
    }

    public String handleUserRating(String rating) {
        if (rating != null && !rating.isEmpty()) {
            System.out.println("User rating selected: " + rating);
            return rating;
        } else {
            System.out.println("Invalid user rating provided.");
            return "invalid";
        }
    }

    public void saveMovieData(String movieStatus, String userRating) {
        // Validation logic
        if (movieStatus == null || userRating == null) {
            System.out.println("Invalid data. Movie status and rating are required.");
            return;
        }

        // Use the repository instance to save the movie data
        movieRepository.saveMovie(movieStatus, userRating);
    }

    public MovieRepository getMovieRepository() {
        return movieRepository;
    }


    public void setOnClearErrorCallback(Runnable callback) {
        this.onClearErrorCallback = callback;
    }

    public void setOnErrorCallback(Consumer<String> callback) {
        this.onErrorCallback = callback;
    }
}



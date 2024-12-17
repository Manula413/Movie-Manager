package com.manula413.movie_manager.database;
import com.manula413.movie_manager.model.MovieDetails;

import java.sql.*;

public class MovieRepository {


    public void addMovieToDatabase() {
        System.out.println("Worked till here 1");

        // Check if movieDetails is not null
        if (movieDetails != null) {
            // Retrieve movie details
            String title = movieDetails.getTitle();
            String year = movieDetails.getYear();
            String genre = getFirstTwoGenres(movieDetails.getGenre());
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

    private boolean isMovieInUserList(Connection connectDB, int userId, int movieId) throws SQLException {
        String checkUserMovieQuery = "SELECT COUNT(*) FROM user_movies WHERE userid = ? AND movieid = ?";
        try (PreparedStatement checkUserMovieStmt = connectDB.prepareStatement(checkUserMovieQuery)) {
            checkUserMovieStmt.setInt(1, userId);
            checkUserMovieStmt.setInt(2, movieId);
            ResultSet resultSet = checkUserMovieStmt.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }



}

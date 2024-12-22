package com.manula413.movie_manager.database;

import com.manula413.movie_manager.model.MovieDetails;
import com.manula413.movie_manager.util.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WatchListRepository {

    public ObservableList<MovieDetails> fetchWatchedMovies(String type) {
        String userMoviesQuery = "SELECT movieid, userComment FROM user_movies WHERE userid = ? AND watched_Status = 'watched'";
        String movieDetailsQuery = "SELECT movieName, year, genre, imdb_Rating, seasons FROM movies WHERE movieid = ? AND type = ?";

        ObservableList<MovieDetails> movieList = FXCollections.observableArrayList();

        DatabaseConnection connectNow = new DatabaseConnection();
        String userId = Session.getInstance().getUserId();

        try (Connection connectDB = connectNow.getConnection();
             PreparedStatement userMoviesStmt = connectDB.prepareStatement(userMoviesQuery);
             PreparedStatement movieDetailsStmt = connectDB.prepareStatement(movieDetailsQuery)) {

            userMoviesStmt.setInt(1, Integer.parseInt(userId));
            ResultSet userMoviesResult = userMoviesStmt.executeQuery();

            while (userMoviesResult.next()) {
                int movieId = userMoviesResult.getInt("movieid");
                String userComment = userMoviesResult.getString("userComment");

                movieDetailsStmt.setInt(1, movieId);
                movieDetailsStmt.setString(2, type);
                ResultSet movieDetailsResult = movieDetailsStmt.executeQuery();

                if (movieDetailsResult.next()) {
                    String movieName = movieDetailsResult.getString("movieName");
                    String year = movieDetailsResult.getString("year");
                    String genre = movieDetailsResult.getString("genre");
                    String imdbRating = movieDetailsResult.getString("imdb_Rating");
                    String seasons = movieDetailsResult.getString("seasons");

                    MovieDetails movie = new MovieDetails(
                            movieName, year, genre, imdbRating, null, null, null, null, seasons, userComment
                    );
                    movieList.add(movie);
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }

        return movieList;
    }
}
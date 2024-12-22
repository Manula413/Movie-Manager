package com.manula413.movie_manager.services;

import com.manula413.movie_manager.database.WatchListRepository;
import com.manula413.movie_manager.model.MovieDetails;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class WatchListServices {

    private final WatchListRepository repository = new WatchListRepository();

    public ObservableList<MovieDetails> getWatchedMoviesDetails(String type) {
        return repository.fetchWatchedMovies(type);
    }
    public ObservableList<MovieDetails> getWatchLaterMoviesDetails(String type) {
        return repository.fetchWatchLaterMovies(type);
    }

    public void showMovieDetails(MovieDetails movie) {
        System.out.println("Showing details for movie: " + movie.getTitle());
    }

    public static String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String[] words = input.split(" ");
        StringBuilder capitalized = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return capitalized.toString().trim();
    }
}

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

    public void showMovieDetails(MovieDetails movie) {
        System.out.println("Showing details for movie: " + movie.getTitle());
    }
}

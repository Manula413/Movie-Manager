package com.manula413.movie_manager.database;

import java.sql.Connection;
import java.sql.SQLException;

public class MovieRepository {

    public boolean isMovieInUserList(Connection connectDB, int userId, int movieId) throws SQLException {
        return false; // Placeholder return
    }

    public int getOrInsertMovie(Connection connectDB, String movieName, String movieYear, String genre,
                                String type, String imdbRating, String rtRating, String seasons) throws SQLException {
        return 0; // Placeholder return
    }

    public boolean addUserMovie(Connection connectDB, int userId, int movieId, String userComment, String status) throws SQLException {
        return false; // Placeholder return
    }

}

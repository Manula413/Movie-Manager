package com.manula413.movie_manager.controller;

import com.manula413.movie_manager.database.DatabaseConnection;
import com.manula413.movie_manager.model.MovieDetails;
import com.manula413.movie_manager.util.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class WatchedListController implements Initializable {

    @FXML
    private TableView<MovieDetails> watchedListTableView;

    @FXML
    private Label usernameLabel;

    private final String userId = Session.getInstance().getUserId();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns(); // Setup the table columns
        String username = Session.getInstance().getUsername();
        setUsernameLabel(username);
        getWatchedMoviesDetails(); // Populate the table with data
    }

    /**
     * Sets up the table columns with specified headings.
     */
    private void setupTableColumns() {
        // Define and configure columns
        TableColumn<MovieDetails, String> movieNameColumn = createColumn("Movie Name", "title", 300);
        TableColumn<MovieDetails, String> yearColumn = createColumn("Year", "year", 80);
        TableColumn<MovieDetails, String> genreColumn = createColumn("Genre", "genre", 200);
        TableColumn<MovieDetails, String> imdbRatingColumn = createColumn("IMDb Rating", "imdbRating", 120);
        TableColumn<MovieDetails, String> userCommentColumn = createColumn("User Comment", "userComment", 150);

        // Action column with button
        TableColumn<MovieDetails, Void> actionColumn = createActionColumn("Action", 82);

        // Add columns to TableView
        watchedListTableView.getColumns().addAll(
                movieNameColumn, yearColumn, genreColumn, imdbRatingColumn, userCommentColumn, actionColumn
        );

        // Set preferred table width
        watchedListTableView.setPrefWidth(932);
    }

    /**
     * Helper method to create a TableColumn with specified properties.
     */
    private <T> TableColumn<MovieDetails, T> createColumn(String title, String property, double width) {
        TableColumn<MovieDetails, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        return column;
    }

    /**
     * Helper method to create an Action column with a button in each cell.
     */
    private TableColumn<MovieDetails, Void> createActionColumn(String title, double width) {
        TableColumn<MovieDetails, Void> column = new TableColumn<>(title);
        column.setPrefWidth(width);

        column.setCellFactory(param -> new TableCell<>() {
            private final Button actionButton = new Button("Details");

            {
                actionButton.setOnAction(event -> {
                    MovieDetails movie = getTableView().getItems().get(getIndex());
                    showMovieDetails(movie); // Custom action for the button
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionButton);
            }
        });

        return column;
    }


    /**
     * Populates the `TableView` with data from the database.
     */
    public void getWatchedMoviesDetails() {
        String userMoviesQuery = "SELECT movieid, userComment FROM user_movies WHERE userid = ? AND watched_Status = 'watched'";
        String movieDetailsQuery = "SELECT movieName, year, genre, imdb_Rating FROM movies WHERE movieid = ?";

        ObservableList<MovieDetails> movieList = FXCollections.observableArrayList();

        DatabaseConnection connectNow = new DatabaseConnection();
        try (Connection connectDB = connectNow.getConnection();
             PreparedStatement userMoviesStmt = connectDB.prepareStatement(userMoviesQuery);
             PreparedStatement movieDetailsStmt = connectDB.prepareStatement(movieDetailsQuery)) {

            userMoviesStmt.setInt(1, Integer.parseInt(userId));
            ResultSet userMoviesResult = userMoviesStmt.executeQuery();

            while (userMoviesResult.next()) {
                int movieId = userMoviesResult.getInt("movieid");
                String userComment = userMoviesResult.getString("userComment");

                movieDetailsStmt.setInt(1, movieId);
                ResultSet movieDetailsResult = movieDetailsStmt.executeQuery();

                if (movieDetailsResult.next()) {
                    String movieName = movieDetailsResult.getString("movieName");
                    String year = movieDetailsResult.getString("year");
                    String genre = movieDetailsResult.getString("genre");
                    String imdbRating = movieDetailsResult.getString("imdb_Rating");

                    // Create a MovieDetails object and add it to the list
                    MovieDetails movie = new MovieDetails(movieName, year, genre, imdbRating, null, null, null, null, null, userComment);
                    movieList.add(movie);
                }
            }

            // Populate the TableView
            watchedListTableView.setItems(movieList);

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Displays movie details in the console (replace with your desired functionality).
     */
    private void showMovieDetails(MovieDetails movie) {
        // Placeholder for actual action logic
        System.out.println("Showing details for movie: " + movie.getTitle());
    }

    /**
     * Sets the username label on the UI.
     */
    public void setUsernameLabel(String username) {
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + username + "!");
        }
    }

    /**
     * Navigation logic for buttons (Add Movies, Watched List, Watch Later List).
     */
    public void navigateTo(String fxmlPath, String title, ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane panel = loader.load();
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
}

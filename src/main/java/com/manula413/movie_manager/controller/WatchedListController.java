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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class WatchedListController implements Initializable {

    @FXML
    private TableView<MovieDetails> watchedListTableView; // Correct generic type
    @FXML
    private Button addMoviesNavButton;
    @FXML
    private Button watchedListNavButton;
    @FXML
    private Button watchLaterNavButton;
    @FXML
    private Label usernameLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        String username = Session.getInstance().getUsername();
        setUsernameLabel(username);
        populateWatchedMoviesTable();
    }

    private void setupTableColumns() {
        TableColumn<MovieDetails, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));

        TableColumn<MovieDetails, String> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty(cellData.getValue().getYear()));

        TableColumn<MovieDetails, String> genreCol = new TableColumn<>("Genre");
        genreCol.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty(cellData.getValue().getGenre()));

        TableColumn<MovieDetails, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));

        TableColumn<MovieDetails, String> seasonsCol = new TableColumn<>("Seasons");
        seasonsCol.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty(cellData.getValue().getTotalSeasons()));

        TableColumn<MovieDetails, String> imdbRatingCol = new TableColumn<>("IMDb Rating");
        imdbRatingCol.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty(cellData.getValue().getImdbRating()));

        TableColumn<MovieDetails, String> userCommentCol = new TableColumn<>("User Comment");
        userCommentCol.setCellValueFactory(cellData -> javafx.beans.property.SimpleStringProperty(cellData.getValue().getPlot()));

        watchedListTableView.getColumns().addAll(titleCol, yearCol, genreCol, typeCol, seasonsCol, imdbRatingCol, userCommentCol);
    }

    private ObservableList<MovieDetails> getWatchedListFromDB() {
        String userid = Session.getInstance().getUserId();
        ObservableList<MovieDetails> watchedMovies = FXCollections.observableArrayList();

        String query = """
                SELECT m.movieName, m.year, m.genre, m.type, m.seasons, m.imdb_Rating, um.userComment
                FROM user_movies um
                JOIN movies m ON um.movieid = m.movieid
                WHERE um.userid = ? AND um.watched_Status = 'watched'
                """;

        try (Connection connectDB = new DatabaseConnection().getConnection();
             PreparedStatement preparedStatement = connectDB.prepareStatement(query)) {

            preparedStatement.setInt(1, Integer.parseInt(userid));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                watchedMovies.add(new MovieDetails(
                        resultSet.getString("movieName"),
                        resultSet.getString("year"),
                        resultSet.getString("genre"),
                        resultSet.getString("imdb_Rating"),
                        null, // rtRating (not used here)
                        resultSet.getString("userComment"), // Using plot to hold userComment
                        null, // posterUrl (not used here)
                        resultSet.getString("type"),
                        resultSet.getString("seasons")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return watchedMovies;
    }

    private void populateWatchedMoviesTable() {
        ObservableList<MovieDetails> watchedMovies = getWatchedListFromDB();
        watchedListTableView.setItems(watchedMovies);
    }

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

    public void setUsernameLabel(String username) {
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + username + "!");
        }
    }
}

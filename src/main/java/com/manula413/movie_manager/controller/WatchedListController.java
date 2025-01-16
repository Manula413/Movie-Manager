package com.manula413.movie_manager.controller;

import com.manula413.movie_manager.model.MovieDetails;
import com.manula413.movie_manager.services.WatchListServices;
import com.manula413.movie_manager.util.Session;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WatchedListController implements Initializable {

    @FXML
    private TableView<MovieDetails> watchedListTableView;

    @FXML
    private Label displayNameLabel;

    @FXML
    private RadioButton tvSeriesRadioButton;

    @FXML
    private RadioButton moviesRadioButton;

    @FXML
    private Button btnSidebar;

    @FXML
    private Pane sidebarContainer;

    @FXML
    private StackPane rootLayout;

    private final WatchListServices watchListServices = new WatchListServices();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns(false);
        setDisplayNameLabel(Session.getInstance().getDisplayName());
        setupRadioButtonListeners();
        updateTable("movie");
        setupSidebarToggle();
        setSidebarContainer(sidebarContainer);
    }

    public void setSidebarContainer(Pane sidebarContainer) {
        this.sidebarContainer = sidebarContainer;
        attachSidebarBehavior();  // Ensure sidebar toggle is attached
    }

    private void attachSidebarBehavior() {
        if (btnSidebar != null && sidebarContainer != null) {
            btnSidebar.setOnAction(event -> toggleSidebar());
        }
    }

    private void setupRadioButtonListeners() {
        ToggleGroup toggleGroup = new ToggleGroup();
        tvSeriesRadioButton.setToggleGroup(toggleGroup);
        moviesRadioButton.setToggleGroup(toggleGroup);

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            boolean isSeries = newValue == tvSeriesRadioButton;
            setupTableColumns(isSeries);
            updateTable(isSeries ? "series" : "movie");
        });
    }

    private void setupTableColumns(boolean includeSeasons) {
        watchedListTableView.getColumns().clear();

        watchedListTableView.getColumns().addAll(
                createColumn("Title", "title", 300),
                createCenteredColumn("Year", "year", 80),
                createColumn("Genre", "genre", 200),
                createCenteredColumn("IMDb Rating", "imdbRating", 120),
                createCenteredColumn("User Comment", "userComment", 150)
        );

        if (includeSeasons) {
            watchedListTableView.getColumns().add(createCenteredColumn("Seasons", "totalSeasons", 100));
        }

        watchedListTableView.getColumns().add(createActionColumn("Action", 82));
    }

    private <T> TableColumn<MovieDetails, T> createColumn(String title, String property, double width) {
        TableColumn<MovieDetails, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        return column;
    }

    private <T> TableColumn<MovieDetails, T> createCenteredColumn(String title, String property, double width) {
        TableColumn<MovieDetails, T> column = createColumn(title, property, width);
        column.setCellFactory(tc -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item.toString());
                    setAlignment(Pos.CENTER);
                }
            };
        });
        return column;
    }

    private TableColumn<MovieDetails, Void> createActionColumn(String title, double width) {
        TableColumn<MovieDetails, Void> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setCellFactory(param -> new TableCell<>() {
            private final Button actionButton = new Button("Details");

            {
                actionButton.setOnAction(event -> {
                    MovieDetails movie = getTableView().getItems().get(getIndex());
                    watchListServices.showMovieDetails(movie);
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

    private void updateTable(String type) {
        watchedListTableView.setItems(watchListServices.getWatchedMoviesDetails(type));
    }

    private void setDisplayNameLabel(String displayName) {
        displayNameLabel.setText("Welcome, " + displayName + "!");
    }

    private void setupSidebarToggle() {
        btnSidebar.setOnAction(event -> toggleSidebar());
    }

    private void toggleSidebar() {
        if (sidebarContainer != null) {
            double targetX = (sidebarContainer.getTranslateX() == 0) ? -300 : 0;

            TranslateTransition slide = new TranslateTransition(Duration.millis(300), sidebarContainer);
            slide.setToX(targetX);
            slide.play();

            sidebarContainer.setMouseTransparent(targetX != 0);
        } else {
            System.err.println("Sidebar container is null!");
        }
    }

    private void animateSidebar(double targetX) {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebarContainer);
        transition.setToX(targetX);
        sidebarContainer.setMouseTransparent(targetX == -200);
        transition.play();
    }
}





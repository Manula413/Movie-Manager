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

    private final WatchListServices watchListServices = new WatchListServices();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns(false);
        String displayName = Session.getInstance().getDisplayName();
        setDisplayNameLabel(displayName);
        setupRadioButtonListeners();
        updateTable("movie");
    }

    public Button getBtnSidebar() {
        return btnSidebar;
    }


    private void setupRadioButtonListeners() {
        ToggleGroup toggleGroup = tvSeriesRadioButton.getToggleGroup();
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == moviesRadioButton) {
                setupTableColumns(false);
                updateTable("movie");
            } else if (newValue == tvSeriesRadioButton) {
                setupTableColumns(true);
                updateTable("series");
            }
        });
    }

    private void setupTableColumns(boolean includeSeasons) {
        watchedListTableView.getColumns().clear();

        TableColumn<MovieDetails, String> movieNameColumn = createColumn("Title", "title", 300);
        TableColumn<MovieDetails, String> yearColumn = createColumn("Year", "year", 80);
        TableColumn<MovieDetails, String> genreColumn = createColumn("Genre", "genre", 200);
        TableColumn<MovieDetails, String> imdbRatingColumn = createColumn("IMDb Rating", "imdbRating", 120);
        TableColumn<MovieDetails, String> userCommentColumn = createColumn("User Comment", "userComment", 150);

        setColumnCenterAlignment(yearColumn);
        setColumnCenterAlignment(imdbRatingColumn);
        setColumnCenterAlignment(userCommentColumn);

        watchedListTableView.getColumns().addAll(movieNameColumn, yearColumn, genreColumn, imdbRatingColumn, userCommentColumn);

        if (includeSeasons) {
            TableColumn<MovieDetails, String> seasonsColumn = createColumn("Seasons", "totalSeasons", 100);
            setColumnCenterAlignment(seasonsColumn);
            watchedListTableView.getColumns().add(seasonsColumn);
        }

        TableColumn<MovieDetails, Void> actionColumn = createActionColumn("Action", 82);
        setColumnCenterAlignment(actionColumn);
        watchedListTableView.getColumns().add(actionColumn);
    }

    private <T> TableColumn<MovieDetails, T> createColumn(String title, String property, double width) {
        TableColumn<MovieDetails, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        return column;
    }

    private <T> void setColumnCenterAlignment(TableColumn<MovieDetails, T> column) {
        column.setCellFactory(tc -> {
            TableCell<MovieDetails, T> cell = new TableCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? "" : item != null ? item.toString() : "");
                }
            };
            cell.setStyle("-fx-alignment: center;");
            return cell;
        });
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
        ObservableList<MovieDetails> movies = watchListServices.getWatchedMoviesDetails(type);
        watchedListTableView.setItems(movies);
    }

    public void setDisplayNameLabel(String displayName) {
        if (displayNameLabel != null) {
            displayNameLabel.setText("Welcome, " + displayName + "!");
        }
    }

    public void navigateTo(String fxmlPath, String title, ActionEvent event) {
        try {
            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Load the target FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane panel = loader.load();

            // Access controller to get btnSidebar
            Object controller = loader.getController();
            Button btnSidebar = null;

            if (controller instanceof MainPanelController) {
                btnSidebar = ((MainPanelController) controller).getBtnSidebar();
            }

            // Load the sidebar
            FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("/com/manula413/movie_manager/sidebar.fxml"));
            VBox sidebar = sidebarLoader.load();

            // Sidebar container
            Pane sidebarContainer = new Pane(sidebar);
            sidebarContainer.setPrefSize(300, 800);
            sidebarContainer.setTranslateX(-300);
            sidebarContainer.setMouseTransparent(true);

            // Bind toggle action to existing button
            if (btnSidebar != null) {
                btnSidebar.setOnAction(e -> toggleSidebar(sidebarContainer));
            }

            // StackPane setup
            StackPane rootLayout = new StackPane();
            rootLayout.getChildren().addAll(panel, sidebarContainer);
            StackPane.setAlignment(sidebarContainer, Pos.CENTER_LEFT);

            // Scene setup
            Scene scene = new Scene(rootLayout, 1300, 800);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Sidebar Toggle with Mouse Event Handling
    private void toggleSidebar(Pane sidebarContainer) {
        double currentX = sidebarContainer.getTranslateX();
        double targetX = (currentX == 0) ? -200 : 0;

        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebarContainer);
        transition.setToX(targetX);
        transition.setOnFinished(event -> {
            // Disable mouse clicks when hidden
            sidebarContainer.setMouseTransparent(targetX == -200);
        });
        transition.play();
    }

    private void handleOutsideClickToHideSidebar(StackPane rootLayout, Pane sidebarContainer) {
        rootLayout.setOnMouseClicked(event -> {
            // Check if the click is outside the sidebar
            if (!sidebarContainer.contains(event.getSceneX(), event.getSceneY())) {
                // Hide the sidebar by translating it to the left (out of view)
                TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebarContainer);
                transition.setToX(-200); // Moves the sidebar out of view
                transition.setOnFinished(e -> sidebarContainer.setMouseTransparent(true)); // Disable interaction when hidden
                transition.play();
            }
        });
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

package com.manula413.movie_manager.model;

public class MovieDetails {
    private String title;
    private String year;
    private String genre;
    private String imdbRating;
    private String rtRating;
    private String plot;
    private String posterUrl;
    private String type;
    private String totalSeasons;
    private String userComment;



    // Constructor
    public MovieDetails(String title, String year, String genre, String imdbRating, String rtRating, String plot, String posterUrl, String type, String totalSeasons,String userComment) {
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.imdbRating = imdbRating;
        this.rtRating = rtRating;
        this.plot = plot;
        this.posterUrl = posterUrl;
        this.type = type;
        this.totalSeasons = totalSeasons;
        this.userComment = userComment;
    }

    // Getter methods
    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public String getRtRating() {
        return rtRating;
    }

    public String getPlot() {
        return plot;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getTotalSeasons() {
        return totalSeasons;
    }

    public String getType() {
        return type;
    }
    public String getUserComment() {
        return userComment;
    }
}

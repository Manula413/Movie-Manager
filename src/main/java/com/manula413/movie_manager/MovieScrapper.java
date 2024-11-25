package com.manula413.movie_manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class MovieScrapper {

    private static final String API_KEY = "3115537f"; // Use your OMDB API key

    public static void getMovieDetails(String movieName, String movieYear) throws Exception {
        String url = "http://www.omdbapi.com/?t=" + movieName.replace(" ", "+") + "&y=" + movieYear + "&apikey=" + API_KEY;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                String responseString = EntityUtils.toString(response.getEntity());

                // Parse the JSON response using Gson
                JsonObject json = JsonParser.parseString(responseString).getAsJsonObject();

                String title = json.has("Title") ? json.get("Title").getAsString() : "N/A";
                String year = json.has("Year") ? json.get("Year").getAsString() : "N/A";
                String genre = json.has("Genre") ? json.get("Genre").getAsString() : "N/A";
                String imdbRating = json.has("imdbRating") ? json.get("imdbRating").getAsString() : "N/A";
                String poster = json.has("Poster") ? json.get("Poster").getAsString() : "N/A";

                // Print movie details
                System.out.println("Title: " + title);
                System.out.println("Year: " + year);
                System.out.println("Genre: " + genre);
                System.out.println("IMDb Rating: " + imdbRating);
                System.out.println("Poster URL: " + poster);
            }
        }
    }

    public static void main(String[] args) {
        try {
            // Example movie details to fetch
            String movieName = "we live in time";
            String movieYear = "2024";

            // Call the getMovieDetails method
            System.out.println("Fetching movie details...");
            getMovieDetails(movieName, movieYear);
        } catch (Exception e) {
            System.err.println("An error occurred while fetching movie details: " + e.getMessage());
        }
    }
}

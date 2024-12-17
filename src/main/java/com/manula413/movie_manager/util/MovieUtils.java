package com.manula413.movie_manager.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

public class MovieUtils {

    public boolean isValidURL(String urlString) {
        try {
            new URL(urlString).toURI();  // Try to convert the string to a URI
            return true;
        } catch (URISyntaxException | MalformedURLException e) {
            return false;  // Invalid URL
        }
    }

    public static String getAPIKey() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/resources/keys.properties")) {
            properties.load(input);
        }
        return properties.getProperty("api.key");
    }
}

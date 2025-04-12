package com.example.movierecommender.util;

import android.content.Context;
import android.util.Log;

import com.example.movierecommender.data.local.MovieDao;
import com.example.movierecommender.data.local.entity.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {
    private static final String TAG = "DataLoader";
    private static final String MOVIES_FILE = "movies.json";
    private final Context context;

    public DataLoader(Context context) {
        this.context = context;
    }

    public void loadInitialData(MovieDao movieDao) {
        if (movieDao.getMovieCount() > 0) {
            Log.d(TAG, "Database already has data, skipping initial load");
            return;
        }

        try {
            String jsonData = loadJSONFromAsset(MOVIES_FILE);
            if (jsonData != null) {
                List<Movie> movies = parseMoviesJson(jsonData);
                movieDao.insertMovies(movies);
                Log.d(TAG, "Loaded " + movies.size() + " movies into database");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading initial data", e);
        }
    }

    private String loadJSONFromAsset(String fileName) {
        String json;
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, StandardCharsets.UTF_8);
            return json;
        } catch (IOException e) {
            Log.e(TAG, "Error reading JSON file: " + fileName, e);
            return null;
        }
    }

    private List<Movie> parseMoviesJson(String jsonData) {
        Gson gson = new Gson();
        Type movieListType = new TypeToken<ArrayList<MovieJson>>() {}.getType();
        List<MovieJson> movieJsonList = gson.fromJson(jsonData, movieListType);

        List<Movie> movies = new ArrayList<>();
        for (MovieJson movieJson : movieJsonList) {
            Movie movie = new Movie(
                    String.valueOf(movieJson.id),
                    movieJson.title,
                    movieJson.overview,
                    movieJson.poster_path,
                    movieJson.backdrop_path,
                    movieJson.release_date,
                    movieJson.vote_average,
                    String.join(",", movieJson.genres),
                    extractYear(movieJson.release_date),
                    movieJson.director,
                    String.join(",", movieJson.cast),
                    movieJson.runtime
            );
            movies.add(movie);
        }

        return movies;
    }

    private int extractYear(String releaseDate) {
        if (releaseDate == null || releaseDate.isEmpty()) {
            return 0;
        }
        try {
            // Assuming format is "YYYY-MM-DD"
            return Integer.parseInt(releaseDate.substring(0, 4));
        } catch (Exception e) {
            return 0;
        }
    }

    // Helper class for parsing JSON
    private static class MovieJson {
        public int id;
        public String title;
        public String overview;
        public String poster_path;
        public String backdrop_path;
        public String release_date;
        public double vote_average;
        public List<String> genres;
        public int release_year;
        public String director;
        public List<String> cast;
        public int runtime;
    }
}
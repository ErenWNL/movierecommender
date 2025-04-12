package com.example.movierecommender.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.movierecommender.data.local.MovieDao;
import com.example.movierecommender.data.local.MovieDatabase;
import com.example.movierecommender.data.local.entity.Movie;
import com.example.movierecommender.data.local.entity.UserMovie;
import com.example.movierecommender.util.RecommendationEngine;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MovieRepository {
    private final MovieDao movieDao;
    private final LiveData<List<Movie>> allMovies;
    private final RecommendationEngine recommendationEngine;

    public static final String ACTION_WATCHED = "WATCHED";
    public static final String ACTION_FAVORITE = "FAVORITE";
    public static final String ACTION_DISLIKED = "DISLIKED";

    public MovieRepository(Application application) {
        MovieDatabase db = MovieDatabase.getDatabase(application);
        movieDao = db.movieDao();
        allMovies = movieDao.getAllMovies();
        recommendationEngine = new RecommendationEngine(this);
    }

    // Movie methods
    public LiveData<List<Movie>> getAllMovies() {
        return allMovies;
    }

    public LiveData<Movie> getMovieById(String movieId) {
        return movieDao.getMovieById(movieId);
    }

    // User action methods
    public void addToWatched(String movieId, float rating) {
        MovieDatabase.databaseWriteExecutor.execute(() -> {
            // First check if movie exists
            Movie movie = movieDao.getMovieByIdSync(movieId);
            if (movie != null) {
                UserMovie userMovie = new UserMovie(movieId, ACTION_WATCHED, System.currentTimeMillis(), rating);
                movieDao.insertUserMovie(userMovie);
            } else {
                Log.e("MovieRepository", "Cannot mark movie as watched - movie ID " + movieId + " does not exist");
                // Consider adding a callback or using LiveData to inform the UI
            }
        });
    }

    public void addToFavorites(String movieId) {
        MovieDatabase.databaseWriteExecutor.execute(() -> {
            // First check if movie exists
            Movie movie = movieDao.getMovieByIdSync(movieId);
            if (movie != null) {
                UserMovie userMovie = new UserMovie(movieId, ACTION_FAVORITE, System.currentTimeMillis(), 5.0f);
                movieDao.insertUserMovie(userMovie);
            } else {
                Log.e("MovieRepository", "Cannot mark movie as favorite - movie ID " + movieId + " does not exist");
                // Consider adding a callback or using LiveData to inform the UI
            }
        });
    }

    public void addToDisliked(String movieId) {
        MovieDatabase.databaseWriteExecutor.execute(() -> {
            // First check if movie exists
            Movie movie = movieDao.getMovieByIdSync(movieId);
            if (movie != null) {
                UserMovie userMovie = new UserMovie(movieId, ACTION_DISLIKED, System.currentTimeMillis(), 1.0f);
                movieDao.insertUserMovie(userMovie);
            } else {
                Log.e("MovieRepository", "Cannot mark movie as disliked - movie ID " + movieId + " does not exist");
                // Consider adding a callback or using LiveData to inform the UI
            }
        });
    }

    public void removeUserAction(String movieId, String userAction) {
        MovieDatabase.databaseWriteExecutor.execute(() -> {
            // First check if movie exists
            Movie movie = movieDao.getMovieByIdSync(movieId);
            if (movie != null) {
                movieDao.deleteUserMovie(movieId, userAction);
            } else {
                Log.e("MovieRepository", "Cannot remove user action - movie ID " + movieId + " does not exist");
                // Consider adding a callback or using LiveData to inform the UI
            }
        });
    }

    public LiveData<List<Movie>> getWatchedMovies() {
        return movieDao.getMoviesByUserAction(ACTION_WATCHED);
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return movieDao.getMoviesByUserAction(ACTION_FAVORITE);
    }

    public LiveData<List<Movie>> getDislikedMovies() {
        return movieDao.getMoviesByUserAction(ACTION_DISLIKED);
    }

    public boolean isMovieInUserAction(String movieId, String userAction) {
        try {
            Future<Boolean> future = MovieDatabase.databaseWriteExecutor.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    return movieDao.getUserMovieSync(movieId, userAction) != null;
                }
            });
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Recommendation methods
    public List<Movie> getRecommendedMovies(int limit) {
        try {
            Future<List<Movie>> future = MovieDatabase.databaseWriteExecutor.submit(() ->
                    recommendationEngine.getRecommendations(limit));
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Data access methods for recommendation engine
    public List<Movie> getRandomUnratedMovies(int limit) {
        try {
            Future<List<Movie>> future = MovieDatabase.databaseWriteExecutor.submit(() ->
                    movieDao.getRandomUnratedMovies(limit));
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Movie> getRecentUserActionMovies(String userAction, int limit) {
        try {
            Future<List<Movie>> future = MovieDatabase.databaseWriteExecutor.submit(() ->
                    movieDao.getRecentUserActionMovies(userAction, limit));
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getAllGenres() {
        try {
            Future<List<String>> future = MovieDatabase.databaseWriteExecutor.submit(() ->
                    movieDao.getAllGenres());
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Movie> getTopMoviesByGenre(String genre, int limit) {
        try {
            Future<List<Movie>> future = MovieDatabase.databaseWriteExecutor.submit(() ->
                    movieDao.getTopMoviesByGenre(genre, limit));
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Movie> getMoviesByDirectors(List<String> directors, int limit) {
        try {
            Future<List<Movie>> future = MovieDatabase.databaseWriteExecutor.submit(() ->
                    movieDao.getMoviesByDirectors(directors, limit));
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
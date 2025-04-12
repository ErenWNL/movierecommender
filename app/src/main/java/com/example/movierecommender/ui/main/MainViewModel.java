package com.example.movierecommender.ui.main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movierecommender.data.local.entity.Movie;
import com.example.movierecommender.data.repository.MovieRepository;
import com.example.movierecommender.util.Constants;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel {
    private final MovieRepository repository;
    private final ExecutorService executorService;
    private final MutableLiveData<List<Movie>> recommendedMovies;
    private final LiveData<List<Movie>> allMovies;
    private final LiveData<List<Movie>> watchedMovies;
    private final LiveData<List<Movie>> favoriteMovies;

    public MainViewModel(Application application) {
        super(application);
        repository = new MovieRepository(application);
        executorService = Executors.newSingleThreadExecutor();
        recommendedMovies = new MutableLiveData<>();
        allMovies = repository.getAllMovies();
        watchedMovies = repository.getWatchedMovies();
        favoriteMovies = repository.getFavoriteMovies();

        // Load initial recommendations
        loadRecommendations();
    }

    public LiveData<List<Movie>> getAllMovies() {
        return allMovies;
    }

    public LiveData<List<Movie>> getWatchedMovies() {
        return watchedMovies;
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return favoriteMovies;
    }

    public LiveData<List<Movie>> getRecommendedMovies() {
        return recommendedMovies;
    }

    public void loadRecommendations() {
        executorService.execute(() -> {
            List<Movie> recommendations = repository.getRecommendedMovies(Constants.RECOMMENDATION_LIMIT);
            recommendedMovies.postValue(recommendations);
        });
    }

    public void markAsWatched(String movieId, float rating) {
        repository.addToWatched(movieId, rating);
        loadRecommendations(); // Refresh recommendations
    }

    public void markAsFavorite(String movieId) {
        repository.addToFavorites(movieId);
        loadRecommendations(); // Refresh recommendations
    }

    public void markAsDisliked(String movieId) {
        repository.addToDisliked(movieId);
        loadRecommendations(); // Refresh recommendations
    }

    public void removeFromWatched(String movieId) {
        repository.removeUserAction(movieId, MovieRepository.ACTION_WATCHED);
    }

    public void removeFromFavorites(String movieId) {
        repository.removeUserAction(movieId, MovieRepository.ACTION_FAVORITE);
    }

    public void removeFromDisliked(String movieId) {
        repository.removeUserAction(movieId, MovieRepository.ACTION_DISLIKED);
    }

    public boolean isMovieWatched(String movieId) {
        return repository.isMovieInUserAction(movieId, MovieRepository.ACTION_WATCHED);
    }

    public boolean isMovieFavorite(String movieId) {
        return repository.isMovieInUserAction(movieId, MovieRepository.ACTION_FAVORITE);
    }

    public boolean isMovieDisliked(String movieId) {
        return repository.isMovieInUserAction(movieId, MovieRepository.ACTION_DISLIKED);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
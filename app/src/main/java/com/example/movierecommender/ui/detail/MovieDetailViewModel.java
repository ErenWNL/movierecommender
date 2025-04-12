package com.example.movierecommender.ui.detail;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.movierecommender.data.local.entity.Movie;
import com.example.movierecommender.data.repository.MovieRepository;

public class MovieDetailViewModel extends AndroidViewModel {
    private final MovieRepository repository;
    private final MutableLiveData<String> movieIdLiveData = new MutableLiveData<>();
    private final LiveData<Movie> movie;

    public MovieDetailViewModel(Application application) {
        super(application);
        repository = new MovieRepository(application);

        // Using Transformations to get movie details when movieId changes
        movie = Transformations.switchMap(movieIdLiveData, repository::getMovieById);
    }

    public void setMovieId(String movieId) {
        movieIdLiveData.setValue(movieId);
    }

    public LiveData<Movie> getMovie() {
        return movie;
    }

    public void markAsWatched(float rating) {
        String movieId = movieIdLiveData.getValue();
        if (movieId != null) {
            repository.addToWatched(movieId, rating);
        }
    }

    public void markAsFavorite() {
        String movieId = movieIdLiveData.getValue();
        if (movieId != null) {
            repository.addToFavorites(movieId);
        }
    }

    public void markAsDisliked() {
        String movieId = movieIdLiveData.getValue();
        if (movieId != null) {
            repository.addToDisliked(movieId);
        }
    }

    public void removeFromWatched() {
        String movieId = movieIdLiveData.getValue();
        if (movieId != null) {
            repository.removeUserAction(movieId, MovieRepository.ACTION_WATCHED);
        }
    }

    public void removeFromFavorites() {
        String movieId = movieIdLiveData.getValue();
        if (movieId != null) {
            repository.removeUserAction(movieId, MovieRepository.ACTION_FAVORITE);
        }
    }

    public void removeFromDisliked() {
        String movieId = movieIdLiveData.getValue();
        if (movieId != null) {
            repository.removeUserAction(movieId, MovieRepository.ACTION_DISLIKED);
        }
    }

    public boolean isMovieWatched() {
        String movieId = movieIdLiveData.getValue();
        return movieId != null && repository.isMovieInUserAction(movieId, MovieRepository.ACTION_WATCHED);
    }

    public boolean isMovieFavorite() {
        String movieId = movieIdLiveData.getValue();
        return movieId != null && repository.isMovieInUserAction(movieId, MovieRepository.ACTION_FAVORITE);
    }

    public boolean isMovieDisliked() {
        String movieId = movieIdLiveData.getValue();
        return movieId != null && repository.isMovieInUserAction(movieId, MovieRepository.ACTION_DISLIKED);
    }
}
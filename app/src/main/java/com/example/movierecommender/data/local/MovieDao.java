package com.example.movierecommender.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.movierecommender.data.local.entity.Movie;
import com.example.movierecommender.data.local.entity.UserMovie;

import java.util.List;

@Dao
public interface MovieDao {
    // Movie queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<Movie> movies);

    @Query("SELECT * FROM movies")
    LiveData<List<Movie>> getAllMovies();

    @Query("SELECT * FROM movies WHERE id = :movieId")
    LiveData<Movie> getMovieById(String movieId);

    @Query("SELECT * FROM movies WHERE id = :movieId")
    Movie getMovieByIdSync(String movieId);

    @Query("SELECT COUNT(*) FROM movies")
    int getMovieCount();

    // UserMovie queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserMovie(UserMovie userMovie);

    @Query("DELETE FROM user_movies WHERE movieId = :movieId AND userAction = :userAction")
    void deleteUserMovie(String movieId, String userAction);

    @Query("SELECT * FROM user_movies WHERE userAction = :userAction ORDER BY timestamp DESC")
    LiveData<List<UserMovie>> getUserMoviesByAction(String userAction);

    @Query("SELECT * FROM user_movies WHERE movieId = :movieId AND userAction = :userAction")
    UserMovie getUserMovieSync(String movieId, String userAction);

    // Combined queries
    @Transaction
    @Query("SELECT m.* FROM movies m INNER JOIN user_movies um ON m.id = um.movieId " +
            "WHERE um.userAction = :userAction ORDER BY um.timestamp DESC")
    LiveData<List<Movie>> getMoviesByUserAction(String userAction);

    // Recommendation-related queries
    @Query("SELECT m.* FROM movies m " +
            "WHERE m.id NOT IN (SELECT movieId FROM user_movies) " +
            "ORDER BY RANDOM() LIMIT :limit")
    List<Movie> getRandomUnratedMovies(int limit);

    @Query("SELECT m.* FROM movies m " +
            "INNER JOIN user_movies um ON m.id = um.movieId " +
            "WHERE um.userAction = :userAction " +
            "ORDER BY um.timestamp DESC LIMIT :limit")
    List<Movie> getRecentUserActionMovies(String userAction, int limit);

    @Query("SELECT DISTINCT genres FROM movies")
    List<String> getAllGenres();

    @Query("SELECT * FROM movies WHERE genres LIKE '%' || :genre || '%' " +
            "AND id NOT IN (SELECT movieId FROM user_movies) " +
            "ORDER BY voteAverage DESC LIMIT :limit")
    List<Movie> getTopMoviesByGenre(String genre, int limit);

    @Query("SELECT * FROM movies WHERE director IN (:directors) " +
            "AND id NOT IN (SELECT movieId FROM user_movies) " +
            "ORDER BY voteAverage DESC LIMIT :limit")
    List<Movie> getMoviesByDirectors(List<String> directors, int limit);
}
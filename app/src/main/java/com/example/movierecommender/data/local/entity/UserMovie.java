package com.example.movierecommender.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "user_movies",
        primaryKeys = {"movieId", "userAction"},
        foreignKeys = @ForeignKey(
                entity = Movie.class,
                parentColumns = "id",
                childColumns = "movieId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("movieId")}
)
public class UserMovie {
    @NonNull
    private String movieId;

    @NonNull
    private String userAction; // "WATCHED", "FAVORITE", "DISLIKED"

    private long timestamp;

    private float rating; // User rating from 0-5

    public UserMovie(@NonNull String movieId, @NonNull String userAction, long timestamp, float rating) {
        this.movieId = movieId;
        this.userAction = userAction;
        this.timestamp = timestamp;
        this.rating = rating;
    }

    @NonNull
    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(@NonNull String movieId) {
        this.movieId = movieId;
    }

    @NonNull
    public String getUserAction() {
        return userAction;
    }

    public void setUserAction(@NonNull String userAction) {
        this.userAction = userAction;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
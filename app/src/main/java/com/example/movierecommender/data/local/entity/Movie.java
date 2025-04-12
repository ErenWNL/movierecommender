package com.example.movierecommender.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "movies")
public class Movie {
    @PrimaryKey
    @NonNull
    private String id;
    private String title;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private String releaseDate;
    private double voteAverage;
    private String genres; // Stored as comma-separated string
    private int releaseYear;
    private String director;
    private String cast; // Stored as comma-separated string
    private int runtime;

    public Movie(@NonNull String id, String title, String overview, String posterPath,
                 String backdropPath, String releaseDate, double voteAverage,
                 String genres, int releaseYear, String director, String cast, int runtime) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.genres = genres;
        this.releaseYear = releaseYear;
        this.director = director;
        this.cast = cast;
        this.runtime = runtime;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    // Helper method to get genres as list
    public List<String> getGenresList() {
        if (genres != null && !genres.isEmpty()) {
            return java.util.Arrays.asList(genres.split(","));
        }
        return new java.util.ArrayList<>();
    }

    // Helper method to get cast as list
    public List<String> getCastList() {
        if (cast != null && !cast.isEmpty()) {
            return java.util.Arrays.asList(cast.split(","));
        }
        return new java.util.ArrayList<>();
    }
}
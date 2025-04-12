package com.example.movierecommender.util;

public class Constants {
    // Recommendation types
    public static final String GENRE_BASED = "GENRE_BASED";
    public static final String DIRECTOR_BASED = "DIRECTOR_BASED";
    public static final String POPULAR = "POPULAR";

    // Fragment tags
    public static final String TAG_HOME = "home";
    public static final String TAG_WATCHED = "watched";
    public static final String TAG_FAVORITES = "favorites";
    public static final String TAG_RECOMMENDATIONS = "recommendations";

    // Intent extras
    public static final String EXTRA_MOVIE_ID = "movie_id";

    // Shared preferences
    public static final String PREF_NAME = "movie_recommender_prefs";
    public static final String PREF_FIRST_RUN = "first_run";

    // Default poster image path (for when no poster is available)
    public static final String DEFAULT_POSTER_PATH = "android.resource://com.example.movierecommender/drawable/poster_placeholder";

    // Number of recommendations to generate
    public static final int RECOMMENDATION_LIMIT = 20;
}
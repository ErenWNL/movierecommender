package com.example.movierecommender.util;

import android.util.Log;

import com.example.movierecommender.data.local.entity.Movie;
import com.example.movierecommender.data.repository.MovieRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecommendationEngine {
    private static final String TAG = "RecommendationEngine";
    private final MovieRepository repository;

    public RecommendationEngine(MovieRepository repository) {
        this.repository = repository;
    }

    public List<Movie> getRecommendations(int limit) {
        // Get user preferences
        List<Movie> favoriteMovies = repository.getRecentUserActionMovies(MovieRepository.ACTION_FAVORITE, 10);
        List<Movie> watchedMovies = repository.getRecentUserActionMovies(MovieRepository.ACTION_WATCHED, 20);
        List<Movie> dislikedMovies = repository.getRecentUserActionMovies(MovieRepository.ACTION_DISLIKED, 10);

        // If user hasn't rated any movies, return popular or random movies
        if (favoriteMovies.isEmpty() && watchedMovies.isEmpty()) {
            return repository.getRandomUnratedMovies(limit);
        }

        // Extract preferred genres from favorites and highly rated watched movies
        Map<String, Integer> genrePreferences = extractGenrePreferences(favoriteMovies, watchedMovies, dislikedMovies);

        // Extract preferred directors
        Map<String, Integer> directorPreferences = extractDirectorPreferences(favoriteMovies, watchedMovies, dislikedMovies);

        // Generate candidate recommendations
        List<Movie> recommendations = generateCandidateRecommendations(genrePreferences, directorPreferences);

        // Filter out movies user has already interacted with
        recommendations = filterAlreadyInteractedMovies(recommendations, favoriteMovies, watchedMovies, dislikedMovies);

        // Sort recommendations by score
        recommendations = rankRecommendations(recommendations, genrePreferences, directorPreferences);

        // Limit to requested number
        if (recommendations.size() > limit) {
            recommendations = recommendations.subList(0, limit);
        }

        // If we still don't have enough recommendations, pad with popular/random movies
        if (recommendations.size() < limit) {
            int remaining = limit - recommendations.size();
            List<Movie> additionalMovies = repository.getRandomUnratedMovies(remaining);
            recommendations.addAll(additionalMovies);
        }

        return recommendations;
    }

    private Map<String, Integer> extractGenrePreferences(List<Movie> favoriteMovies,
                                                         List<Movie> watchedMovies,
                                                         List<Movie> dislikedMovies) {
        Map<String, Integer> genreScores = new HashMap<>();

        // Add scores for favorite movies' genres (higher weight)
        for (Movie movie : favoriteMovies) {
            for (String genre : movie.getGenresList()) {
                genreScores.put(genre, genreScores.getOrDefault(genre, 0) + 3);
            }
        }

        // Add scores for watched movies' genres (medium weight)
        for (Movie movie : watchedMovies) {
            for (String genre : movie.getGenresList()) {
                genreScores.put(genre, genreScores.getOrDefault(genre, 0) + 1);
            }
        }

        // Subtract scores for disliked movies' genres
        for (Movie movie : dislikedMovies) {
            for (String genre : movie.getGenresList()) {
                genreScores.put(genre, genreScores.getOrDefault(genre, 0) - 2);
            }
        }

        return genreScores;
    }

    private Map<String, Integer> extractDirectorPreferences(List<Movie> favoriteMovies,
                                                            List<Movie> watchedMovies,
                                                            List<Movie> dislikedMovies) {
        Map<String, Integer> directorScores = new HashMap<>();

        // Add scores for favorite movies' directors (higher weight)
        for (Movie movie : favoriteMovies) {
            if (movie.getDirector() != null && !movie.getDirector().isEmpty()) {
                directorScores.put(movie.getDirector(), directorScores.getOrDefault(movie.getDirector(), 0) + 3);
            }
        }

        // Add scores for watched movies' directors (medium weight)
        for (Movie movie : watchedMovies) {
            if (movie.getDirector() != null && !movie.getDirector().isEmpty()) {
                directorScores.put(movie.getDirector(), directorScores.getOrDefault(movie.getDirector(), 0) + 1);
            }
        }

        // Subtract scores for disliked movies' directors
        for (Movie movie : dislikedMovies) {
            if (movie.getDirector() != null && !movie.getDirector().isEmpty()) {
                directorScores.put(movie.getDirector(), directorScores.getOrDefault(movie.getDirector(), 0) - 2);
            }
        }

        return directorScores;
    }

    private List<Movie> generateCandidateRecommendations(Map<String, Integer> genrePreferences,
                                                         Map<String, Integer> directorPreferences) {
        List<Movie> candidates = new ArrayList<>();

        // Get top genres (with positive scores)
        List<Map.Entry<String, Integer>> sortedGenres = new ArrayList<>(genrePreferences.entrySet());
        sortedGenres.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Add movies from top 3 genres
        int genreCount = 0;
        for (Map.Entry<String, Integer> genreEntry : sortedGenres) {
            if (genreEntry.getValue() <= 0 || genreCount >= 3) {
                break;
            }

            List<Movie> genreMovies = repository.getTopMoviesByGenre(genreEntry.getKey(), 5);
            candidates.addAll(genreMovies);
            genreCount++;
        }

        // Get top directors (with positive scores)
        List<Map.Entry<String, Integer>> sortedDirectors = new ArrayList<>(directorPreferences.entrySet());
        sortedDirectors.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Add movies from top 3 directors
        List<String> topDirectors = new ArrayList<>();
        int directorCount = 0;
        for (Map.Entry<String, Integer> directorEntry : sortedDirectors) {
            if (directorEntry.getValue() <= 0 || directorCount >= 3) {
                break;
            }

            topDirectors.add(directorEntry.getKey());
            directorCount++;
        }

        if (!topDirectors.isEmpty()) {
            List<Movie> directorMovies = repository.getMoviesByDirectors(topDirectors, 5);
            candidates.addAll(directorMovies);
        }

        return candidates;
    }

    private List<Movie> filterAlreadyInteractedMovies(List<Movie> candidates,
                                                      List<Movie> favoriteMovies,
                                                      List<Movie> watchedMovies,
                                                      List<Movie> dislikedMovies) {
        // Create a set of movie IDs the user has already interacted with
        Set<String> interactedIds = new HashSet<>();

        for (Movie movie : favoriteMovies) {
            interactedIds.add(movie.getId());
        }

        for (Movie movie : watchedMovies) {
            interactedIds.add(movie.getId());
        }

        for (Movie movie : dislikedMovies) {
            interactedIds.add(movie.getId());
        }

        // Filter out movies user has already interacted with
        List<Movie> filteredCandidates = new ArrayList<>();
        for (Movie movie : candidates) {
            if (!interactedIds.contains(movie.getId())) {
                filteredCandidates.add(movie);
            }
        }

        return filteredCandidates;
    }

    private List<Movie> rankRecommendations(List<Movie> candidates,
                                            Map<String, Integer> genrePreferences,
                                            Map<String, Integer> directorPreferences) {
        // Calculate score for each candidate movie
        Map<Movie, Double> movieScores = new HashMap<>();

        for (Movie movie : candidates) {
            double score = 0.0;

            // Add genre score
            for (String genre : movie.getGenresList()) {
                score += genrePreferences.getOrDefault(genre, 0);
            }

            // Add director score
            score += directorPreferences.getOrDefault(movie.getDirector(), 0) * 1.5;

            // Add vote average (normalized)
            score += movie.getVoteAverage() / 2;

            // Store score
            movieScores.put(movie, score);
        }

        // Sort movies by score
        List<Movie> sortedRecommendations = new ArrayList<>(candidates);
        sortedRecommendations.sort((m1, m2) -> Double.compare(movieScores.getOrDefault(m2, 0.0),
                movieScores.getOrDefault(m1, 0.0)));

        return sortedRecommendations;
    }
}
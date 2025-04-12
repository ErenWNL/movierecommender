package com.example.movierecommender.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movierecommender.R;
import com.example.movierecommender.data.local.entity.Movie;
import com.example.movierecommender.ui.detail.MovieDetailActivity;
import com.example.movierecommender.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private final Context context;
    private List<Movie> movies = new ArrayList<>();
    private final OnMovieActionListener actionListener;

    public interface OnMovieActionListener {
        void onFavoriteClicked(Movie movie);
        void onWatchedClicked(Movie movie);
        void onDislikeClicked(Movie movie);
        boolean isMovieFavorite(String movieId);
        boolean isMovieWatched(String movieId);
        boolean isMovieDisliked(String movieId);
    }

    public MovieAdapter(Context context, OnMovieActionListener actionListener) {
        this.context = context;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);

        holder.titleTextView.setText(movie.getTitle());
        holder.yearTextView.setText(String.valueOf(movie.getReleaseYear()));

        // Set rating
        String rating = String.format("%.1f", movie.getVoteAverage());
        holder.ratingTextView.setText(rating);

        // Load poster image
        if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) {
            Glide.with(context)
                    .load(movie.getPosterPath())
                    .placeholder(R.drawable.poster_placeholder)
                    .error(R.drawable.poster_placeholder)
                    .into(holder.posterImageView);
        } else {
            holder.posterImageView.setImageResource(R.drawable.poster_placeholder);
        }

        // Set genres
        List<String> genres = movie.getGenresList();
        if (!genres.isEmpty()) {
            holder.genresTextView.setText(genres.get(0)); // Just display the first genre
        } else {
            holder.genresTextView.setText("");
        }

        // Update action button states
        updateActionButtonStates(holder, movie);

        // Set click listeners
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra(Constants.EXTRA_MOVIE_ID, movie.getId());
            context.startActivity(intent);
        });

        holder.favoriteButton.setOnClickListener(v -> {
            actionListener.onFavoriteClicked(movie);
            updateActionButtonStates(holder, movie);
        });

        holder.watchedButton.setOnClickListener(v -> {
            actionListener.onWatchedClicked(movie);
            updateActionButtonStates(holder, movie);
        });

        holder.dislikeButton.setOnClickListener(v -> {
            actionListener.onDislikeClicked(movie);
            updateActionButtonStates(holder, movie);
        });
    }

    private void updateActionButtonStates(MovieViewHolder holder, Movie movie) {
        // Set favorite button state
        if (actionListener.isMovieFavorite(movie.getId())) {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_border);
        }

        // Set watched button state
        if (actionListener.isMovieWatched(movie.getId())) {
            holder.watchedButton.setImageResource(R.drawable.ic_watched_filled);
        } else {
            holder.watchedButton.setImageResource(R.drawable.ic_watched_border);
        }

        // Set dislike button state
        if (actionListener.isMovieDisliked(movie.getId())) {
            holder.dislikeButton.setImageResource(R.drawable.ic_thumb_down_filled);
        } else {
            holder.dislikeButton.setImageResource(R.drawable.ic_thumb_down_border);
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView posterImageView;
        TextView titleTextView;
        TextView yearTextView;
        TextView ratingTextView;
        TextView genresTextView;
        ImageView favoriteButton;
        ImageView watchedButton;
        ImageView dislikeButton;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.movie_card);
            posterImageView = itemView.findViewById(R.id.movie_poster);
            titleTextView = itemView.findViewById(R.id.movie_title);
            yearTextView = itemView.findViewById(R.id.movie_year);
            ratingTextView = itemView.findViewById(R.id.movie_rating);
            genresTextView = itemView.findViewById(R.id.movie_genre);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
            watchedButton = itemView.findViewById(R.id.watched_button);
            dislikeButton = itemView.findViewById(R.id.dislike_button);
        }
    }
}
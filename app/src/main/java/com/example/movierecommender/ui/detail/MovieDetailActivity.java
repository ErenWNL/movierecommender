package com.example.movierecommender.ui.detail;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movierecommender.R;
import com.example.movierecommender.data.local.entity.Movie;
import com.example.movierecommender.util.Constants;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity {
    private MovieDetailViewModel viewModel;
    private ImageView backdropImageView;
    private ImageView posterImageView;
    private TextView overviewTextView;
    private ChipGroup genreChipGroup;
    private TextView directorTextView;
    private RecyclerView castRecyclerView;
    private TextView yearTextView;
    private TextView runtimeTextView;
    private TextView ratingValueTextView;
    private RatingBar ratingBar;
    private FloatingActionButton fabFavorite;
    private FloatingActionButton fabWatched;
    private FloatingActionButton fabDislike;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Get movie ID from intent
        String movieId = getIntent().getStringExtra(Constants.EXTRA_MOVIE_ID);
        if (movieId == null) {
            Toast.makeText(this, "Movie not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(MovieDetailViewModel.class);
        viewModel.setMovieId(movieId);

        // Initialize views
        initViews();

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Observe movie data changes
        viewModel.getMovie().observe(this, this::populateUI);

        // Set up action buttons
        setupActionButtons();
    }

    private void initViews() {
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        backdropImageView = findViewById(R.id.backdrop_image);
        posterImageView = findViewById(R.id.movie_poster);
        overviewTextView = findViewById(R.id.movie_overview);
        genreChipGroup = findViewById(R.id.genre_chips);
        directorTextView = findViewById(R.id.movie_director);
        castRecyclerView = findViewById(R.id.cast_recycler_view);
        yearTextView = findViewById(R.id.movie_year);
        runtimeTextView = findViewById(R.id.movie_runtime);
        ratingBar = findViewById(R.id.rating_bar);
        ratingValueTextView = findViewById(R.id.rating_value);
        fabFavorite = findViewById(R.id.fab_favorite);
        fabWatched = findViewById(R.id.fab_watched);
        fabDislike = findViewById(R.id.fab_dislike);

        // Setup RecyclerView for cast
        castRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void populateUI(Movie movie) {
        if (movie == null) return;

        // Set title
        collapsingToolbarLayout.setTitle(movie.getTitle());

        // Load backdrop image
        if (movie.getBackdropPath() != null && !movie.getBackdropPath().isEmpty()) {
            Glide.with(this)
                    .load(movie.getBackdropPath())
                    .placeholder(R.drawable.backdrop_placeholder)
                    .error(R.drawable.backdrop_placeholder)
                    .into(backdropImageView);
        }

        // Load poster image
        if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) {
            Glide.with(this)
                    .load(movie.getPosterPath())
                    .placeholder(R.drawable.poster_placeholder)
                    .error(R.drawable.poster_placeholder)
                    .into(posterImageView);
        }

        // Set text fields
        overviewTextView.setText(movie.getOverview());
        directorTextView.setText(movie.getDirector());
        yearTextView.setText(String.valueOf(movie.getReleaseYear()));

        // Set runtime in hours and minutes format
        int hours = movie.getRuntime() / 60;
        int minutes = movie.getRuntime() % 60;
        String runtimeText = hours > 0 ?
                hours + "h " + minutes + "m" :
                minutes + "m";
        runtimeTextView.setText(runtimeText);

        // Set rating
        float rating = (float) (movie.getVoteAverage() / 2.0); // Convert 10-scale to 5-scale
        ratingBar.setRating(rating);
        ratingValueTextView.setText(String.format("%.1f", movie.getVoteAverage()));

        // Set up genre chips
        setupGenreChips(movie.getGenres());

        // Set up cast recycler view
        setupCastRecyclerView(movie.getCast());

        // Update action button states
        updateActionButtonStates();
    }

    private void setupGenreChips(String genreString) {
        genreChipGroup.removeAllViews();

        if (genreString != null && !genreString.isEmpty()) {
            String[] genres = genreString.split(",");

            for (String genre : genres) {
                String trimmedGenre = genre.trim();
                if (!trimmedGenre.isEmpty()) {
                    Chip chip = new Chip(this);
                    chip.setText(trimmedGenre);
                    chip.setChipBackgroundColorResource(R.color.chip_background);
                    chip.setTextColor(getResources().getColor(R.color.chip_text, getTheme()));
                    chip.setChipMinHeightResource(R.dimen.chip_min_height);
                    chip.setTextSize(12);
                    genreChipGroup.addView(chip);
                }
            }
        }
    }

    private void setupCastRecyclerView(String castString) {
        // Temporary solution: Add a TextView instead of using RecyclerView
        if (castString == null || castString.isEmpty()) return;

        // Find the parent view (likely the LinearLayout containing the RecyclerView)
        ViewGroup parentView = (ViewGroup) castRecyclerView.getParent();

        // Create a TextView for the cast
        TextView castTextView = new TextView(this);
        castTextView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        castTextView.setText(castString.replace(",", ", "));
        castTextView.setTextColor(getResources().getColor(R.color.primary_text, getTheme()));

        // Add the TextView where the RecyclerView would be
        int index = parentView.indexOfChild(castRecyclerView);
        parentView.removeView(castRecyclerView);
        parentView.addView(castTextView, index);
    }

    private void setupActionButtons() {
        fabFavorite.setOnClickListener(v -> {
            if (viewModel.isMovieFavorite()) {
                viewModel.removeFromFavorites();
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.markAsFavorite();
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            }
            updateActionButtonStates();
        });

        fabWatched.setOnClickListener(v -> {
            if (viewModel.isMovieWatched()) {
                viewModel.removeFromWatched();
                Toast.makeText(this, "Removed from watched", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.markAsWatched(ratingBar.getRating());
                Toast.makeText(this, "Added to watched", Toast.LENGTH_SHORT).show();
            }
            updateActionButtonStates();
        });

        fabDislike.setOnClickListener(v -> {
            if (viewModel.isMovieDisliked()) {
                viewModel.removeFromDisliked();
                Toast.makeText(this, "Removed from disliked", Toast.LENGTH_SHORT).show();
            } else {
                viewModel.markAsDisliked();
                Toast.makeText(this, "Added to disliked", Toast.LENGTH_SHORT).show();
            }
            updateActionButtonStates();
        });
    }

    private void updateActionButtonStates() {
        // Update favorite button
        if (viewModel.isMovieFavorite()) {
            fabFavorite.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            fabFavorite.setImageResource(R.drawable.ic_favorite_border);
        }

        // Update watched button
        if (viewModel.isMovieWatched()) {
            fabWatched.setImageResource(R.drawable.ic_watched_filled);
        } else {
            fabWatched.setImageResource(R.drawable.ic_watched_border);
        }

        // Update dislike button
        if (viewModel.isMovieDisliked()) {
            fabDislike.setImageResource(R.drawable.ic_thumb_down_filled);
        } else {
            fabDislike.setImageResource(R.drawable.ic_thumb_down_border);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Simple data class for cast members
    static class CastMember {
        private final String name;
        private final String character;
        private final String photoUrl;

        CastMember(String name, String character, String photoUrl) {
            this.name = name;
            this.character = character;
            this.photoUrl = photoUrl;
        }

        public String getName() { return name; }
        public String getCharacter() { return character; }
        public String getPhotoUrl() { return photoUrl; }
    }
}
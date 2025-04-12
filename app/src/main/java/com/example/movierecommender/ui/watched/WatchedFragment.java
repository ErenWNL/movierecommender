package com.example.movierecommender.ui.watched;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movierecommender.R;
import com.example.movierecommender.data.local.entity.Movie;
import com.example.movierecommender.ui.adapter.MovieAdapter;
import com.example.movierecommender.ui.main.MainViewModel;
import com.google.android.material.button.MaterialButton;

public class WatchedFragment extends Fragment implements MovieAdapter.OnMovieActionListener {
    private MainViewModel viewModel;
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private TextView emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_watched, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyView = view.findViewById(R.id.empty_view);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        adapter = new MovieAdapter(requireContext(), this);
        recyclerView.setAdapter(adapter);

        // Get ViewModel from the MainActivity
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Observe watched movies
        viewModel.getWatchedMovies().observe(getViewLifecycleOwner(), movies -> {
            adapter.setMovies(movies);

            // Show empty view if no watched movies
            if (movies == null || movies.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onFavoriteClicked(Movie movie) {
        if (isMovieFavorite(movie.getId())) {
            viewModel.removeFromFavorites(movie.getId());
            Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.markAsFavorite(movie.getId());
            Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWatchedClicked(Movie movie) {
        if (isMovieWatched(movie.getId())) {
            viewModel.removeFromWatched(movie.getId());
            Toast.makeText(requireContext(), "Removed from watched", Toast.LENGTH_SHORT).show();
        } else {
            showRatingDialog(movie);
        }
    }

    @Override
    public void onDislikeClicked(Movie movie) {
        if (isMovieDisliked(movie.getId())) {
            viewModel.removeFromDisliked(movie.getId());
            Toast.makeText(requireContext(), "Removed from disliked", Toast.LENGTH_SHORT).show();
        } else {
            viewModel.markAsDisliked(movie.getId());
            Toast.makeText(requireContext(), "Added to disliked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean isMovieFavorite(String movieId) {
        return viewModel.isMovieFavorite(movieId);
    }

    @Override
    public boolean isMovieWatched(String movieId) {
        return viewModel.isMovieWatched(movieId);
    }

    @Override
    public boolean isMovieDisliked(String movieId) {
        return viewModel.isMovieDisliked(movieId);
    }

    private void showRatingDialog(Movie movie) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_rating);

        TextView titleTextView = dialog.findViewById(R.id.dialog_title);
        RatingBar ratingBar = dialog.findViewById(R.id.rating_bar);
        MaterialButton cancelButton = dialog.findViewById(R.id.cancel_button);
        MaterialButton submitButton = dialog.findViewById(R.id.submit_button);

        titleTextView.setText(getString(R.string.rate_movie, movie.getTitle()));

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            viewModel.markAsWatched(movie.getId(), rating);
            Toast.makeText(requireContext(), "Added to watched", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }
}
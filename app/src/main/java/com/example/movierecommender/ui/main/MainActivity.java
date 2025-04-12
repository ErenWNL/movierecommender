package com.example.movierecommender.ui.main;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.movierecommender.R;
import com.example.movierecommender.ui.favorites.FavoritesFragment;
import com.example.movierecommender.ui.recommendations.RecommendationsFragment;
import com.example.movierecommender.ui.watched.WatchedFragment;
import com.example.movierecommender.util.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private MainViewModel viewModel;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Set up bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Default fragment
        if (savedInstanceState == null) {
            loadFragment(new RecommendationsFragment(), Constants.TAG_RECOMMENDATIONS);
            bottomNavigationView.setSelectedItemId(R.id.nav_recommendations);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        String tag = null;

        int itemId = item.getItemId();
        if (itemId == R.id.nav_recommendations) {
            fragment = new RecommendationsFragment();
            tag = Constants.TAG_RECOMMENDATIONS;
        } else if (itemId == R.id.nav_watched) {
            fragment = new WatchedFragment();
            tag = Constants.TAG_WATCHED;
        } else if (itemId == R.id.nav_favorites) {
            fragment = new FavoritesFragment();
            tag = Constants.TAG_FAVORITES;
        }

        return loadFragment(fragment, tag);
    }

    private boolean loadFragment(Fragment fragment, String tag) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, tag)
                    .commit();
            return true;
        }
        return false;
    }
}
package com.example.movierecommender.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.movierecommender.data.local.entity.Movie;
import com.example.movierecommender.data.local.entity.UserMovie;
import com.example.movierecommender.util.DataLoader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Movie.class, UserMovie.class}, version = 1, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    public abstract MovieDao movieDao();

    private static volatile MovieDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static MovieDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MovieDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    MovieDatabase.class, "movie_database")
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    databaseWriteExecutor.execute(() -> {
                                        // Load initial data from movies.json
                                        DataLoader dataLoader = new DataLoader(context);
                                        MovieDao dao = INSTANCE.movieDao();
                                        dataLoader.loadInitialData(dao);
                                    });
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
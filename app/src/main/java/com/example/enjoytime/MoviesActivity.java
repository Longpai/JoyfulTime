package com.example.enjoytime;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoviesActivity extends AppCompatActivity implements MoviesAdapter.OnMovieClickListener {

    private MoviesAdapter adapter;
    private List<Movie> allMovies;
    private Map<String, String> movieVideoUrls = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        RecyclerView recyclerView = findViewById(R.id.movies_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        allMovies = new ArrayList<>();
        allMovies.add(new Movie("夏洛特烦恼", R.drawable.charlotte_poster));
        allMovies.add(new Movie("阿凡达", R.drawable.avatar_poster));
        allMovies.add(new Movie("举起手来", R.drawable.raise_hand_poster));

        // Assign different videos to each movie
        movieVideoUrls.put("夏洛特烦恼", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");
        movieVideoUrls.put("阿凡达", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");
        movieVideoUrls.put("举起手来", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4");

        adapter = new MoviesAdapter(new ArrayList<>(allMovies), this);
        recyclerView.setAdapter(adapter);

        SearchView searchView = findViewById(R.id.movie_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String text) {
        List<Movie> filteredList = new ArrayList<>();
        for (Movie movie : allMovies) {
            if (movie.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(movie);
            }
        }
        adapter.filterList(filteredList);
    }

    @Override
    public void onMovieClick(String movieTitle) {
        Intent intent = new Intent(this, VideoPlayerActivity.class);
        String videoUrl = movieVideoUrls.getOrDefault(movieTitle, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
        intent.putExtra(VideoPlayerActivity.VIDEO_URL, videoUrl);
        startActivity(intent);
    }
}

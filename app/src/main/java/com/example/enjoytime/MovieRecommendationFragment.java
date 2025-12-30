package com.example.enjoytime;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MovieRecommendationFragment extends Fragment implements MoviesAdapter.OnMovieClickListener {

    // Google's public test video
    private static final String BIG_BUCK_BUNNY_URL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_recommendation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.movie_recommendation_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie("夏洛特烦恼", R.drawable.charlotte_poster));
        movies.add(new Movie("阿凡达", R.drawable.avatar_poster));
        movies.add(new Movie("举起手来", R.drawable.raise_hand_poster));

        MoviesAdapter moviesAdapter = new MoviesAdapter(movies, this);
        recyclerView.setAdapter(moviesAdapter);
    }

    @Override
    public void onMovieClick(String movieTitle) {
        Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
        intent.putExtra(VideoPlayerActivity.VIDEO_URL, BIG_BUCK_BUNNY_URL);
        startActivity(intent);
    }
}

package com.example.enjoytime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private final OnMovieClickListener onMovieClickListener;

    public interface OnMovieClickListener {
        void onMovieClick(String movieTitle);
    }

    public MoviesAdapter(List<Movie> movies, OnMovieClickListener onMovieClickListener) {
        this.movies = movies;
        this.onMovieClickListener = onMovieClickListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie, onMovieClickListener);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<Movie> filteredList) {
        movies = filteredList;
        notifyDataSetChanged();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ImageView posterImageView;
        private final TextView titleTextView;
        private final ImageButton likeButton;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.movie_poster_image_view);
            titleTextView = itemView.findViewById(R.id.movie_title_text_view);
            likeButton = itemView.findViewById(R.id.like_button);
        }

        public void bind(final Movie movie, final OnMovieClickListener onMovieClickListener) {
            titleTextView.setText(movie.getTitle());
            posterImageView.setImageResource(movie.getPosterResId());
            itemView.setOnClickListener(v -> onMovieClickListener.onMovieClick(movie.getTitle()));

            likeButton.setOnClickListener(v -> {
                // Handle like button click
                SharedPreferences preferences = itemView.getContext().getSharedPreferences("user_likes", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(movie.getTitle(), true);
                editor.apply();

                Toast.makeText(itemView.getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
            });
        }
    }
}

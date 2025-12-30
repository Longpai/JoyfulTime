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

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SeriesViewHolder> {

    private List<Series> series;
    private final OnSeriesClickListener onSeriesClickListener;

    public interface OnSeriesClickListener {
        void onSeriesClick(String seriesTitle);
    }

    public SeriesAdapter(List<Series> series, OnSeriesClickListener onSeriesClickListener) {
        this.series = series;
        this.onSeriesClickListener = onSeriesClickListener;
    }

    @NonNull
    @Override
    public SeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_series, parent, false);
        return new SeriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesViewHolder holder, int position) {
        Series seriesItem = series.get(position);
        holder.bind(seriesItem, onSeriesClickListener);
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<Series> filteredList) {
        series = filteredList;
        notifyDataSetChanged();
    }

    static class SeriesViewHolder extends RecyclerView.ViewHolder {
        private final ImageView posterImageView;
        private final TextView titleTextView;
        private final ImageButton likeButton;

        public SeriesViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.series_poster_image_view);
            titleTextView = itemView.findViewById(R.id.series_title_text_view);
            likeButton = itemView.findViewById(R.id.like_button);
        }

        public void bind(final Series seriesItem, final OnSeriesClickListener onSeriesClickListener) {
            titleTextView.setText(seriesItem.getTitle());
            posterImageView.setImageResource(seriesItem.getPosterResId());
            itemView.setOnClickListener(v -> onSeriesClickListener.onSeriesClick(seriesItem.getTitle()));

            likeButton.setOnClickListener(v -> {
                // Handle like button click
                SharedPreferences preferences = itemView.getContext().getSharedPreferences("user_likes", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(seriesItem.getTitle(), true);
                editor.apply();

                Toast.makeText(itemView.getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
            });
        }
    }
}

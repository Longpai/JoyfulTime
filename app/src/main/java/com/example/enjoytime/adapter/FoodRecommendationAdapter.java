package com.example.enjoytime.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enjoytime.R;
import com.example.enjoytime.databinding.ItemFoodRecommendationBinding;
import com.example.enjoytime.model.FoodRecommendation;

/**
 * 美食推荐列表Adapter
 */
public class FoodRecommendationAdapter extends ListAdapter<FoodRecommendation, FoodRecommendationAdapter.FoodViewHolder> {

    public FoodRecommendationAdapter() {
        super(new DiffUtil.ItemCallback<FoodRecommendation>() {
            @Override
            public boolean areItemsTheSame(@NonNull FoodRecommendation oldItem, @NonNull FoodRecommendation newItem) {
                return oldItem.getName().equals(newItem.getName());
            }

            @Override
            public boolean areContentsTheSame(@NonNull FoodRecommendation oldItem, @NonNull FoodRecommendation newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodRecommendationBinding binding = ItemFoodRecommendationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodRecommendation food = getItem(position);
        holder.bind(food);
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        private final ItemFoodRecommendationBinding binding;

        public FoodViewHolder(ItemFoodRecommendationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FoodRecommendation food) {
            binding.foodName.setText(food.getName());
            binding.foodCuisine.setText("菜系: " + food.getCuisine());
            binding.foodRating.setText(String.format("评分: %.1f (%d评)", food.getRating(), food.getReviewCount()));
            binding.foodAddress.setText("地址: " + food.getAddress());
            binding.foodPhone.setText("电话: " + food.getPhone());
            binding.foodDistance.setText(String.format("距离: %.0fm", food.getDistance()));
        }
    }
}

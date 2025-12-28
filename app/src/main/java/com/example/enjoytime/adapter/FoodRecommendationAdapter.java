package com.example.enjoytime.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

    private OnItemClickListener onItemClickListener;

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

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodRecommendationBinding binding = ItemFoodRecommendationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodViewHolder(binding, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodRecommendation food = getItem(position);
        holder.bind(food);
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        private final ItemFoodRecommendationBinding binding;
        private OnItemClickListener onItemClickListener;

        public FoodViewHolder(ItemFoodRecommendationBinding binding, OnItemClickListener onItemClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.onItemClickListener = onItemClickListener;
        }

        public void bind(FoodRecommendation food) {
            binding.foodName.setText(food.getName());
            binding.foodCuisine.setText("菜系: " + food.getCuisine());
            binding.foodRating.setText(String.format("评分: %.1f (%d评)", food.getRating(), food.getReviewCount()));
            binding.foodAddress.setText("地址: " + food.getAddress());
            binding.foodPhone.setText("电话: " + food.getPhone());
            binding.foodDistance.setText(String.format("距离: %.0fm", food.getDistance()));

            // 设置点击事件
            binding.getRoot().setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(food);
                }
                // 尝试导航到该位置
                navigateToFood(food, binding.getRoot().getContext());
            });
        }

        private void navigateToFood(FoodRecommendation food, android.content.Context context) {
            String phone = food.getPhone();
            String address = food.getAddress();
            
            // 显示操作选项
            new android.app.AlertDialog.Builder(context)
                    .setTitle(food.getName())
                    .setItems(new String[]{"调用地图导航", "拨打电话", "取消"}, (dialog, which) -> {
                        if (which == 0) {
                            // 调用地图导航
                            navigateWithMap(context, food.getAddress(), food.getName());
                        } else if (which == 1) {
                            // 拨打电话
                            if (phone != null && !phone.equals("未提供")) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + phone));
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, "暂无电话信息", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .show();
        }

        private void navigateWithMap(android.content.Context context, String address, String title) {
            // 使用高德地图或Google地图进行导航
            try {
                // 尝试调用高德地图
                Uri uri = Uri.parse("amapuri://route/plan?sname=我的位置&dname=" + title + "&destination=" + address);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            } catch (Exception e1) {
                try {
                    // 备用方案：使用Google Maps
                    Uri uri = Uri.parse("https://maps.google.com/?q=" + address);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                } catch (Exception e2) {
                    Toast.makeText(context, "无可用地图应用", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(FoodRecommendation food);
    }
}

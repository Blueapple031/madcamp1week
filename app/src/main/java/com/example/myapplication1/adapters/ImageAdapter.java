package com.example.myapplication1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication1.R;



public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final int[] imageIds;
    private final Context context;

    public ImageAdapter(Context context, int[] imageIds) {
        this.context = context;
        this.imageIds = imageIds;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // Glide로 이미지 로드
        Glide.with(context)
                .load(imageIds[position]) // 리소스 ID로 이미지 로드
                .placeholder(R.drawable.image6) // 로드 중 보여줄 기본 이미지
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageIds.length;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

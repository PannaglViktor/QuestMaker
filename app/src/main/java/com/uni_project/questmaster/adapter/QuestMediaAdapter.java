package com.uni_project.questmaster.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uni_project.questmaster.R;

import java.util.List;

public class QuestMediaAdapter extends RecyclerView.Adapter<QuestMediaAdapter.ImageViewHolder> {

    private final Context context;
    private final List<Uri> mediaUris;
    private final boolean isEditable;
    private final OnImageDeleteListener onImageDeleteListener;

    public interface OnImageDeleteListener {
        void onImageDelete(int position);
    }

    public QuestMediaAdapter(Context context, List<Uri> mediaUris, OnImageDeleteListener onImageDeleteListener, boolean isEditable) {
        this.context = context;
        this.mediaUris = mediaUris;
        this.onImageDeleteListener = onImageDeleteListener;
        this.isEditable = isEditable;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_slider, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri mediaUri = mediaUris.get(position);
        Glide.with(context).load(mediaUri).into(holder.imageView);

        if (isEditable) {
            holder.buttonDeleteImage.setVisibility(View.VISIBLE);
            holder.buttonDeleteImage.setOnClickListener(v -> {
                if (onImageDeleteListener != null) {
                    onImageDeleteListener.onImageDelete(position);
                }
            });
        } else {
            holder.buttonDeleteImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mediaUris.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton buttonDeleteImage;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            buttonDeleteImage = itemView.findViewById(R.id.button_delete_image);
        }
    }
}

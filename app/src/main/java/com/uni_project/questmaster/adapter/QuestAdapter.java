package com.uni_project.questmaster.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.model.Quest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestViewHolder> {

    private final Context context;
    private final List<Quest> questList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());

    public QuestAdapter(Context context, List<Quest> questList) {
        this.context = context;
        this.questList = questList;
    }

    @NonNull
    @Override
    public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_quest, parent, false);
        return new QuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestViewHolder holder, int position) {
        Quest quest = questList.get(position);
        holder.bind(quest, context);
    }

    @Override
    public int getItemCount() {
        return questList.size();
    }

    public void setQuests(List<Quest> newQuestList) {
        this.questList.clear();
        this.questList.addAll(newQuestList);
        notifyDataSetChanged();
    }

    public static class QuestViewHolder extends RecyclerView.ViewHolder {
        private final TextView usernameTextView;
        private final TextView dateTextView;
        private final TextView descriptionTextView;
        private final ViewPager2 questImagesViewPager;
        private final TabLayout tabIndicator;

        public QuestViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            questImagesViewPager = itemView.findViewById(R.id.questImagesViewPager);
            tabIndicator = itemView.findViewById(R.id.tabIndicator);
        }

        public void bind(Quest quest, Context context) {
            usernameTextView.setText(quest.getOwnerName());
            descriptionTextView.setText(quest.getDescription());

            if (quest.getTimestamp() != null) {
                dateTextView.setText(new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(quest.getTimestamp()));
            } else {
                dateTextView.setText("");
            }

            List<String> allMedia = new ArrayList<>();
            if (quest.getImageUrls() != null) {
                allMedia.addAll(quest.getImageUrls());
            }

            String mapUrl = null;
            if (quest.getLocation() != null) {
                mapUrl = "https://maps.googleapis.com/maps/api/staticmap?center=" +
                        quest.getLocation().getLatitude() + "," + quest.getLocation().getLongitude() +
                        "&zoom=15&size=600x300&maptype=roadmap&markers=color:red%7Clabel:S%7C" +
                        quest.getLocation().getLatitude() + "," + quest.getLocation().getLongitude() +
                        "&key=YOUR_STATIC_API_KEY"; 
            } else if (quest.getStartPoint() != null && quest.getEndPoint() != null) {
                mapUrl = "https://maps.googleapis.com/maps/api/staticmap?size=600x300&maptype=roadmap" +
                        "&markers=color:blue%7Clabel:S%7C" + quest.getStartPoint().getLatitude() + "," + quest.getStartPoint().getLongitude() +
                        "&markers=color:green%7Clabel:E%7C" + quest.getEndPoint().getLatitude() + "," + quest.getEndPoint().getLongitude() +
                        "&key=YOUR_STATIC_API_KEY"; 
            }

            if (mapUrl != null) {
                allMedia.add(mapUrl);
            }

            if (!allMedia.isEmpty()) {
                questImagesViewPager.setVisibility(View.VISIBLE);
                tabIndicator.setVisibility(View.VISIBLE);
                ImageSliderAdapter imageAdapter = new ImageSliderAdapter(allMedia);
                questImagesViewPager.setAdapter(imageAdapter);

                new TabLayoutMediator(tabIndicator, questImagesViewPager, (tab, position) -> {})
                .attach();
            } else {
                questImagesViewPager.setVisibility(View.GONE);
                tabIndicator.setVisibility(View.GONE);
            }
        }
    }

    public static class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {

        private final List<String> mediaUrls;

        public ImageSliderAdapter(List<String> mediaUrls) {
            this.mediaUrls = mediaUrls;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            String mediaUrl = mediaUrls.get(position);
            Glide.with(holder.imageView.getContext())
                    .load(mediaUrl)
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return mediaUrls.size();
        }

        public static class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.sliderImageView);
            }
        }
    }
}

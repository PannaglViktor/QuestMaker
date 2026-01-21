package com.uni_project.questmaster.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.model.Quest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestViewHolder> {

    private final Context context;
    private final List<Quest> questList;
    private final FirebaseFirestore db;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
    private final OnQuestClickListener onQuestClickListener;

    public interface OnQuestClickListener {
        void onQuestClick(Quest quest);
    }

    public QuestAdapter(Context context, List<Quest> questList, OnQuestClickListener onQuestClickListener) {
        this.context = context;
        this.questList = questList;
        this.db = FirebaseFirestore.getInstance();
        this.onQuestClickListener = onQuestClickListener;
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
        holder.bind(quest, context, db);
        holder.itemView.setOnClickListener(v -> {
            if (onQuestClickListener != null) {
                onQuestClickListener.onQuestClick(quest);
            }
        });
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
        private final TextView questNameTextView;
        private final TextView usernameTextView;
        private final TextView dateTextView;
        private final TextView descriptionTextView;
        private final ViewPager2 questImagesViewPager;
        private final TabLayout tabIndicator;

        public QuestViewHolder(@NonNull View itemView) {
            super(itemView);
            questNameTextView = itemView.findViewById(R.id.questNameTextView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            questImagesViewPager = itemView.findViewById(R.id.questImagesViewPager);
            tabIndicator = itemView.findViewById(R.id.tabIndicator);
        }

        public void bind(Quest quest, Context context, FirebaseFirestore db) {
            questNameTextView.setText(quest.getTitle());
            usernameTextView.setText(quest.getOwnerName());
            descriptionTextView.setText(quest.getDescription());

            if (quest.getOwnerId() != null && !quest.getOwnerId().isEmpty()) {
                db.collection("users").document(quest.getOwnerId()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String username = documentSnapshot.getString("username");
                                usernameTextView.setText(username);
                            } else {
                                Log.w("QuestAdapter", "User not found with ID: " + quest.getOwnerId());
                                usernameTextView.setText(quest.getOwnerName());
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("QuestAdapter", "Error fetching user", e);
                            usernameTextView.setText(quest.getOwnerName());
                        });
            }


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
                        "&key=AIzaSyBDUhzGs292Fad_UxWXTDrXWmCaeD2BZpY";
            } else if (quest.getStartPoint() != null && quest.getEndPoint() != null) {
                mapUrl = "https://maps.googleapis.com/maps/api/staticmap?size=600x300&maptype=roadmap" +
                        "&markers=color:blue%7Clabel:S%7C" + quest.getStartPoint().getLatitude() + "," + quest.getStartPoint().getLongitude() +
                        "&markers=color:green%7Clabel:E%7C" + quest.getEndPoint().getLatitude() + "," + quest.getEndPoint().getLongitude() +
                        "&key=AIzaSyBDUhzGs292Fad_UxWXTDrXWmCaeD2BZpY";
            }

            if (mapUrl != null) {
                allMedia.add(mapUrl);
            }

            if (!allMedia.isEmpty()) {
                questImagesViewPager.setVisibility(View.VISIBLE);
                tabIndicator.setVisibility(View.VISIBLE);
                ImageSliderAdapter imageAdapter = new ImageSliderAdapter(allMedia, quest, context);
                questImagesViewPager.setAdapter(imageAdapter);

                new TabLayoutMediator(tabIndicator, questImagesViewPager, (tab, position) -> {}).attach();
            } else {
                questImagesViewPager.setVisibility(View.GONE);
                tabIndicator.setVisibility(View.GONE);
            }
        }
    }

    public static class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {

        private final List<String> mediaUrls;
        private final Quest quest;
        private final Context context;

        public ImageSliderAdapter(List<String> mediaUrls, Quest quest, Context context) {
            this.mediaUrls = mediaUrls;
            this.quest = quest;
            this.context = context;
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

            if (mediaUrl.startsWith("https://maps.googleapis.com/maps/api/staticmap")) {
                holder.imageView.setOnClickListener(v -> {
                    if (quest.getLocation() != null) {
                        Uri gmmIntentUri = Uri.parse("geo:" + quest.getLocation().getLatitude() + "," + quest.getLocation().getLongitude() + "?q=" + quest.getLocation().getLatitude() + "," + quest.getLocation().getLongitude() + "(Quest Location)");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(mapIntent);
                        }
                    } else if (quest.getStartPoint() != null && quest.getEndPoint() != null) {
                        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" +
                                quest.getStartPoint().getLatitude() + "," + quest.getStartPoint().getLongitude() +
                                "&destination=" + quest.getEndPoint().getLatitude() + "," + quest.getEndPoint().getLongitude());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(mapIntent);
                        }
                    }
                });
            } else {
                holder.imageView.setOnClickListener(null);
            }

        }

        @Override
        public int getItemCount() {
            return mediaUrls.size();
        }

        public static class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.image_view);
            }
        }
    }
}

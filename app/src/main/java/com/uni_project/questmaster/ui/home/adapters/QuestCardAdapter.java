package com.uni_project.questmaster.ui.home.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.model.QuestCard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuestCardAdapter extends RecyclerView.Adapter<QuestCardAdapter.QuestViewHolder> {

    private List<QuestCard> questList = new ArrayList<>();

    @NonNull
    @Override
    public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_quest, parent, false);
        return new QuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestViewHolder holder, int position) {
        QuestCard quest = questList.get(position);
        holder.bind(quest);
    }

    @Override
    public int getItemCount() {
        return questList.size();
    }

    public void setQuests(List<QuestCard> quests) {
        this.questList = quests;
        notifyDataSetChanged(); // Use DiffUtil for better performance in a real app
    }

    static class QuestViewHolder extends RecyclerView.ViewHolder {
        private final TextView usernameTextView;
        private final TextView dateTextView;
        private final TextView descriptionTextView;
        private final ViewPager2 questImagesViewPager;
        private final TabLayout tabIndicator;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());


        public QuestViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            questImagesViewPager = itemView.findViewById(R.id.questImagesViewPager);
            tabIndicator = itemView.findViewById(R.id.tabIndicator);
        }

        public void bind(QuestCard quest) {
            usernameTextView.setText(quest.getUsername());
            descriptionTextView.setText(quest.getDescription());

            if (quest.getTimestamp() != null) {
                dateTextView.setText(dateFormat.format(quest.getTimestamp()));
            }

            // Setup ViewPager2 for images
            if (quest.getImageUrls() != null && !quest.getImageUrls().isEmpty()) {
                ImageSliderAdapter imageAdapter = new ImageSliderAdapter(quest.getImageUrls());
                questImagesViewPager.setAdapter(imageAdapter);

                // Link TabLayout to ViewPager2
                new TabLayoutMediator(tabIndicator, questImagesViewPager,
                        (tab, position) -> {} // No need to configure tabs, selector handles it
                ).attach();
                tabIndicator.setVisibility(View.VISIBLE);
            } else {
                questImagesViewPager.setVisibility(View.GONE);
                tabIndicator.setVisibility(View.GONE);
            }
        }
    }
}

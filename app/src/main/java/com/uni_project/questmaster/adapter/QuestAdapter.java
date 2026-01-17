package com.uni_project.questmaster.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.model.Quest;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestViewHolder> {

    private final Context context;
    private final List<Quest> questList;
    private final String currentUserId;

    public QuestAdapter(Context context, List<Quest> questList) {
        this.context = context;
        this.questList = questList;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
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
        holder.bind(quest, currentUserId);
    }

    @Override
    public int getItemCount() {
        return questList.size();
    }

    static class QuestViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView usernameTextView;
        private final TextView dateTextView;
        private final TextView descriptionTextView;

        public QuestViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }

        public void bind(final Quest quest, final String currentUserId) {
            usernameTextView.setText(quest.getOwnerName());
            descriptionTextView.setText(quest.getDescription());

            if (quest.getTimestamp() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
                dateTextView.setText(sdf.format(quest.getTimestamp()));
            }

            if (currentUserId != null && quest.getSavedBy() != null) {
                cardView.setChecked(quest.getSavedBy().contains(currentUserId));
            } else {
                cardView.setChecked(false);
            }

            // TODO: Set up ViewPager2 for images
            // TODO: Implement onCheckedChangeListener for the star icon to update Firestore
        }
    }
}

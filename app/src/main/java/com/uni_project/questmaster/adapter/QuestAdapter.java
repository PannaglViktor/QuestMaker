package com.uni_project.questmaster.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.model.Quest;
import com.uni_project.questmaster.model.User;

import java.util.ArrayList;
import java.util.List;

public class QuestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_QUEST = 0;
    private static final int VIEW_TYPE_USER = 1;

    private final Context context;
    private List<Object> items = new ArrayList<>();
    private OnQuestClickListener onQuestClickListener;
    private OnUserClickListener onUserClickListener;

    public interface OnQuestClickListener {
        void onQuestClick(Quest quest);
    }

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public QuestAdapter(Context context) {
        this.context = context;
    }

    public void setOnQuestClickListener(OnQuestClickListener listener) {
        this.onQuestClickListener = listener;
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.onUserClickListener = listener;
    }

    public void setData(List<Quest> quests, List<User> users) {
        items.clear();
        if (quests != null) {
            items.addAll(quests);
        }
        if (users != null) {
            items.addAll(users);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Quest) {
            return VIEW_TYPE_QUEST;
        } else {
            return VIEW_TYPE_USER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_QUEST) {
            View view = LayoutInflater.from(context).inflate(R.layout.card_quest, parent, false);
            return new QuestViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.card_user, parent, false);
            return new UserViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_QUEST) {
            Quest quest = (Quest) items.get(position);
            ((QuestViewHolder) holder).bind(quest);
            holder.itemView.setOnClickListener(v -> {
                if (onQuestClickListener != null) {
                    onQuestClickListener.onQuestClick(quest);
                }
            });
        } else {
            User user = (User) items.get(position);
            ((UserViewHolder) holder).bind(user);
            holder.itemView.setOnClickListener(v -> {
                if (onUserClickListener != null) {
                    onUserClickListener.onUserClick(user);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class QuestViewHolder extends RecyclerView.ViewHolder {
        TextView questNameTextView;
        // Add other views from card_quest.xml

        QuestViewHolder(@NonNull View itemView) {
            super(itemView);
            questNameTextView = itemView.findViewById(R.id.questNameTextView);
        }

        void bind(Quest quest) {
            questNameTextView.setText(quest.getTitle());
            // Bind other quest data
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImageView;
        TextView userNameTextView;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImageView = itemView.findViewById(R.id.user_profile_image);
            userNameTextView = itemView.findViewById(R.id.user_name);
        }

        void bind(User user) {
            userNameTextView.setText(user.getName());
            // Load user profile image with Glide
        }
    }
}

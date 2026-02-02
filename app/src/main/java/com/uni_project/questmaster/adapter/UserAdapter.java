// In C:/Users/benja/AndroidStudioProjects/QuestMaker/app/src/main/java/com/uni_project/questmaster/adapter/UserAdapter.java
package com.uni_project.questmaster.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.model.User;
import com.uni_project.questmaster.ui.utils.ProfileFragment;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Context context;
    private final List<User> userList;
    private final NavController navController;
    private final int navigationActionId;

    public UserAdapter(Context context, List<User> userList, NavController navController, int navigationActionId) {
        this.context = context;
        this.userList = userList;
        this.navController = navController;
        this.navigationActionId = navigationActionId;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.username.setText(user.getUsername());

        String imageUrl = user.getProfileImageUrl();
        if (TextUtils.isEmpty(imageUrl)) {
            imageUrl = user.getAvatarUrl();
        }
        //GLIDE
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.profileImage);

        // ON-CLICK LISTENER
        holder.itemView.setOnClickListener(v -> {
            if (user.getUid() != null && !user.getUid().isEmpty()) {
                Bundle bundle = ProfileFragment.newInstance(user.getUid()).getArguments();

                navController.navigate(navigationActionId, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // VIEWHOLDER
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView username;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ensure these IDs match your R.layout.card_user
            profileImage = itemView.findViewById(R.id.user_profile_image);
            username = itemView.findViewById(R.id.user_name);
        }
    }
}

package com.uni_project.questmaster.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.model.Comment;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private final Context context;
    private final List<Comment> commentList;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.usernameTextView.setText(comment.getUsername());
        holder.commentTextView.setText(comment.getText());

        if (comment.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.timestampTextView.setText(sdf.format(comment.getTimestamp()));
        } else {
            holder.timestampTextView.setText("now");
        }

        Glide.with(context)
                .load(comment.getAvatarUrl())
                .placeholder(R.mipmap.ic_user_icon)
                .into(holder.avatarImageView);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void updateComments(List<Comment> newComments) {
        commentList.clear();
        if (newComments != null) {
            commentList.addAll(newComments);
        }
        notifyDataSetChanged();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView avatarImageView;
        TextView usernameTextView;
        TextView commentTextView;
        TextView timestampTextView;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImageView = itemView.findViewById(R.id.image_view_user_avatar);
            usernameTextView = itemView.findViewById(R.id.text_view_user_name);
            commentTextView = itemView.findViewById(R.id.text_view_comment);
            timestampTextView = itemView.findViewById(R.id.text_view_timestamp);
        }
    }
}
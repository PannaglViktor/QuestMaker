package com.example.questmaster.ui.home.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.fragment.NavHostFragment; // Import for navigation

import com.example.questmaster.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView; // Import for ShapeableImageView
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class QuestViewFragment extends Fragment {

    // Existing views
    private ImageView imageViewQuestMedia;
    private TextView textViewQuestName, textViewQuestDescription;
    private CheckBox buttonStar;
    private ImageButton buttonShare;
    private ChipGroup chipGroupTags;
    private RecyclerView recyclerViewComments;
    private TextInputLayout textInputLayoutComment;
    private EditText editTextComment;
    private MaterialButton buttonPostComment;

    // New views for creator info
    private ShapeableImageView imageViewCreatorAvatar;
    private TextView textViewCreatorName;

    private boolean isStarred = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment layout
        return inflater.inflate(R.layout.fragment_quest_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupClickListeners();
        setupCommentsRecyclerView();
        loadQuestData();
    }

    private void initializeViews(View view) {
        // Existing views
        imageViewQuestMedia = view.findViewById(R.id.image_view_quest_media);
        textViewQuestName = view.findViewById(R.id.text_view_quest_name);
        textViewQuestDescription = view.findViewById(R.id.text_view_quest_description);
        buttonStar = view.findViewById(R.id.button_star);
        buttonShare = view.findViewById(R.id.button_share);
        chipGroupTags = view.findViewById(R.id.chip_group_tags);
        recyclerViewComments = view.findViewById(R.id.recycler_view_comments);
        textInputLayoutComment = view.findViewById(R.id.text_input_layout_comment);
        editTextComment = view.findViewById(R.id.edit_text_comment);
        buttonPostComment = view.findViewById(R.id.button_post_comment);

        // New views
        imageViewCreatorAvatar = view.findViewById(R.id.image_view_creator_avatar);
        textViewCreatorName = view.findViewById(R.id.text_view_creator_name);
    }

    private void setupClickListeners() {
        buttonStar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isStarred = isChecked;
            if (isChecked) {
                Toast.makeText(getContext(), "Quest added to starred!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Quest removed from starred", Toast.LENGTH_SHORT).show();
            }
        });

        buttonShare.setOnClickListener(v -> shareQuest());
        buttonPostComment.setOnClickListener(v -> postNewComment());

        imageViewQuestMedia.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Opening map/image details...", Toast.LENGTH_SHORT).show();
            // Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:45.4642,9.1900?q=Duomo di Milano"));
            // startActivity(mapIntent);
        });


        View.OnClickListener creatorProfileClickListener = v -> {


            Toast.makeText(getContext(), "Navigating to creator's profile...", Toast.LENGTH_SHORT).show();
            // NavHostFragment.findNavController(QuestViewFragment.this)
            //      .navigate(R.id.action_questViewFragment_to_userProfileFragment);
        };

        imageViewCreatorAvatar.setOnClickListener(creatorProfileClickListener);
        textViewCreatorName.setOnClickListener(creatorProfileClickListener);
    }

    private void setupCommentsRecyclerView() {
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewComments.setNestedScrollingEnabled(false);
        // commentAdapter = new CommentAdapter(new ArrayList<>());
        // recyclerViewComments.setAdapter(commentAdapter);
    }

    private void loadQuestData() {

        textViewQuestName.setText("The Lost Artifact of the Ancients");
        textViewQuestDescription.setText("An ancient legend speaks of a treasure hidden in the heart of the city. Only the bravest will decipher the clues and find it.");
        textViewCreatorName.setText("by Arthur");

        // Glide.with(this).load("URL_CREATOR_IMAGE").into(imageViewCreatorAvatar);
        imageViewCreatorAvatar.setImageResource(R.drawable.quest_icon);

        isStarred = true;
        buttonStar.setChecked(isStarred);

        chipGroupTags.removeAllViews();
        String[] tags = {"Drinking", "Trip", "Outdoor", "Tour", "Adventure", "Pub crawl"};
        for (String tagText : tags) {
            Chip chip = new Chip(requireContext());
            chip.setText(tagText);
            chipGroupTags.addView(chip);
        }

        // Glide.with(this)
        //      .load("URL_IMG/URL_STATIC_MAP")
        //      .placeholder(R.drawable.map_placeholder)
        //      .into(imageViewQuestMedia);
        imageViewQuestMedia.setImageResource(R.drawable.map_placeholder);
        // List<Comment> comments = getCommentsForThisQuest();
        // commentAdapter.updateComments(comments);
    }

    private void shareQuest() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String questName = textViewQuestName.getText().toString();
        String shareBody = "Join us in the quest: " + questName + "! Download QuestMaster to join the adventure!";
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Invite to Quest: " + questName);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(shareIntent, "Share quest with..."));
    }

    private void postNewComment() {
        String commentText = editTextComment.getText().toString().trim();
        if (commentText.isEmpty()) {
            textInputLayoutComment.setError("the comment cannot be empty.");
            return;
        }

        textInputLayoutComment.setError(null);

        // Comment newComment = new Comment("CurrentUser", commentText, System.currentTimeMillis());
        // commentAdapter.addComment(newComment);
        // recyclerViewComments.scrollToPosition(commentAdapter.getItemCount() - 1);

        editTextComment.setText("");
        Toast.makeText(getContext(), "Your comment was published!", Toast.LENGTH_SHORT).show();
    }
}

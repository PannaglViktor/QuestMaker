package com.uni_project.questmaster.ui.quest;

import android.content.Intent;
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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.uni_project.questmaster.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView; // Import for ShapeableImageView
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uni_project.questmaster.model.Quest;

import java.util.List;

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
    private String questId;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment layout
        return inflater.inflate(R.layout.fragment_quest_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (getArguments() != null) {
            questId = getArguments().getString("questId");
        }

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
            if (currentUser != null && questId != null) {
                DocumentReference userRef = db.collection("users").document(currentUser.getUid());
                if (isChecked) {
                    userRef.update("savedQuests", FieldValue.arrayUnion(questId));
                } else {
                    userRef.update("savedQuests", FieldValue.arrayRemove(questId));
                }
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
            NavHostFragment.findNavController(QuestViewFragment.this)
                    .navigate(R.id.action_questViewFragment_to_profileFragment);
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
        if (questId != null) {
            db.collection("quests").document(questId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Quest quest = documentSnapshot.toObject(Quest.class);
                    if (quest != null) {
                        textViewQuestName.setText(quest.getTitle());
                        textViewQuestDescription.setText(quest.getDescription());

                        // Load creator info
                        db.collection("users").document(quest.getOwnerId()).get().addOnSuccessListener(userDocument -> {
                            if (userDocument.exists()) {
                                textViewCreatorName.setText("by " + userDocument.getString("username"));
                                // Load creator avatar using Glide
                            }
                        });

                        /* Load tags
                        chipGroupTags.removeAllViews();
                        if (quest.getTags() != null) {
                            for (String tagText : quest.getTags()) {
                                Chip chip = new Chip(requireContext());
                                chip.setText(tagText);
                                chipGroupTags.addView(chip);
                            }
                        }*/

                        // Check if the quest is starred
                        if (currentUser != null) {
                            db.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(userDocument -> {
                                if (userDocument.exists()) {
                                    List<String> savedQuests = (List<String>) userDocument.get("savedQuests");
                                    if (savedQuests != null && savedQuests.contains(questId)) {
                                        buttonStar.setChecked(true);
                                    }
                                }
                            });
                        }

                    }
                }
            });
        }
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

package com.uni_project.questmaster.ui.quest;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.firestore.Query;
import com.uni_project.questmaster.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uni_project.questmaster.adapter.CommentAdapter;
import com.uni_project.questmaster.adapter.QuestMediaAdapter;
import com.uni_project.questmaster.model.Comment;
import com.uni_project.questmaster.model.Quest;

import java.util.ArrayList;
import java.util.List;

public class QuestViewFragment extends Fragment {

    private RecyclerView recyclerViewQuestMedia;
    private TextView textViewQuestName, textViewQuestDescription;
    private CheckBox buttonStar;
    private ImageButton buttonShare;
    private ChipGroup chipGroupTags;
    private RecyclerView recyclerViewComments;
    private TextInputLayout textInputLayoutComment;
    private EditText editTextComment;
    private MaterialButton buttonPostComment, buttonCompleteQuest, buttonDeleteQuest;

    private ShapeableImageView imageViewCreatorAvatar;
    private TextView textViewCreatorName;

    private String questId;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private Quest currentQuest;
    private CommentAdapter commentAdapter;
    private final List<Comment> commentList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        loadComments();
    }

    private void initializeViews(View view) {
        recyclerViewQuestMedia = view.findViewById(R.id.recycler_view_quest_media);
        textViewQuestName = view.findViewById(R.id.text_view_quest_name);
        textViewQuestDescription = view.findViewById(R.id.text_view_quest_description);
        buttonStar = view.findViewById(R.id.button_star);
        buttonShare = view.findViewById(R.id.button_share);
        //chipGroupTags = view.findViewById(R.id.chip_group_tags);
        recyclerViewComments = view.findViewById(R.id.recycler_view_comments);
        textInputLayoutComment = view.findViewById(R.id.text_input_layout_comment);
        editTextComment = view.findViewById(R.id.edit_text_comment);
        buttonPostComment = view.findViewById(R.id.button_post_comment);
        buttonCompleteQuest = view.findViewById(R.id.button_complete_quest);
        buttonDeleteQuest = view.findViewById(R.id.button_delete_quest);
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
        buttonCompleteQuest.setOnClickListener(v -> completeQuest());
        buttonDeleteQuest.setOnClickListener(v -> deleteQuest());


        View.OnClickListener creatorProfileClickListener = v -> {
            if (currentQuest != null && currentQuest.getOwnerId() != null) {
                Bundle bundle = new Bundle();
                bundle.putString("userId", currentQuest.getOwnerId());
                NavHostFragment.findNavController(QuestViewFragment.this)
                        .navigate(R.id.action_questViewFragment_to_profileFragment, bundle);
            } else {
                Toast.makeText(getContext(), "User ID not found.", Toast.LENGTH_SHORT).show();
            }
        };

        imageViewCreatorAvatar.setOnClickListener(creatorProfileClickListener);
        textViewCreatorName.setOnClickListener(creatorProfileClickListener);
    }

    private void setupCommentsRecyclerView() {
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext()));
        commentAdapter = new CommentAdapter(getContext(),commentList);
        recyclerViewComments.setAdapter(commentAdapter);
        recyclerViewComments.setNestedScrollingEnabled(false);
    }

    private void loadQuestData() {
        if (questId != null) {
            db.collection("quests").document(questId).get().addOnSuccessListener(documentSnapshot -> {
                if (!documentSnapshot.exists()) {
                    Toast.makeText(getContext(), "Quest not found.", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(QuestViewFragment.this).popBackStack();
                    return;
                }

                currentQuest = documentSnapshot.toObject(Quest.class);
                if (currentQuest == null) {
                    Toast.makeText(getContext(), "Failed to load quest data.", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(QuestViewFragment.this).popBackStack();
                    return;
                }

                textViewQuestName.setText(currentQuest.getTitle());
                textViewQuestDescription.setText(currentQuest.getDescription());

                List<Uri> mediaUris = new ArrayList<>();
                try {
                    ApplicationInfo ai = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
                    String apiKey = ai.metaData.getString("com.google.android.geo.API_KEY");

                    if (currentQuest.getStartPoint() != null) {
                        String staticMapUrl = "https://maps.googleapis.com/maps/api/staticmap?center=" +
                                currentQuest.getStartPoint().getLatitude() + "," + currentQuest.getStartPoint().getLongitude() +
                                "&zoom=15&size=600x300&maptype=roadmap&markers=color:red%7Clabel:S%7C" +
                                currentQuest.getStartPoint().getLatitude() + "," + currentQuest.getStartPoint().getLongitude() +
                                "&key=" + apiKey;
                        mediaUris.add(Uri.parse(staticMapUrl));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e("QuestViewFragment", "Failed to load meta-data, NameNotFound: " + e.getMessage());
                }

                if (currentQuest.getImageUrls() != null && !currentQuest.getImageUrls().isEmpty()) {
                    for(String url : currentQuest.getImageUrls()){
                        mediaUris.add(Uri.parse(url));
                    }
                }

                if (!mediaUris.isEmpty()) {
                    QuestMediaAdapter questMediaAdapter = new QuestMediaAdapter(getContext(), mediaUris, null, false);
                    recyclerViewQuestMedia.setAdapter(questMediaAdapter);
                    recyclerViewQuestMedia.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                }

                db.collection("users").document(currentQuest.getOwnerId()).get().addOnSuccessListener(userDocument -> {
                    if (userDocument.exists()) {
                        textViewCreatorName.setText("by " + userDocument.getString("username"));
                    }
                });

                if (currentUser != null) {
                    if (currentUser.getUid().equals(currentQuest.getOwnerId())) {
                        buttonCompleteQuest.setVisibility(View.GONE);
                        buttonDeleteQuest.setVisibility(View.VISIBLE);
                    } else {
                        buttonCompleteQuest.setVisibility(View.VISIBLE);
                        buttonDeleteQuest.setVisibility(View.GONE);
                    }

                    db.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(userDocument -> {
                        if (userDocument.exists()) {
                            List<String> savedQuests = (List<String>) userDocument.get("savedQuests");
                            buttonStar.setChecked(savedQuests != null && savedQuests.contains(questId));

                            if (!currentUser.getUid().equals(currentQuest.getOwnerId())) {
                                List<String> completedQuests = (List<String>) userDocument.get("completedQuests");
                                if (completedQuests != null && completedQuests.contains(questId)) {
                                    buttonCompleteQuest.setEnabled(false);
                                    buttonCompleteQuest.setText("Completed");
                                } else {
                                    buttonCompleteQuest.setEnabled(true);
                                    buttonCompleteQuest.setText(R.string.complete_quest);
                                }
                            }
                        }
                    });
                } else {
                    buttonCompleteQuest.setVisibility(View.VISIBLE);
                    buttonDeleteQuest.setVisibility(View.GONE);
                    buttonCompleteQuest.setEnabled(false);
                    buttonStar.setEnabled(false);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to load quest.", Toast.LENGTH_SHORT).show();
                Log.e("QuestViewFragment", "Error loading quest", e);
                NavHostFragment.findNavController(QuestViewFragment.this).popBackStack();
            });
        }
    }

    private void loadComments() {
        if (questId != null) {
            db.collection("quests").document(questId).collection("comments")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            Log.e("QuestViewFragment", "Error loading comments", e);
                            return;
                        }

                        commentList.clear();
                        if (snapshots != null) {
                            for (Comment comment : snapshots.toObjects(Comment.class)) {
                                commentList.add(comment);
                            }
                        }
                        commentAdapter.notifyDataSetChanged();
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
        if(currentUser == null){
            Toast.makeText(getContext(), "You must be logged in to comment", Toast.LENGTH_SHORT).show();
            return;
        }

        textInputLayoutComment.setError(null);

        db.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String username = documentSnapshot.getString("username");
                String avatarUrl = documentSnapshot.getString("avatarUrl");

                Comment newComment = new Comment(currentUser.getUid(), username, avatarUrl, commentText);

                db.collection("quests").document(questId).collection("comments").add(newComment)
                        .addOnSuccessListener(documentReference -> editTextComment.setText(""))
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to post comment.", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(getContext(), "User data not found.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
        });
    }

    private void completeQuest() {
        if (currentUser != null && questId != null && currentQuest != null) {
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());
            db.runTransaction(transaction -> {
                transaction.update(userRef, "completedQuests", FieldValue.arrayUnion(questId));

                long questPpq = currentQuest.getPpq();
                transaction.update(userRef, "ppq", FieldValue.increment(questPpq));
                
                return null;
            }).addOnSuccessListener(aVoid -> {
                buttonCompleteQuest.setEnabled(false);
                buttonCompleteQuest.setText("Completed");
                Toast.makeText(getContext(), "Quest marked as completed!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update quest status.", Toast.LENGTH_SHORT).show());
        }
    }

    private void deleteQuest() {
        if (questId != null && currentQuest != null && currentUser != null && currentUser.getUid().equals(currentQuest.getOwnerId())) {
            db.collection("quests").document(questId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Quest deleted", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(QuestViewFragment.this).popBackStack();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error deleting quest", Toast.LENGTH_SHORT).show();
                    Log.e("QuestViewFragment", "Error deleting quest", e);
                });
        }
    }
}

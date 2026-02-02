
package com.uni_project.questmaster.ui.quest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputLayout;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.adapter.CommentAdapter;
import com.uni_project.questmaster.adapter.QuestMediaAdapter;
import com.uni_project.questmaster.model.Quest;
import com.uni_project.questmaster.model.User;

import java.util.ArrayList;
import java.util.List;

public class QuestViewFragment extends Fragment {

    private QuestViewViewModel viewModel;
    private String questId;
    private Quest currentQuest;

    private RecyclerView recyclerViewQuestMedia;
    private TextView textViewQuestName, textViewQuestDescription, textViewCreatorName;
    private CheckBox buttonStar;
    private ImageButton buttonShare;
    private RecyclerView recyclerViewComments;
    private TextInputLayout textInputLayoutComment;
    private EditText editTextComment;
    private MaterialButton buttonPostComment, buttonCompleteQuest, buttonDeleteQuest;
    private ShapeableImageView imageViewCreatorAvatar;
    private CommentAdapter commentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quest_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            questId = getArguments().getString("questId");
        }

        QuestViewViewModelFactory factory = new QuestViewViewModelFactory(requireActivity().getApplication());
        viewModel = new ViewModelProvider(this, factory).get(QuestViewViewModel.class);

        initializeViews(view);
        setupCommentsRecyclerView();
        setupObservers();
        setupClickListeners();

        if (questId != null) {
            viewModel.loadQuestData(questId);
        }
    }

    private void initializeViews(View view) {
        recyclerViewQuestMedia = view.findViewById(R.id.recycler_view_quest_media);
        textViewQuestName = view.findViewById(R.id.text_view_quest_name);
        textViewQuestDescription = view.findViewById(R.id.text_view_quest_description);
        buttonStar = view.findViewById(R.id.button_star);
        buttonShare = view.findViewById(R.id.button_share);
        recyclerViewComments = view.findViewById(R.id.recycler_view_comments);
        textInputLayoutComment = view.findViewById(R.id.text_input_layout_comment);
        editTextComment = view.findViewById(R.id.edit_text_comment);
        buttonPostComment = view.findViewById(R.id.button_post_comment);
        buttonCompleteQuest = view.findViewById(R.id.button_complete_quest);
        buttonDeleteQuest = view.findViewById(R.id.button_delete_quest);
        imageViewCreatorAvatar = view.findViewById(R.id.image_view_creator_avatar);
        textViewCreatorName = view.findViewById(R.id.text_view_creator_name);
    }

    private void setupCommentsRecyclerView() {
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext()));
        commentAdapter = new CommentAdapter(getContext(), new ArrayList<>());
        recyclerViewComments.setAdapter(commentAdapter);
        recyclerViewComments.setNestedScrollingEnabled(false);
    }

    private void setupObservers() {
        viewModel.quest.observe(getViewLifecycleOwner(), this::updateQuestUI);
        viewModel.questOwner.observe(getViewLifecycleOwner(), this::updateQuestOwnerUI);
        viewModel.comments.observe(getViewLifecycleOwner(), comments -> commentAdapter.updateComments(comments));
        viewModel.currentUser.observe(getViewLifecycleOwner(), this::updateUserSpecificUI);
    }

    private void updateQuestUI(Quest quest) {
        if (quest == null) {
            Toast.makeText(getContext(), "Quest not found.", Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(QuestViewFragment.this).popBackStack();
            return;
        }
        currentQuest = quest;
        textViewQuestName.setText(quest.getTitle());
        textViewQuestDescription.setText(quest.getDescription());

        List<Uri> mediaUris = new ArrayList<>();
        if (quest.getImageUrls() != null) {
            for (String url : quest.getImageUrls()) {
                mediaUris.add(Uri.parse(url));
            }
        }
        if (!mediaUris.isEmpty()) {
            QuestMediaAdapter questMediaAdapter = new QuestMediaAdapter(getContext(), mediaUris, null, false);
            recyclerViewQuestMedia.setAdapter(questMediaAdapter);
            recyclerViewQuestMedia.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        }

        updateDeleteButtonVisibility();
    }

    private void updateQuestOwnerUI(User user) {
        if (user != null) {
            textViewCreatorName.setText("by " + user.getUsername());
        }
    }

    private void updateUserSpecificUI(User user) {
        if (user == null) {
            buttonCompleteQuest.setVisibility(View.VISIBLE);
            buttonDeleteQuest.setVisibility(View.GONE);
            buttonCompleteQuest.setEnabled(false);
            buttonStar.setEnabled(false);
            return;
        }

        if (currentQuest != null) {
            updateDeleteButtonVisibility();

            if (user.getUid().equals(currentQuest.getOwnerId())) {
                buttonCompleteQuest.setVisibility(View.GONE);
            } else {
                buttonCompleteQuest.setVisibility(View.VISIBLE);
                if (user.getCompletedQuests() != null && user.getCompletedQuests().contains(questId)) {
                    buttonCompleteQuest.setEnabled(false);
                    buttonCompleteQuest.setText("Completed");
                } else {
                    buttonCompleteQuest.setEnabled(true);
                    buttonCompleteQuest.setText(R.string.complete_quest);
                }
            }

            buttonStar.setChecked(user.getSavedQuests() != null && user.getSavedQuests().contains(questId));
        }
        buttonStar.setEnabled(true);
    }

    private void updateDeleteButtonVisibility() {
        String currentUserId = viewModel.getCurrentUserId();
        if (currentQuest != null && currentUserId != null && currentUserId.equals(currentQuest.getOwnerId())) {
            buttonDeleteQuest.setVisibility(View.VISIBLE);
        } else {
            buttonDeleteQuest.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        buttonStar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (questId != null) {
                viewModel.toggleSavedQuest(questId, isChecked);
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
            }
        };
        imageViewCreatorAvatar.setOnClickListener(creatorProfileClickListener);
        textViewCreatorName.setOnClickListener(creatorProfileClickListener);
    }

    private void shareQuest() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Invite to Quest: " + currentQuest.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Join us in the quest: " + currentQuest.getTitle() + "! Download QuestMaster to join the adventure!");
        startActivity(Intent.createChooser(shareIntent, "Share quest with..."));
    }

    private void postNewComment() {
        String commentText = editTextComment.getText().toString().trim();
        if (commentText.isEmpty()) {
            textInputLayoutComment.setError("the comment cannot be empty.");
            return;
        }
        if (viewModel.getCurrentUserId() == null) {
            Toast.makeText(getContext(), "You must be logged in to comment", Toast.LENGTH_SHORT).show();
            return;
        }

        textInputLayoutComment.setError(null);
        viewModel.addComment(questId, commentText);
        editTextComment.setText("");
    }

    private void completeQuest() {
        if (questId != null && currentQuest != null) {
            viewModel.completeQuest(questId, currentQuest.getPpq());
        }
    }

    private void deleteQuest() {
        if (questId != null) {
            viewModel.deleteQuest(questId);
            NavHostFragment.findNavController(QuestViewFragment.this).popBackStack();
        }
    }
}

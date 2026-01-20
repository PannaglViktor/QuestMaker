package com.uni_project.questmaster.ui.quest;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.adapter.QuestAdapter;
import com.uni_project.questmaster.model.Quest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class QuestSavedFragment extends Fragment implements QuestAdapter.OnQuestClickListener {

    private static final String TAG = "QuestSavedFragment";
    private RecyclerView savedQuestsRecyclerView;
    private QuestAdapter questAdapter;
    private ArrayList<Quest> savedQuestsList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quest_saved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        savedQuestsRecyclerView = view.findViewById(R.id.savedQuestsRecyclerView);
        savedQuestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        savedQuestsList = new ArrayList<>();
        questAdapter = new QuestAdapter(getContext(), savedQuestsList, this);
        savedQuestsRecyclerView.setAdapter(questAdapter);

        loadSavedQuests();
    }

    private void loadSavedQuests() {
        String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (currentUserId == null) {
            Log.w(TAG, "User not logged in, cannot load saved quests");
            return;
        }

        Query savedQuestsQuery = db.collection("quests").whereArrayContains("savedBy", currentUserId);

        savedQuestsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                savedQuestsList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Quest quest = document.toObject(Quest.class);
                    quest.setId(document.getId());
                    savedQuestsList.add(quest);
                }
                questAdapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "Error getting saved quests: ", task.getException());
            }
        });
    }

    @Override
    public void onQuestClick(Quest quest) {
        // TODO: Implement action on saved quest click
        Log.d(TAG, "Clicked on quest: " + quest.getId());
    }
}

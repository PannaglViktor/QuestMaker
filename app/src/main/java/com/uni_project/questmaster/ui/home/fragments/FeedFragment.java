package com.uni_project.questmaster.ui.home.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.adapter.QuestAdapter;
import com.uni_project.questmaster.model.Quest;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment implements QuestAdapter.OnQuestClickListener {

    private static final String TAG = "FeedFragment";
    private RecyclerView recyclerView;
    private QuestAdapter questAdapter;
    private CircularProgressIndicator loadingIndicator;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.recycler_view_feed);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);

        setupRecyclerView();
        fetchQuests();
    }

    private void setupRecyclerView() {
        questAdapter = new QuestAdapter(getContext(), new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(questAdapter);
    }

    private void fetchQuests() {
        loadingIndicator.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        db.collection("quests")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    loadingIndicator.setVisibility(View.GONE);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Quest> questList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Quest quest = document.toObject(Quest.class);
                            quest.setId(document.getId());
                            questList.add(quest);
                        }
                        questAdapter.setQuests(questList);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        Log.w(TAG, "Error getting documents", task.getException());
                    }
                });
    }

    @Override
    public void onQuestClick(Quest quest) {
        Bundle bundle = new Bundle();
        bundle.putString("questId", quest.getId());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_feedFragment_to_questViewFragment, bundle);
    }
}

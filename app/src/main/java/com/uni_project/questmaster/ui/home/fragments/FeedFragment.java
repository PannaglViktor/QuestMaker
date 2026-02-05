package com.uni_project.questmaster.ui.home.fragments;

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
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.model.QuestCard;
import com.uni_project.questmaster.ui.home.adapters.QuestCardAdapter;
import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    private static final String TAG = "FeedFragment";
    private RecyclerView recyclerView;
    private QuestCardAdapter questCardAdapter;
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
        questCardAdapter = new QuestCardAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(questCardAdapter);
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
                        List<QuestCard> questList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            QuestCard quest = document.toObject(QuestCard.class);
                            questList.add(quest);
                        }
                        questCardAdapter.setQuests(questList);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        Log.w(TAG, "Error getting documents", task.getException());
                    }
                });
    }
}

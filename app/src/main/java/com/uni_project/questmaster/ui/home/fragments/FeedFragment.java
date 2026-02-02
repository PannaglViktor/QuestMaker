package com.uni_project.questmaster.ui.home.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.adapter.QuestAdapter;
import com.uni_project.questmaster.model.Quest;
import com.uni_project.questmaster.model.User;
import com.uni_project.questmaster.viewmodel.QuestViewModel;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment implements QuestAdapter.OnQuestClickListener, QuestAdapter.OnUserClickListener {

    private RecyclerView recyclerView;
    private QuestAdapter questAdapter;
    private CircularProgressIndicator loadingIndicator;
    private QuestViewModel questViewModel;
    private SearchView searchView;
    private TextView noConnectionTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_feed);
        loadingIndicator = view.findViewById(R.id.loadingIndicator);
        searchView = view.findViewById(R.id.search_view);
        noConnectionTextView = view.findViewById(R.id.no_connection);

        questViewModel = new ViewModelProvider(this).get(QuestViewModel.class);

        setupRecyclerView();

        if (isNetworkAvailable()) {
            observeViewModel();
            setupSearchView();
        } else {
            showNoConnectionScreen();
        }
    }

    private void setupRecyclerView() {
        questAdapter = new QuestAdapter(getContext());
        questAdapter.setOnQuestClickListener(this);
        questAdapter.setOnUserClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(questAdapter);
    }

    private void observeViewModel() {
        loadingIndicator.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        questViewModel.getQuests().observe(getViewLifecycleOwner(), quests -> {
            questViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
                questAdapter.setData(quests, users);
                loadingIndicator.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            });
        });
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String text) {
        List<Quest> filteredQuests = new ArrayList<>();
        List<User> filteredUsers = new ArrayList<>();

        if (questViewModel.getQuests().getValue() != null) {
            for (Quest quest : questViewModel.getQuests().getValue()) {
                if (quest.getTitle().toLowerCase().contains(text.toLowerCase())) {
                    filteredQuests.add(quest);
                }
            }
        }

        if (questViewModel.getUsers().getValue() != null) {
            for (User user : questViewModel.getUsers().getValue()) {
                if (user.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredUsers.add(user);
                }
            }
        }

        questAdapter.setData(filteredQuests, filteredUsers);
    }

    @Override
    public void onQuestClick(Quest quest) {
        Bundle bundle = new Bundle();
        bundle.putString("questId", quest.getId());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_feedFragment_to_questViewFragment, bundle);
    }

    @Override
    public void onUserClick(User user) {
        Bundle bundle = new Bundle();
        bundle.putString("userId", user.getUid());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_feedFragment_to_profileFragment, bundle);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showNoConnectionScreen() {
        recyclerView.setVisibility(View.GONE);
        loadingIndicator.setVisibility(View.GONE);
        searchView.setVisibility(View.GONE);
        noConnectionTextView.setVisibility(View.VISIBLE);
    }
}

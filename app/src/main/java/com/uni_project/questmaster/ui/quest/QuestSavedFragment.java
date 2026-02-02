
package com.uni_project.questmaster.ui.quest;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uni_project.questmaster.R;
import com.uni_project.questmaster.adapter.QuestAdapter;
import com.uni_project.questmaster.model.Quest;
import com.uni_project.questmaster.model.User;

import java.util.ArrayList;
import java.util.Map;

public class QuestSavedFragment extends Fragment implements QuestAdapter.OnQuestClickListener {

    private static final String TAG = "QuestSavedFragment";
    private RecyclerView savedQuestsRecyclerView;
    private QuestAdapter questAdapter;
    private QuestSavedViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_quest_saved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        QuestSavedViewModelFactory factory = new QuestSavedViewModelFactory(requireActivity().getApplication());
        viewModel = new ViewModelProvider(this, factory).get(QuestSavedViewModel.class);

        savedQuestsRecyclerView = view.findViewById(R.id.savedQuestsRecyclerView);
        savedQuestsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        questAdapter = new QuestAdapter(getContext(), new ArrayList<>(), this);
        savedQuestsRecyclerView.setAdapter(questAdapter);

        viewModel.savedQuests.observe(getViewLifecycleOwner(), quests -> {
            if (quests != null) {
                questAdapter.updateQuests(quests);
            } else {
                Log.e(TAG, "Error loading saved quests");
            }
        });

        viewModel.userProfiles.observe(getViewLifecycleOwner(), userProfiles -> {
            if (userProfiles != null) {
                questAdapter.updateUserProfiles(userProfiles);
            }
        });

        viewModel.loadSavedQuests();
    }

    @Override
    public void onQuestClick(Quest quest) {
        Bundle bundle = new Bundle();
        bundle.putString("questId", quest.getId());
        NavHostFragment.findNavController(this).navigate(R.id.action_questSavedFragment_to_questViewFragment, bundle);
    }
}

package com.uni_project.questmaster.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uni_project.questmaster.model.Quest;
import com.uni_project.questmaster.model.User;

import java.util.ArrayList;
import java.util.List;

public class QuestViewModel extends ViewModel {
    private final MutableLiveData<List<Quest>> quests = new MutableLiveData<>();
    private final MutableLiveData<List<User>> users = new MutableLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public QuestViewModel() {
        fetchQuests();
        fetchUsers();
    }

    public LiveData<List<Quest>> getQuests() {
        return quests;
    }

    public LiveData<List<User>> getUsers() {
        return users;
    }

    private void fetchQuests() {
        db.collection("quests")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Quest> questList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Quest quest = document.toObject(Quest.class);
                            quest.setId(document.getId());
                            questList.add(quest);
                        }
                        quests.setValue(questList);
                    }
                });
    }

    private void fetchUsers() {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> userList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            userList.add(user);
                        }
                        users.setValue(userList);
                    }
                });
    }
}

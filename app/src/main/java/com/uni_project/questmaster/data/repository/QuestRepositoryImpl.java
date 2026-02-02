
package com.uni_project.questmaster.data.repository;

import com.uni_project.questmaster.domain.repository.QuestRepository;
import com.uni_project.questmaster.model.Quest;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuestRepositoryImpl implements QuestRepository {
    private final FirebaseFirestore firestore;

    public QuestRepositoryImpl(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public Task<List<Quest>> getQuests() {
        return firestore.collection("quests").get().continueWith(task -> {
            List<Quest> quests = new ArrayList<>();
            for (QueryDocumentSnapshot document : task.getResult()) {
                Quest quest = document.toObject(Quest.class);
                quest.setId(document.getId());
                quests.add(quest);
            }
            return quests;
        });
    }

    @Override
    public Task<Quest> getQuest(String questId) {
        return firestore.collection("quests").document(questId).get().continueWith(task -> {
            Quest quest = task.getResult().toObject(Quest.class);
            if (quest != null) {
                quest.setId(task.getResult().getId());
            }
            return quest;
        });
    }

    @Override
    public Task<Void> createQuest(Quest quest) {
        DocumentReference newQuestRef = firestore.collection("quests").document();
        quest.setId(newQuestRef.getId());
        return newQuestRef.set(quest);
    }

    @Override
    public Task<Void> deleteQuest(String questId) {
        return firestore.collection("quests").document(questId).delete();
    }
}

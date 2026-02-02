
package com.uni_project.questmaster.domain.repository;

import com.google.android.gms.tasks.Task;
import com.uni_project.questmaster.model.Quest;

import java.util.List;

public interface QuestRepository {
    Task<List<Quest>> getQuests();
    Task<Quest> getQuest(String questId);
    Task<Void> createQuest(Quest quest);
    Task<Void> deleteQuest(String questId);
}


package com.uni_project.questmaster.domain.use_case;

import com.google.android.gms.tasks.Task;
import com.uni_project.questmaster.domain.repository.QuestRepository;
import com.uni_project.questmaster.model.Quest;

public class GetQuestUseCase {
    private final QuestRepository repository;

    public GetQuestUseCase(QuestRepository repository) {
        this.repository = repository;
    }

    public Task<Quest> execute(String questId) {
        return repository.getQuest(questId);
    }
}

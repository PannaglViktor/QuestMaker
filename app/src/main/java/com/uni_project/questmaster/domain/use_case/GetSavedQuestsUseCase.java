
package com.uni_project.questmaster.domain.use_case;

import com.google.android.gms.tasks.Task;
import com.uni_project.questmaster.domain.repository.AuthRepository;
import com.uni_project.questmaster.domain.repository.QuestRepository;
import com.uni_project.questmaster.domain.repository.UserRepository;
import com.uni_project.questmaster.model.Quest;

import java.util.List;

public class GetSavedQuestsUseCase {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final QuestRepository questRepository;

    public GetSavedQuestsUseCase(AuthRepository authRepository, UserRepository userRepository, QuestRepository questRepository) {
        this.authRepository = authRepository;
        this.userRepository = userRepository;
        this.questRepository = questRepository;
    }

    public Task<List<Quest>> execute() {
        String currentUserId = authRepository.getCurrentUser().getUid();
        return userRepository.getUser(currentUserId).onSuccessTask(user -> {
            if (user != null && user.getSavedQuests() != null && !user.getSavedQuests().isEmpty()) {
                return questRepository.getQuestsByIds(user.getSavedQuests());
            }
            return null;
        });
    }
}

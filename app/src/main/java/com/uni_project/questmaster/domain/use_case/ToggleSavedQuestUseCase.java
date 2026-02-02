
package com.uni_project.questmaster.domain.use_case;

import com.google.android.gms.tasks.Task;
import com.uni_project.questmaster.domain.repository.AuthRepository;
import com.uni_project.questmaster.domain.repository.UserRepository;

public class ToggleSavedQuestUseCase {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    public ToggleSavedQuestUseCase(UserRepository userRepository, AuthRepository authRepository) {
        this.userRepository = userRepository;
        this.authRepository = authRepository;
    }

    public Task<Void> execute(String questId, boolean isSaved) {
        String uid = authRepository.getCurrentUser().getUid();
        return userRepository.toggleSavedQuest(uid, questId, isSaved);
    }
}

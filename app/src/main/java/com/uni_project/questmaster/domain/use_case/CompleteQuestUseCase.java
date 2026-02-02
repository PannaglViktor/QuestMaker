
package com.uni_project.questmaster.domain.use_case;

import com.google.android.gms.tasks.Task;
import com.uni_project.questmaster.domain.repository.AuthRepository;
import com.uni_project.questmaster.domain.repository.UserRepository;

public class CompleteQuestUseCase {
    private final UserRepository userRepository;
    private final AuthRepository authRepository;

    public CompleteQuestUseCase(UserRepository userRepository, AuthRepository authRepository) {
        this.userRepository = userRepository;
        this.authRepository = authRepository;
    }

    public Task<Void> execute(String questId, long ppq) {
        String uid = authRepository.getCurrentUser().getUid();
        return userRepository.completeQuest(uid, questId, ppq);
    }
}

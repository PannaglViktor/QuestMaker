
package com.uni_project.questmaster.domain.use_case;

import com.google.android.gms.tasks.Task;
import com.uni_project.questmaster.domain.repository.UserRepository;
import com.uni_project.questmaster.model.User;

public class GetUserProfileUseCase {
    private final UserRepository repository;

    public GetUserProfileUseCase(UserRepository repository) {
        this.repository = repository;
    }

    public Task<User> execute(String uid) {
        return repository.getUser(uid);
    }
}


package com.uni_project.questmaster.domain.use_case;

import com.uni_project.questmaster.domain.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

public class GetCurrentUserUseCase {
    private final AuthRepository repository;

    public GetCurrentUserUseCase(AuthRepository repository) {
        this.repository = repository;
    }

    public FirebaseUser execute() {
        return repository.getCurrentUser();
    }
}

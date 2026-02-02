
package com.uni_project.questmaster.domain.use_case;

import com.uni_project.questmaster.domain.repository.AuthRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class LoginUserUseCase {
    private final AuthRepository repository;

    public LoginUserUseCase(AuthRepository repository) {
        this.repository = repository;
    }

    public Task<AuthResult> execute(String email, String password) {
        return repository.signInWithEmailAndPassword(email, password);
    }
}

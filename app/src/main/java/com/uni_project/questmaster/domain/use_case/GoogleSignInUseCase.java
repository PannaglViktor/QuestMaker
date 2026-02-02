
package com.uni_project.questmaster.domain.use_case;

import com.uni_project.questmaster.domain.repository.AuthRepository;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class GoogleSignInUseCase {
    private final AuthRepository repository;

    public GoogleSignInUseCase(AuthRepository repository) {
        this.repository = repository;
    }

    public Task<AuthResult> execute(GoogleSignInAccount account) {
        return repository.signInWithGoogle(account);
    }
}


package com.uni_project.questmaster.domain.use_case;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.uni_project.questmaster.domain.repository.AuthRepository;
import com.uni_project.questmaster.domain.repository.UserRepository;
import com.uni_project.questmaster.model.User;

public class CreateUserUseCase {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    public CreateUserUseCase(AuthRepository authRepository, UserRepository userRepository) {
        this.authRepository = authRepository;
        this.userRepository = userRepository;
    }

    public Task<AuthResult> execute(String email, String password, String username) {
        return authRepository.createUserWithEmailAndPassword(email, password)
                .onSuccessTask(authResult -> {
                    String uid = authResult.getUser().getUid();
                    User user = new User(uid, username, email);
                    return userRepository.updateUser(user).continueWith(task -> authResult);
                });
    }
}

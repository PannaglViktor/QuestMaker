
package com.uni_project.questmaster.domain.repository;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public interface AuthRepository {
    FirebaseUser getCurrentUser();
    Task<AuthResult> signInWithEmailAndPassword(String email, String password);
    Task<AuthResult> createUserWithEmailAndPassword(String email, String password);
    Task<AuthResult> signInWithGoogle(GoogleSignInAccount account);
    void signOut();
}

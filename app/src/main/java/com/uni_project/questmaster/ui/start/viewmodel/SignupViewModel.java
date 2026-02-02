
package com.uni_project.questmaster.ui.start.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthResult;
import com.uni_project.questmaster.domain.use_case.CreateUserUseCase;
import com.uni_project.questmaster.domain.use_case.GoogleSignInUseCase;

public class SignupViewModel extends ViewModel {

    private final CreateUserUseCase createUserUseCase;
    private final GoogleSignInUseCase googleSignInUseCase;

    private final MutableLiveData<AuthResult> _signupResult = new MutableLiveData<>();
    public LiveData<AuthResult> signupResult = _signupResult;

    public SignupViewModel(CreateUserUseCase createUserUseCase, GoogleSignInUseCase googleSignInUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.googleSignInUseCase = googleSignInUseCase;
    }

    public void signup(String email, String password, String username) {
        createUserUseCase.execute(email, password, username)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        _signupResult.postValue(task.getResult());
                    } else {
                        _signupResult.postValue(null);
                    }
                });
    }

    public void signupWithGoogle(GoogleSignInAccount account) {
        googleSignInUseCase.execute(account)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        _signupResult.postValue(task.getResult());
                    } else {
                        _signupResult.postValue(null);
                    }
                });
    }
}

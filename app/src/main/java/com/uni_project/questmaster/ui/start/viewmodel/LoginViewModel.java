
package com.uni_project.questmaster.ui.start.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthResult;
import com.uni_project.questmaster.domain.use_case.GetCurrentUserUseCase;
import com.uni_project.questmaster.domain.use_case.GoogleSignInUseCase;
import com.uni_project.questmaster.domain.use_case.LoginUserUseCase;

public class LoginViewModel extends ViewModel {

    private final LoginUserUseCase loginUserUseCase;
    private final GoogleSignInUseCase googleSignInUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    private final MutableLiveData<AuthResult> _loginResult = new MutableLiveData<>();
    public LiveData<AuthResult> loginResult = _loginResult;

    private final MutableLiveData<Boolean> _isLoggedIn = new MutableLiveData<>();
    public LiveData<Boolean> isLoggedIn = _isLoggedIn;

    public LoginViewModel(LoginUserUseCase loginUserUseCase, GoogleSignInUseCase googleSignInUseCase, GetCurrentUserUseCase getCurrentUserUseCase) {
        this.loginUserUseCase = loginUserUseCase;
        this.googleSignInUseCase = googleSignInUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;

        if (getCurrentUserUseCase.execute() != null) {
            _isLoggedIn.postValue(true);
        } else {
            _isLoggedIn.postValue(false);
        }
    }

    public void login(String email, String password) {
        loginUserUseCase.execute(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        _loginResult.postValue(task.getResult());
                    } else {
                        _loginResult.postValue(null);
                    }
                });
    }

    public void loginWithGoogle(GoogleSignInAccount account) {
        googleSignInUseCase.execute(account)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        _loginResult.postValue(task.getResult());
                    } else {
                        _loginResult.postValue(null);
                    }
                });
    }
}

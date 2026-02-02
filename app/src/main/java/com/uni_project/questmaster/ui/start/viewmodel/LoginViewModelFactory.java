
package com.uni_project.questmaster.ui.start.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.uni_project.questmaster.QuestMasterApplication;
import com.uni_project.questmaster.domain.repository.AuthRepository;
import com.uni_project.questmaster.domain.use_case.GetCurrentUserUseCase;
import com.uni_project.questmaster.domain.use_case.GoogleSignInUseCase;
import com.uni_project.questmaster.domain.use_case.LoginUserUseCase;

public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public LoginViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            AuthRepository authRepository = ((QuestMasterApplication) application).appContainer.authRepository;
            LoginUserUseCase loginUserUseCase = new LoginUserUseCase(authRepository);
            GoogleSignInUseCase googleSignInUseCase = new GoogleSignInUseCase(authRepository);
            GetCurrentUserUseCase getCurrentUserUseCase = new GetCurrentUserUseCase(authRepository);
            return (T) new LoginViewModel(loginUserUseCase, googleSignInUseCase, getCurrentUserUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

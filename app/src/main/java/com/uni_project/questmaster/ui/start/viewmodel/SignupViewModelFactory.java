
package com.uni_project.questmaster.ui.start.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.uni_project.questmaster.QuestMasterApplication;
import com.uni_project.questmaster.domain.repository.AuthRepository;
import com.uni_project.questmaster.domain.repository.UserRepository;
import com.uni_project.questmaster.domain.use_case.CreateUserUseCase;
import com.uni_project.questmaster.domain.use_case.GoogleSignInUseCase;

public class SignupViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public SignupViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SignupViewModel.class)) {
            AuthRepository authRepository = ((QuestMasterApplication) application).appContainer.authRepository;
            UserRepository userRepository = ((QuestMasterApplication) application).appContainer.userRepository;
            CreateUserUseCase createUserUseCase = new CreateUserUseCase(authRepository, userRepository);
            GoogleSignInUseCase googleSignInUseCase = new GoogleSignInUseCase(authRepository);
            return (T) new SignupViewModel(createUserUseCase, googleSignInUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

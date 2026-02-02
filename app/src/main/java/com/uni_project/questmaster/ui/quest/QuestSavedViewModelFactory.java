
package com.uni_project.questmaster.ui.quest;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.uni_project.questmaster.QuestMasterApplication;
import com.uni_project.questmaster.domain.repository.AuthRepository;
import com.uni_project.questmaster.domain.repository.QuestRepository;
import com.uni_project.questmaster.domain.repository.UserRepository;
import com.uni_project.questmaster.domain.use_case.GetSavedQuestsUseCase;
import com.uni_project.questmaster.domain.use_case.GetUserProfileUseCase;

public class QuestSavedViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public QuestSavedViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(QuestSavedViewModel.class)) {
            AuthRepository authRepository = ((QuestMasterApplication) application).appContainer.authRepository;
            UserRepository userRepository = ((QuestMasterApplication) application).appContainer.userRepository;
            QuestRepository questRepository = ((QuestMasterApplication) application).appContainer.questRepository;
            GetSavedQuestsUseCase getSavedQuestsUseCase = new GetSavedQuestsUseCase(authRepository, userRepository, questRepository);
            GetUserProfileUseCase getUserProfileUseCase = new GetUserProfileUseCase(userRepository);
            return (T) new QuestSavedViewModel(getSavedQuestsUseCase, getUserProfileUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

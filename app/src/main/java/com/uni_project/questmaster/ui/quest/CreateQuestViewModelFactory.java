
package com.uni_project.questmaster.ui.quest;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.storage.StorageReference;
import com.uni_project.questmaster.QuestMasterApplication;
import com.uni_project.questmaster.domain.repository.AuthRepository;
import com.uni_project.questmaster.domain.repository.QuestRepository;
import com.uni_project.questmaster.domain.use_case.CreateQuestUseCase;

public class CreateQuestViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public CreateQuestViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CreateQuestViewModel.class)) {
            QuestMasterApplication app = (QuestMasterApplication) application;
            AuthRepository authRepository = app.appContainer.authRepository;
            QuestRepository questRepository = app.appContainer.questRepository;
            StorageReference storageReference = app.appContainer.storageReference;

            CreateQuestUseCase createQuestUseCase = new CreateQuestUseCase(questRepository, authRepository, storageReference);

            return (T) new CreateQuestViewModel(createQuestUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

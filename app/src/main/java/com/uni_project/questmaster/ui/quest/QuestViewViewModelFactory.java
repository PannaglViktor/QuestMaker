
package com.uni_project.questmaster.ui.quest;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.uni_project.questmaster.QuestMasterApplication;
import com.uni_project.questmaster.domain.repository.AuthRepository;
import com.uni_project.questmaster.domain.repository.CommentRepository;
import com.uni_project.questmaster.domain.repository.QuestRepository;
import com.uni_project.questmaster.domain.repository.UserRepository;
import com.uni_project.questmaster.domain.use_case.AddCommentUseCase;
import com.uni_project.questmaster.domain.use_case.CompleteQuestUseCase;
import com.uni_project.questmaster.domain.use_case.DeleteQuestUseCase;
import com.uni_project.questmaster.domain.use_case.GetCommentsUseCase;
import com.uni_project.questmaster.domain.use_case.GetQuestUseCase;
import com.uni_project.questmaster.domain.use_case.GetUserProfileUseCase;
import com.uni_project.questmaster.domain.use_case.ToggleSavedQuestUseCase;

public class QuestViewViewModelFactory implements ViewModelProvider.Factory {

    private final Application application;

    public QuestViewViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(QuestViewViewModel.class)) {
            QuestMasterApplication app = (QuestMasterApplication) application;
            AuthRepository authRepository = app.appContainer.authRepository;
            UserRepository userRepository = app.appContainer.userRepository;
            QuestRepository questRepository = app.appContainer.questRepository;
            CommentRepository commentRepository = app.appContainer.commentRepository;

            GetQuestUseCase getQuestUseCase = new GetQuestUseCase(questRepository);
            GetCommentsUseCase getCommentsUseCase = new GetCommentsUseCase(commentRepository);
            AddCommentUseCase addCommentUseCase = new AddCommentUseCase(commentRepository, authRepository, userRepository);
            ToggleSavedQuestUseCase toggleSavedQuestUseCase = new ToggleSavedQuestUseCase(userRepository, authRepository);
            CompleteQuestUseCase completeQuestUseCase = new CompleteQuestUseCase(userRepository, authRepository);
            DeleteQuestUseCase deleteQuestUseCase = new DeleteQuestUseCase(questRepository);
            GetUserProfileUseCase getUserProfileUseCase = new GetUserProfileUseCase(userRepository);

            return (T) new QuestViewViewModel(getQuestUseCase, getCommentsUseCase, addCommentUseCase, toggleSavedQuestUseCase, completeQuestUseCase, deleteQuestUseCase, getUserProfileUseCase, authRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

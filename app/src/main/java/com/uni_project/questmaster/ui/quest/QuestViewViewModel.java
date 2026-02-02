
package com.uni_project.questmaster.ui.quest;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uni_project.questmaster.domain.repository.AuthRepository;
import com.uni_project.questmaster.domain.use_case.AddCommentUseCase;
import com.uni_project.questmaster.domain.use_case.CompleteQuestUseCase;
import com.uni_project.questmaster.domain.use_case.DeleteQuestUseCase;
import com.uni_project.questmaster.domain.use_case.GetCommentsUseCase;
import com.uni_project.questmaster.domain.use_case.GetQuestUseCase;
import com.uni_project.questmaster.domain.use_case.GetUserProfileUseCase;
import com.uni_project.questmaster.domain.use_case.ToggleSavedQuestUseCase;
import com.uni_project.questmaster.model.Comment;
import com.uni_project.questmaster.model.Quest;
import com.uni_project.questmaster.model.User;

import java.util.List;

public class QuestViewViewModel extends ViewModel {

    private final GetQuestUseCase getQuestUseCase;
    private final GetCommentsUseCase getCommentsUseCase;
    private final AddCommentUseCase addCommentUseCase;
    private final ToggleSavedQuestUseCase toggleSavedQuestUseCase;
    private final CompleteQuestUseCase completeQuestUseCase;
    private final DeleteQuestUseCase deleteQuestUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final AuthRepository authRepository;

    private final MutableLiveData<Quest> _quest = new MutableLiveData<>();
    public LiveData<Quest> quest = _quest;

    private final MutableLiveData<User> _questOwner = new MutableLiveData<>();
    public LiveData<User> questOwner = _questOwner;

    private final MutableLiveData<List<Comment>> _comments = new MutableLiveData<>();
    public LiveData<List<Comment>> comments = _comments;

    private final MutableLiveData<User> _currentUser = new MutableLiveData<>();
    public LiveData<User> currentUser = _currentUser;

    public QuestViewViewModel(GetQuestUseCase getQuestUseCase, GetCommentsUseCase getCommentsUseCase, AddCommentUseCase addCommentUseCase, ToggleSavedQuestUseCase toggleSavedQuestUseCase, CompleteQuestUseCase completeQuestUseCase, DeleteQuestUseCase deleteQuestUseCase, GetUserProfileUseCase getUserProfileUseCase, AuthRepository authRepository) {
        this.getQuestUseCase = getQuestUseCase;
        this.getCommentsUseCase = getCommentsUseCase;
        this.addCommentUseCase = addCommentUseCase;
        this.toggleSavedQuestUseCase = toggleSavedQuestUseCase;
        this.completeQuestUseCase = completeQuestUseCase;
        this.deleteQuestUseCase = deleteQuestUseCase;
        this.getUserProfileUseCase = getUserProfileUseCase;
        this.authRepository = authRepository;
    }

    public void loadQuestData(String questId) {
        loadQuest(questId);
        loadComments(questId);
        loadCurrentUser();
    }

    private void loadQuest(String questId) {
        getQuestUseCase.execute(questId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Quest quest = task.getResult();
                _quest.postValue(quest);
                if (quest != null) {
                    loadQuestOwner(quest.getOwnerId());
                }
            } else {
                _quest.postValue(null);
            }
        });
    }

    private void loadQuestOwner(String ownerId) {
        getUserProfileUseCase.execute(ownerId).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                _questOwner.postValue(task.getResult());
            }
        });
    }

    private void loadComments(String questId) {
        getCommentsUseCase.execute(questId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                _comments.postValue(task.getResult());
            } else {
                _comments.postValue(null);
            }
        });
    }

    private void loadCurrentUser() {
        if (authRepository.getCurrentUser() != null) {
            String uid = authRepository.getCurrentUser().getUid();
            getUserProfileUseCase.execute(uid).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    _currentUser.postValue(task.getResult());
                }
            });
        }
    }

    public void addComment(String questId, String commentText) {
        addCommentUseCase.execute(questId, commentText).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadComments(questId);
            }
        });
    }

    public void toggleSavedQuest(String questId, boolean isSaved) {
        toggleSavedQuestUseCase.execute(questId, isSaved);
    }

    public void completeQuest(String questId, long ppq) {
        completeQuestUseCase.execute(questId, ppq).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                loadCurrentUser();
            }
        });
    }

    public void deleteQuest(String questId) {
        deleteQuestUseCase.execute(questId);
    }

    public String getCurrentUserId() {
        if (authRepository.getCurrentUser() != null) {
            return authRepository.getCurrentUser().getUid();
        }
        return null;
    }
}

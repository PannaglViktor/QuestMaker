
package com.uni_project.questmaster.ui.quest;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uni_project.questmaster.domain.use_case.GetSavedQuestsUseCase;
import com.uni_project.questmaster.domain.use_case.GetUserProfileUseCase;
import com.uni_project.questmaster.model.Quest;
import com.uni_project.questmaster.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestSavedViewModel extends ViewModel {

    private final GetSavedQuestsUseCase getSavedQuestsUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;

    private final MutableLiveData<List<Quest>> _savedQuests = new MutableLiveData<>();
    public LiveData<List<Quest>> savedQuests = _savedQuests;

    private final MutableLiveData<Map<String, User>> _userProfiles = new MutableLiveData<>();
    public LiveData<Map<String, User>> userProfiles = _userProfiles;

    public QuestSavedViewModel(GetSavedQuestsUseCase getSavedQuestsUseCase, GetUserProfileUseCase getUserProfileUseCase) {
        this.getSavedQuestsUseCase = getSavedQuestsUseCase;
        this.getUserProfileUseCase = getUserProfileUseCase;
    }

    public void loadSavedQuests() {
        getSavedQuestsUseCase.execute().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Quest> quests = task.getResult();
                _savedQuests.postValue(quests);
                loadUserProfiles(quests);
            } else {
                _savedQuests.postValue(null);
            }
        });
    }

    private void loadUserProfiles(List<Quest> quests) {
        if (quests == null) return;
        Map<String, User> userProfiles = new HashMap<>();
        for (Quest quest : quests) {
            getUserProfileUseCase.execute(quest.getOwnerId()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userProfiles.put(quest.getOwnerId(), task.getResult());
                    _userProfiles.postValue(userProfiles);
                }
            });
        }
    }
}

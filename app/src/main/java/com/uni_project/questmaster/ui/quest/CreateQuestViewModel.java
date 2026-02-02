
package com.uni_project.questmaster.ui.quest;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.uni_project.questmaster.domain.use_case.CreateQuestUseCase;

import java.util.List;

public class CreateQuestViewModel extends ViewModel {

    private final CreateQuestUseCase createQuestUseCase;

    private final MutableLiveData<Boolean> _questCreationResult = new MutableLiveData<>();
    public LiveData<Boolean> questCreationResult = _questCreationResult;

    public CreateQuestViewModel(CreateQuestUseCase createQuestUseCase) {
        this.createQuestUseCase = createQuestUseCase;
    }

    public void createQuest(String title, String description, long ppq, List<Uri> mediaUris, LatLng location) {
        createQuestUseCase.execute(title, description, ppq, mediaUris, location).addOnCompleteListener(task -> {
            _questCreationResult.postValue(task.isSuccessful());
        });
    }
}

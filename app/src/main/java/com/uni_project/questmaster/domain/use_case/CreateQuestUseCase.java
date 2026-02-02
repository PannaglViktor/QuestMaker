
package com.uni_project.questmaster.domain.use_case;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uni_project.questmaster.domain.repository.AuthRepository;
import com.uni_project.questmaster.domain.repository.QuestRepository;
import com.uni_project.questmaster.model.Quest;
import com.uni_project.questmaster.model.QuestLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateQuestUseCase {
    private final QuestRepository questRepository;
    private final AuthRepository authRepository;
    private final StorageReference storageReference;

    public CreateQuestUseCase(QuestRepository questRepository, AuthRepository authRepository, StorageReference storageReference) {
        this.questRepository = questRepository;
        this.authRepository = authRepository;
        this.storageReference = storageReference;
    }

    public Task<Void> execute(String title, String description, long ppq, List<Uri> mediaUris, LatLng location) {
        String ownerId = authRepository.getCurrentUser().getUid();

        QuestLocation questLocation = null;
        if (location != null) {
            questLocation = new QuestLocation(location.latitude, location.longitude);
        }

        if (mediaUris == null || mediaUris.isEmpty()) {
            return createQuest(title, description, ppq, ownerId, new ArrayList<>(), questLocation);
        }

        List<Task<Uri>> uploadTasks = new ArrayList<>();
        for (Uri uri : mediaUris) {
            StorageReference ref = storageReference.child("quest_media/" + UUID.randomUUID().toString());
            UploadTask uploadTask = ref.putFile(uri);
            Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            });
            uploadTasks.add(urlTask);
        }
        QuestLocation finalQuestLocation = questLocation;
        return Tasks.whenAllSuccess(uploadTasks).onSuccessTask(urls -> {
            List<String> downloadUrls = new ArrayList<>();
            for (Object url : urls) {
                downloadUrls.add(url.toString());
            }
            return createQuest(title, description, ppq, ownerId, downloadUrls, finalQuestLocation);
        });
    }

    private Task<Void> createQuest(String title, String description, long ppq, String ownerId, List<String> imageUrls, QuestLocation location) {
        Quest quest = new Quest();
        quest.setTitle(title);
        quest.setDescription(description);
        quest.setOwnerId(ownerId);
        quest.setImageUrls(imageUrls);
        quest.setLocation(location);
        quest.setPpq(ppq);
        return questRepository.createQuest(quest);
    }
}

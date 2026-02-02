
package com.uni_project.questmaster.domain.repository;

import com.uni_project.questmaster.model.User;
import com.google.android.gms.tasks.Task;

public interface UserRepository {
    Task<User> getUser(String uid);
    Task<Void> createUser(User user);
    Task<Void> toggleSavedQuest(String uid, String questId, boolean isSaved);
    Task<Void> completeQuest(String uid, String questId, long ppq);
}

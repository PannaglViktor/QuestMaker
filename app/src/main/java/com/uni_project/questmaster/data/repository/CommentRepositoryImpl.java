
package com.uni_project.questmaster.data.repository;

import com.uni_project.questmaster.domain.repository.CommentRepository;
import com.uni_project.questmaster.model.Comment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CommentRepositoryImpl implements CommentRepository {
    private final FirebaseFirestore firestore;

    public CommentRepositoryImpl(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public Task<List<Comment>> getComments(String questId) {
        return firestore.collection("quests").document(questId).collection("comments").orderBy("timestamp").get().continueWith(task -> {
            List<Comment> comments = new ArrayList<>();
            for (QueryDocumentSnapshot document : task.getResult()) {
                comments.add(document.toObject(Comment.class));
            }
            return comments;
        });
    }

    @Override
    public Task<Void> addComment(String questId, Comment comment) {
        return firestore.collection("quests").document(questId).collection("comments").add(comment).continueWith(task -> null);
    }
}

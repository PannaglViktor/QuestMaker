
package com.uni_project.questmaster.domain.repository;

import com.uni_project.questmaster.model.Comment;
import com.google.android.gms.tasks.Task;

import java.util.List;

public interface CommentRepository {
    Task<List<Comment>> getComments(String questId);
    Task<Void> addComment(String questId, Comment comment);
}

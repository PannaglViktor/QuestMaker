
package com.uni_project.questmaster.domain.use_case;

import com.google.android.gms.tasks.Task;
import com.uni_project.questmaster.domain.repository.CommentRepository;
import com.uni_project.questmaster.model.Comment;

import java.util.List;

public class GetCommentsUseCase {
    private final CommentRepository repository;

    public GetCommentsUseCase(CommentRepository repository) {
        this.repository = repository;
    }

    public Task<List<Comment>> execute(String questId) {
        return repository.getComments(questId);
    }
}


package com.uni_project.questmaster.domain.use_case;

import com.google.android.gms.tasks.Task;
import com.uni_project.questmaster.domain.repository.AuthRepository;
import com.uni_project.questmaster.domain.repository.CommentRepository;
import com.uni_project.questmaster.domain.repository.UserRepository;
import com.uni_project.questmaster.model.Comment;

public class AddCommentUseCase {
    private final CommentRepository commentRepository;
    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    public AddCommentUseCase(CommentRepository commentRepository, AuthRepository authRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.authRepository = authRepository;
        this.userRepository = userRepository;
    }

    public Task<Void> execute(String questId, String commentText) {
        String uid = authRepository.getCurrentUser().getUid();
        return userRepository.getUser(uid).onSuccessTask(user -> {
            if (user != null) {
                Comment comment = new Comment(uid, user.getUsername(), user.getAvatarUrl(), commentText);
                return commentRepository.addComment(questId, comment);
            }
            return null;
        });
    }
}

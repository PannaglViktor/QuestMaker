
package com.uni_project.questmaster.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uni_project.questmaster.data.repository.AuthRepositoryImpl;
import com.uni_project.questmaster.data.repository.CommentRepositoryImpl;
import com.uni_project.questmaster.data.repository.QuestRepositoryImpl;
import com.uni_project.questmaster.data.repository.UserRepositoryImpl;
import com.uni_project.questmaster.domain.repository.AuthRepository;
import com.uni_project.questmaster.domain.repository.CommentRepository;
import com.uni_project.questmaster.domain.repository.QuestRepository;
import com.uni_project.questmaster.domain.repository.UserRepository;

public class AppContainer {

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public final AuthRepository authRepository = new AuthRepositoryImpl(firebaseAuth);
    public final QuestRepository questRepository = new QuestRepositoryImpl(firebaseFirestore);
    public final UserRepository userRepository = new UserRepositoryImpl(firebaseFirestore);
    public final CommentRepository commentRepository = new CommentRepositoryImpl(firebaseFirestore);
}

package com.uni_project.questmaster.ui.utils;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.adapter.UserAdapter;
import com.uni_project.questmaster.model.User;
import java.util.ArrayList;
import java.util.List;

public class FollowListFragment extends Fragment {

    private static final String TAG = "FollowListFragment";
    private static final String ARG_USER_ID = "userId";
    private static final String ARG_LIST_TYPE = "listType";

    private String userId;
    private String listType; // "followers" or "following"

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList;
    private FirebaseFirestore db;

    public static Bundle newBundle(String userId, String listType) {
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        args.putString(ARG_LIST_TYPE, listType);
        return args;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
            listType = getArguments().getString(ARG_LIST_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if ("followers".equals(listType)) {
            requireActivity().setTitle(R.string.followers);
        } else {
            requireActivity().setTitle(R.string.following);
        }
        return inflater.inflate(R.layout.fragment_follow_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.followRecyclerView);

        setupRecyclerView();
        loadFollowData();
    }

    private void setupRecyclerView() {
        userList = new ArrayList<>();
        NavController navController = NavHostFragment.findNavController(this);

        adapter = new UserAdapter(getContext(), userList, navController, R.id.action_followListFragment_to_profileFragment);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadFollowData() {
        if (userId == null || listType == null) {
            Log.e(TAG, "User ID or list type is null");
            return;
        }

        db.collection("users").document(userId).collection(listType)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userList.clear();
                    if (queryDocumentSnapshots.isEmpty()) {
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String followUserId = doc.getId();
                        db.collection("users").document(followUserId).get()
                                .addOnSuccessListener(userDocument -> {
                                    if (userDocument.exists()) {
                                        User user = userDocument.toObject(User.class);
                                        if (user != null) {
                                            user.setUid(userDocument.getId());
                                            userList.add(user);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> Log.e(TAG, "Error fetching user details", e));
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading follow list", e));
    }
}

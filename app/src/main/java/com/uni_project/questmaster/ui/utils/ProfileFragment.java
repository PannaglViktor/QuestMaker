package com.uni_project.questmaster.ui.utils;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uni_project.questmaster.R;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final String ARG_USER_ID = "userId";

    private String userId;
    private String currentUserId;

    private ShapeableImageView profileImage;
    private TextView profileName, profileScore, profileDescription, followersCount, followingCount;
    private Button followButton;
    private LinearLayout followersLayout, followingLayout;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;

    private ListenerRegistration followersListener, followingListener, followingCheckListener;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(String userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }

        if (getArguments() != null && getArguments().containsKey(ARG_USER_ID)) {
            userId = getArguments().getString(ARG_USER_ID);
        } else {
            userId = currentUserId;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImage = view.findViewById(R.id.profileImage);
        profileName = view.findViewById(R.id.profileName);
        profileScore = view.findViewById(R.id.profileScore);
        profileDescription = view.findViewById(R.id.profileDescription);
        followButton = view.findViewById(R.id.followButton);
        followersCount = view.findViewById(R.id.followersCount);
        followingCount = view.findViewById(R.id.followingCount);
        followersLayout = view.findViewById(R.id.followersLayout);
        followingLayout = view.findViewById(R.id.followingLayout);


        if (userId != null) {
            if (userId.equals(currentUserId)) {
                followButton.setVisibility(View.GONE);
            } else {
                followButton.setVisibility(View.VISIBLE);
                checkIfFollowing();
                followButton.setOnClickListener(v -> toggleFollow());
            }
            loadUserProfile();
            attachCountListeners(); // Real-time counts
            setupClickListeners();
        } else {
            Log.e(TAG, "User ID is null.");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (followersListener != null) followersListener.remove();
        if (followingListener != null) followingListener.remove();
        if (followingCheckListener != null) followingCheckListener.remove();
    }

    private void loadUserProfile() {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    profileName.setText(document.getString("username"));
                    profileDescription.setText(document.getString("description"));

                    if (document.contains("totalPoints")) {
                        profileScore.setText(String.format("Score: %d", document.getLong("totalPoints")));
                    } else {
                        profileScore.setText("Score: 0");
                    }

                    if (document.contains("profileImageUrl")) {
                        String imageUrl = document.getString("profileImageUrl");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
                            Glide.with(this)
                                    .load(imageRef)
                                    .placeholder(R.drawable.ic_launcher_foreground)
                                    .error(R.drawable.ic_launcher_foreground)
                                    .into(profileImage);
                        }
                    }

                } else {
                    Log.d(TAG, "No such document");
                    Toast.makeText(getContext(), "User profile not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
                Toast.makeText(getContext(), "Failed to load profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void attachCountListeners() {
        followersListener = db.collection("users").document(userId).collection("followers")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (snapshots != null) {
                        followersCount.setText(String.valueOf(snapshots.size()));
                    }
                });

        followingListener = db.collection("users").document(userId).collection("following")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (snapshots != null) {
                        followingCount.setText(String.valueOf(snapshots.size()));
                    }
                });
    }

    private void setupClickListeners() {
        followersLayout.setOnClickListener(v -> {
            Bundle bundle = FollowListFragment.newBundle(userId, "followers");
            NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_followListFragment, bundle);
        });

        followingLayout.setOnClickListener(v -> {
            Bundle bundle = FollowListFragment.newBundle(userId, "following");
            NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_followListFragment, bundle);
        });
    }

    private void checkIfFollowing() {
        if (currentUserId == null) return;
        DocumentReference followingRef = db.collection("users").document(currentUserId)
                .collection("following").document(userId);

        followingCheckListener = followingRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                followButton.setText(R.string.following);
            } else {
                followButton.setText(R.string.follow);
            }
        });
    }

    private void toggleFollow() {
        if (currentUserId == null) {
            Toast.makeText(getContext(), "You must be logged in to follow users.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference followingRef = db.collection("users").document(currentUserId)
                .collection("following").document(userId);
        DocumentReference followerRef = db.collection("users").document(userId)
                .collection("followers").document(currentUserId);

        followingRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // Unfollow
                    followingRef.delete();
                    followerRef.delete();
                } else {
                    // Follow
                    Map<String, Object> data = new HashMap<>();
                    data.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());
                    followingRef.set(data);
                    followerRef.set(data);
                }
            } else {
                Log.e(TAG, "Error checking following status", task.getException());
            }
        });
    }
}

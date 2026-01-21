package com.uni_project.questmaster.ui.home.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.ui.start.StartActivity;
import com.uni_project.questmaster.ui.utils.FollowListFragment;

import java.util.HashMap;
import java.util.Map;

public class PersonalProfileFragment extends Fragment {

    private static final String TAG = "PersonalProfileFragment";
    private ShapeableImageView profileImage;
    private EditText profileName, profileDescription;
    private TextView profileScore, followersCount, followingCount, profilePpq;
    private ImageView editNameIcon, editDescriptionIcon, logoutIcon;
    private Button savedQuestsButton;
    private LinearLayout followersLayout, followingLayout;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private String currentUserId;

    private Uri localImageUri = null;

    private ListenerRegistration userListener;
    private ListenerRegistration followersListener;
    private ListenerRegistration followingListener;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    localImageUri = result.getData().getData();
                    if (localImageUri != null) {
                        Glide.with(this).load(localImageUri).centerCrop().into(profileImage);
                        uploadProfileImage();
                    }
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // VIEWS
        profileImage = view.findViewById(R.id.profileImage);
        profileName = view.findViewById(R.id.profileName);
        profileDescription = view.findViewById(R.id.profileDescription);
        profileScore = view.findViewById(R.id.profileScore);
        profilePpq = view.findViewById(R.id.profilePpq);
        editNameIcon = view.findViewById(R.id.editNameIcon);
        editDescriptionIcon = view.findViewById(R.id.editDescriptionIcon);
        savedQuestsButton = view.findViewById(R.id.savedQuestsButton);
        followersCount = view.findViewById(R.id.followersCount);
        followingCount = view.findViewById(R.id.followingCount);
        followersLayout = view.findViewById(R.id.followersLayout);
        followingLayout = view.findViewById(R.id.followingLayout);
        logoutIcon = view.findViewById(R.id.logoutIcon);

        if (currentUserId != null) {
            attachUserDataListener();
            attachCountListeners();
        }

        setupClickListeners();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (userListener != null) {
            userListener.remove();
        }
        if (followersListener != null) {
            followersListener.remove();
        }
        if (followingListener != null) {
            followingListener.remove();
        }
    }

    private void attachUserDataListener() {
        DocumentReference userRef = db.collection("users").document(currentUserId);
        userListener = userRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                profileName.setText(documentSnapshot.getString("username"));
                profileDescription.setText(documentSnapshot.getString("description"));

                if (documentSnapshot.contains("score")) {
                    profileScore.setText(String.format("Score: %d", documentSnapshot.getLong("score")));
                } else {
                    profileScore.setText("Score: ");
                }

                if (documentSnapshot.contains("ppq")) {
                    profilePpq.setText(String.format("%d ppq", documentSnapshot.getLong("ppq")));
                } else {
                    profilePpq.setText("0 ppq");
                }

                if (documentSnapshot.contains("profileImageUrl")) {
                    String imageUrl = documentSnapshot.getString("profileImageUrl");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
                        Glide.with(this).load(imageRef)
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .error(R.drawable.ic_launcher_foreground)
                                .centerCrop()
                                .into(profileImage);
                    }
                }
            } else {
                Log.d(TAG, "User document does not exist");
            }
        });
    }

    private void attachCountListeners() {
        // FOLLOWERS
        followersListener = db.collection("users").document(currentUserId).collection("followers")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    if (snapshots != null) {
                        followersCount.setText(String.valueOf(snapshots.size()));
                    }
                });

        // FOLLOWING
        followingListener = db.collection("users").document(currentUserId).collection("following")
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
        profileImage.setOnClickListener(v -> openGallery());

        editNameIcon.setOnClickListener(v -> toggleEdit(profileName, editNameIcon, "username"));
        editDescriptionIcon.setOnClickListener(v -> toggleEdit(profileDescription, editDescriptionIcon, "description"));

        savedQuestsButton.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_personalProfileFragment_to_questSavedFragment)
        );

        followersLayout.setOnClickListener(v -> {
            Bundle bundle = FollowListFragment.newBundle(currentUserId, "followers");
            NavHostFragment.findNavController(this).navigate(R.id.action_personalProfileFragment_to_followListFragment, bundle);
        });

        followingLayout.setOnClickListener(v -> {
            Bundle bundle = FollowListFragment.newBundle(currentUserId, "following");
            NavHostFragment.findNavController(this).navigate(R.id.action_personalProfileFragment_to_followListFragment, bundle);
        });

        logoutIcon.setOnClickListener(v -> logoutUser());
    }

    private void toggleEdit(EditText editText, ImageView icon, String field) {
        if (editText.isEnabled()) {
            editText.setEnabled(false);
            icon.setImageResource(R.drawable.ic_edit);
            updateUserField(field, editText.getText().toString());
        } else {
            editText.setEnabled(true);
            icon.setImageResource(R.drawable.ic_save);
            editText.requestFocus();
        }
    }

    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }


    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(galleryIntent);
    }

    private void uploadProfileImage() {
        if (localImageUri == null || currentUserId == null) return;
        StorageReference fileRef = storage.getReference().child("profile_images/" + currentUserId);
        fileRef.putFile(localImageUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    updateUserField("profileImageUrl", downloadUrl);
                    Toast.makeText(getContext(), "Profile image uploaded.", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateUserField(String field, Object value) {
        if (currentUserId == null) return;
        DocumentReference userRef = db.collection("users").document(currentUserId);
        Map<String, Object> updates = new HashMap<>();
        updates.put(field, value);
        userRef.update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User field '"+ field + "' updated successfully"))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating user field '"+ field + "'", e);
                    Toast.makeText(getContext(), "Failed to update " + field, Toast.LENGTH_SHORT).show();
                });
    }
}

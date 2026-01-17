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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.ui.start.StartActivity;

import java.util.HashMap;
import java.util.Map;

public class PersonalProfileFragment extends Fragment {

    private static final String TAG = "PersonalProfileFragment";
    private ShapeableImageView profileImage;
    private TextView profileName, profileDescription, profileScore, followersCount, followingCount;
    private ImageView editNameIcon, editDescriptionIcon, logoutIcon;
    private Button savedQuestsButton;
    private LinearLayout followersLayout, followingLayout;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private String currentUserId;

    private Uri localImageUri = null;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    localImageUri = result.getData().getData();
                    if (localImageUri != null) {
                        Glide.with(this).load(localImageUri).into(profileImage);
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

        // views
        profileImage = view.findViewById(R.id.profileImage);
        profileName = view.findViewById(R.id.profileName);
        profileDescription = view.findViewById(R.id.profileDescription);
        profileScore = view.findViewById(R.id.profileScore);
        editNameIcon = view.findViewById(R.id.editNameIcon);
        editDescriptionIcon = view.findViewById(R.id.editDescriptionIcon);
        savedQuestsButton = view.findViewById(R.id.savedQuestsButton);
        followersCount = view.findViewById(R.id.followersCount);
        followingCount = view.findViewById(R.id.followingCount);
        followersLayout = view.findViewById(R.id.followersLayout);
        followingLayout = view.findViewById(R.id.followingLayout);
        logoutIcon = view.findViewById(R.id.logoutIcon);

        if (currentUserId != null) {
            loadUserData();
            loadCounts();
        }

        setupClickListeners();
    }

    private void loadUserData() {
        DocumentReference userRef = db.collection("users").document(currentUserId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                profileName.setText(documentSnapshot.getString("username"));
                profileDescription.setText(documentSnapshot.getString("description"));

                if (documentSnapshot.contains("score")) {
                    profileScore.setText(String.format("Score: %d", documentSnapshot.getLong("score")));
                } else {
                    profileScore.setText("Score: 0");
                }

                if (documentSnapshot.contains("profileImageUrl")) {
                    String imageUrl = documentSnapshot.getString("profileImageUrl");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
                        Glide.with(this).load(imageRef)
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .error(R.drawable.ic_launcher_foreground)
                                .into(profileImage);
                    }
                }
            } else {
                Log.d(TAG, "User document does not exist");
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Error loading user data", e));
    }

    private void loadCounts() {
        // Followers
        db.collection("users").document(currentUserId).collection("followers")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> followersCount.setText(String.valueOf(queryDocumentSnapshots.size())));

        // Following
        db.collection("users").document(currentUserId).collection("following")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> followingCount.setText(String.valueOf(queryDocumentSnapshots.size())));
    }

    private void setupClickListeners() {
        profileImage.setOnClickListener(v -> openGallery());
        editNameIcon.setOnClickListener(v -> showEditDialog("Edit Username", profileName, "username"));
        editDescriptionIcon.setOnClickListener(v -> showEditDialog("Edit Description", profileDescription, "description"));

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

    private void showEditDialog(String title, TextView textViewToUpdate, String fieldToUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);

        final EditText input = new EditText(requireContext());
        input.setText(textViewToUpdate.getText());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newText = input.getText().toString().trim();
            if (!newText.isEmpty()) {
                textViewToUpdate.setText(newText);
                updateUserField(fieldToUpdate, newText);
                Toast.makeText(getContext(), title + " updated.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Field cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateUserField(String field, Object value) {
        if (currentUserId == null) return;
        DocumentReference userRef = db.collection("users").document(currentUserId);
        Map<String, Object> updates = new HashMap<>();
        updates.put(field, value);
        userRef.update(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User field '" + field + "' updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating user field '" + field + "'", e));
    }
}

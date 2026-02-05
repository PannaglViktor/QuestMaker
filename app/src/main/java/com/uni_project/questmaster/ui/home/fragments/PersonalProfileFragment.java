package com.uni_project.questmaster.ui.home.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.uni_project.questmaster.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PersonalProfileFragment extends Fragment {

    private ShapeableImageView profileImage;
    private TextView profileName;
    private TextView profileDescription;
    private ImageView editNameIcon;
    private ImageView editDescriptionIcon;
    private Button savedQuestsButton;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        Glide.with(this).load(imageUri).into(profileImage);
                        // Here you would typically upload the image to Firebase Storage
                        // and save the URL to the user's profile in Firestore.
                        Toast.makeText(getContext(), "Profile image updated (locally).", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_personal_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImage = view.findViewById(R.id.profileImage);
        profileName = view.findViewById(R.id.profileName);
        profileDescription = view.findViewById(R.id.profileDescription);
        editNameIcon = view.findViewById(R.id.editNameIcon);
        editDescriptionIcon = view.findViewById(R.id.editDescriptionIcon);
        savedQuestsButton = view.findViewById(R.id.savedQuestsButton);

        // TODO: Load user data from Firestore
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            profileName.setText(currentUser.getDisplayName());
            // Load description and profile image URL from Firestore
        }

        profileImage.setOnClickListener(v -> openGallery());
        editNameIcon.setOnClickListener(v -> showEditDialog("Edit Username", profileName));
        editDescriptionIcon.setOnClickListener(v -> showEditDialog("Edit Description", profileDescription));

        savedQuestsButton.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_personalProfileFragment_to_questSavedFragment)
        );
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(galleryIntent);
    }

    private void showEditDialog(String title, TextView textViewToUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title);

        final EditText input = new EditText(requireContext());
        input.setText(textViewToUpdate.getText());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newText = input.getText().toString().trim();
            if (!newText.isEmpty()) {
                textViewToUpdate.setText(newText);
                // TODO: Save the newText to Firestore
                Toast.makeText(getContext(), title + " updated.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Field cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}

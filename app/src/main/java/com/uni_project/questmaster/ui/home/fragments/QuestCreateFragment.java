package com.uni_project.questmaster.ui.home.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.model.Quest;
import com.uni_project.questmaster.model.QuestLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestCreateFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "QuestCreateFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private EditText editTextQuestTitle;
    private EditText editTextQuestDescription;
    private EditText editTextPpq;
    private Button buttonSaveQuest, buttonUploadImage;
    private ImageView imagePreview;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;

    private MapView mapView;
    private GoogleMap googleMap;
    private RadioGroup mapOptionsRadioGroup;
    private View locationFieldsContainer, mapCard;
    private EditText startingPoint, endPoint, singleLocation;
    private Button search_single_location, search_start_point, search_end_point;

    private QuestLocation startLocation, endLocation, singleQuestLocation;
    private Uri imageUri;
    private final List<String> imageUrls = new ArrayList<>();

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    imageUri = result.getData().getData();
                    imagePreview.setImageURI(imageUri);
                    imagePreview.setVisibility(View.VISIBLE);
                    uploadImage();
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quest_create, container, false);

        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        editTextQuestTitle = view.findViewById(R.id.edit_text_quest_name);
        editTextQuestDescription = view.findViewById(R.id.edit_text_quest_description);
        editTextPpq = view.findViewById(R.id.edit_text_ppq);
        buttonSaveQuest = view.findViewById(R.id.button_save_quest);
        buttonUploadImage = view.findViewById(R.id.button_upload_image);
        imagePreview = view.findViewById(R.id.image_preview);
        mapOptionsRadioGroup = view.findViewById(R.id.map_options_radiogroup);
        locationFieldsContainer = view.findViewById(R.id.location_fields_container);
        mapCard = view.findViewById(R.id.map_card);
        startingPoint = view.findViewById(R.id.startingPoint);
        endPoint = view.findViewById(R.id.endPoint);
        singleLocation = view.findViewById(R.id.singleLocation);
        search_single_location = view.findViewById(R.id.search_single_location);
        search_start_point = view.findViewById(R.id.search_start_point);
        search_end_point = view.findViewById(R.id.search_end_point);


        buttonSaveQuest.setOnClickListener(v -> saveQuest());
        buttonUploadImage.setOnClickListener(v -> openImagePicker());

        mapOptionsRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_no_map) {
                mapCard.setVisibility(View.GONE);
                locationFieldsContainer.setVisibility(View.GONE);
            } else if (checkedId == R.id.radio_single_location) {
                mapCard.setVisibility(View.VISIBLE);
                locationFieldsContainer.setVisibility(View.VISIBLE);
                singleLocation.setVisibility(View.VISIBLE);
                startingPoint.setVisibility(View.GONE);
                endPoint.setVisibility(View.GONE);
                search_single_location.setVisibility(View.VISIBLE);
                search_start_point.setVisibility(View.GONE);
                search_end_point.setVisibility(View.GONE);
            } else if (checkedId == R.id.radio_start_end) {
                mapCard.setVisibility(View.VISIBLE);
                locationFieldsContainer.setVisibility(View.VISIBLE);
                singleLocation.setVisibility(View.GONE);
                startingPoint.setVisibility(View.VISIBLE);
                endPoint.setVisibility(View.VISIBLE);
                search_single_location.setVisibility(View.GONE);
                search_start_point.setVisibility(View.VISIBLE);
                search_end_point.setVisibility(View.VISIBLE);
            }
        });

        search_single_location.setOnClickListener(v -> searchLocation(singleLocation.getText().toString(), "single"));
        search_start_point.setOnClickListener(v -> searchLocation(startingPoint.getText().toString(), "start"));
        search_end_point.setOnClickListener(v -> searchLocation(endPoint.getText().toString(), "end"));
    }

    private void searchLocation(String locationName, String type) {
        if (locationName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a location name", Toast.LENGTH_SHORT).show();
            return;
        }

        Geocoder geocoder = new Geocoder(getContext());
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                if (type.equals("single")) {
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Quest Location"));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    singleQuestLocation = new QuestLocation(latLng.latitude, latLng.longitude);
                    singleLocation.setText(address.getAddressLine(0));
                } else if (type.equals("start")) {
                    startLocation = new QuestLocation(latLng.latitude, latLng.longitude);
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Start Point"));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    startingPoint.setText(address.getAddressLine(0));
                } else if (type.equals("end")) {
                    endLocation = new QuestLocation(latLng.latitude, latLng.longitude);
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("End Point"));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    endPoint.setText(address.getAddressLine(0));
                }
            } else {
                Toast.makeText(getContext(), "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Geocoder service not available", Toast.LENGTH_SHORT).show();
        }
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(intent);
    }

    private void uploadImage() {
        if (imageUri != null) {
            buttonSaveQuest.setEnabled(false);
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("quest_images/" + UUID.randomUUID().toString());
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrls.add(uri.toString());
                        Toast.makeText(getContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                        buttonSaveQuest.setEnabled(true);
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        buttonSaveQuest.setEnabled(true);
                    });
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.setOnMapClickListener(latLng -> {
            int checkedId = mapOptionsRadioGroup.getCheckedRadioButtonId();
            if (checkedId == R.id.radio_single_location) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(latLng).title("Quest Location"));
                singleQuestLocation = new QuestLocation(latLng.latitude, latLng.longitude);
                singleLocation.setText(latLng.latitude + ", " + latLng.longitude);
            } else if (checkedId == R.id.radio_start_end) {
                if (startLocation == null) {
                    startLocation = new QuestLocation(latLng.latitude, latLng.longitude);
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Start Point"));
                    startingPoint.setText(latLng.latitude + ", " + latLng.longitude);
                } else if (endLocation == null) {
                    endLocation = new QuestLocation(latLng.latitude, latLng.longitude);
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("End Point"));
                    endPoint.setText(latLng.latitude + ", " + latLng.longitude);
                } else {
                    googleMap.clear();
                    startLocation = new QuestLocation(latLng.latitude, latLng.longitude);
                    endLocation = null;
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("Start Point"));
                    startingPoint.setText(latLng.latitude + ", " + latLng.longitude);
                    endPoint.setText("");
                }
            }
        });
    }

    private void saveQuest() {
        String title = editTextQuestTitle.getText().toString().trim();
        String description = editTextQuestDescription.getText().toString().trim();
        String ppqString = editTextPpq.getText().toString().trim();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "You have to be logged in to create a quest", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty() || description.isEmpty() || ppqString.isEmpty()) {
            Toast.makeText(getContext(), "Please, fill all the blanks", Toast.LENGTH_SHORT).show();
            return;
        }

        long ppq = Long.parseLong(ppqString);

        String ownerId = currentUser.getUid();
        String ownerName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Unknown user";

        Quest quest = new Quest();
        quest.setTitle(title);
        quest.setDescription(description);
        quest.setOwnerId(ownerId);
        quest.setOwnerName(ownerName);
        quest.setImageUrls(imageUrls);
        quest.setSavedBy(new ArrayList<>());
        quest.setPpq(ppq);

        int checkedId = mapOptionsRadioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radio_no_map) {
            // No location, so we can proceed to save
        } else if (checkedId == R.id.radio_single_location) {
            if (singleQuestLocation == null) {
                Toast.makeText(getContext(), "Please select a location on the map.", Toast.LENGTH_SHORT).show();
                return;
            }
            quest.setLocation(singleQuestLocation);
        } else if (checkedId == R.id.radio_start_end) {
            if (startLocation == null || endLocation == null) {
                Toast.makeText(getContext(), "Please select a start and end point on the map.", Toast.LENGTH_SHORT).show();
                return;
            }
            quest.setStartPoint(startLocation);
            quest.setEndPoint(endLocation);
        }

        db.collection("quests")
                .add(quest)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Quest saved with ID: " + documentReference.getId());
                    Toast.makeText(getContext(), "Quest successfully saved!", Toast.LENGTH_SHORT).show();

                    NavHostFragment.findNavController(QuestCreateFragment.this).navigateUp();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error in quest saving", e);
                    Toast.makeText(getContext(), "Error in saving", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
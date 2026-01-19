package com.uni_project.questmaster.ui.home.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uni_project.questmaster.R;
import com.uni_project.questmaster.model.QuestLocation;

import java.util.HashMap;
import java.util.Map;

public class QuestCreateFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "QuestCreateFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private EditText editTextQuestTitle;
    private EditText editTextQuestDescription;
    private Button buttonSaveQuest;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private MapView mapView;
    private GoogleMap googleMap;
    private RadioGroup mapOptionsRadioGroup;
    private View locationFieldsContainer, mapCard;
    private EditText startingPoint, endPoint, singleLocation;

    private QuestLocation startLocation, endLocation, singleQuestLocation;

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

        editTextQuestTitle = view.findViewById(R.id.edit_text_quest_name);
        editTextQuestDescription = view.findViewById(R.id.edit_text_quest_description);
        buttonSaveQuest = view.findViewById(R.id.button_save_quest);
        mapOptionsRadioGroup = view.findViewById(R.id.map_options_radiogroup);
        locationFieldsContainer = view.findViewById(R.id.location_fields_container);
        mapCard = view.findViewById(R.id.map_card);
        startingPoint = view.findViewById(R.id.startingPoint);
        endPoint = view.findViewById(R.id.endPoint);
        singleLocation = view.findViewById(R.id.singleLocation);

        buttonSaveQuest.setOnClickListener(v -> saveQuest());

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
            } else if (checkedId == R.id.radio_start_end) {
                mapCard.setVisibility(View.VISIBLE);
                locationFieldsContainer.setVisibility(View.VISIBLE);
                singleLocation.setVisibility(View.GONE);
                startingPoint.setVisibility(View.VISIBLE);
                endPoint.setVisibility(View.VISIBLE);
            }
        });
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
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "You have to be logged in to create a quest", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Please, fill all the blanks", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> questData = new HashMap<>();
        questData.put("title", title);
        questData.put("description", description);
        questData.put("timestamp", FieldValue.serverTimestamp());
        questData.put("ownerId", currentUser.getUid());
        questData.put("ownerName", currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Unknown user");

        int checkedId = mapOptionsRadioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radio_single_location) {
            questData.put("location", singleQuestLocation);
        } else if (checkedId == R.id.radio_start_end) {
            questData.put("startPoint", startLocation);
            questData.put("endPoint", endLocation);
        }

        db.collection("quests")
                .add(questData)
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

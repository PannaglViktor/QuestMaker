package com.uni_project.questmaster.ui.home.fragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.uni_project.questmaster.R;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsFragment extends Fragment {

    private MaterialSwitch switchDarkMode, switchNotificationsNewQuests, switchNotificationsComments;
    private LinearLayout settingLanguage, settingLocation, settingAppInfo;
    private TextView textViewCurrentLanguage, textViewCurrentLocation, textViewAppVersion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupClickListeners();
        loadCurrentSettings();
    }

    private void initializeViews(View view) {
        // Switches
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        switchNotificationsNewQuests = view.findViewById(R.id.switch_notifications_new_quests);
        switchNotificationsComments = view.findViewById(R.id.switch_notifications_comments);

        // Clickable LinearLayouts
        settingLanguage = view.findViewById(R.id.setting_language);
        settingLocation = view.findViewById(R.id.setting_location);
        settingAppInfo = view.findViewById(R.id.setting_app_info);

        // TextViews for values
        textViewCurrentLanguage = view.findViewById(R.id.text_view_current_language);
        textViewCurrentLocation = view.findViewById(R.id.text_view_current_location);
        textViewAppVersion = view.findViewById(R.id.text_view_app_version);
    }

    private void setupClickListeners() {
        // Dark Mode Switch
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Language Setting
        settingLanguage.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Language selection clicked", Toast.LENGTH_SHORT).show();
        });

        // Location Setting
        settingLocation.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Location selection clicked", Toast.LENGTH_SHORT).show();
        });

        // App Info
        settingAppInfo.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Showing app info...", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadCurrentSettings() {
        // Load Dark Mode state
        int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        switchDarkMode.setChecked(currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES);

        // Load saved language and display it
        textViewCurrentLanguage.setText("English");

        // Load saved location and display it
        textViewCurrentLocation.setText("United Kingdom, London");

        // Load App Version
        try {
            PackageInfo pInfo = requireActivity().getPackageManager().getPackageInfo(requireActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            textViewAppVersion.setText("Version " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            textViewAppVersion.setText("Version not found");
        }

        // Load saved notification preferences
        switchNotificationsNewQuests.setChecked(true);
        switchNotificationsComments.setChecked(true);
    }
}

package com.uni_project.questmaster.ui.home;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.uni_project.questmaster.R;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsActivity extends AppCompatActivity {

    private MaterialSwitch switchDarkMode, switchNotificationsNewQuests, switchNotificationsComments;
    private LinearLayout settingLanguage, settingLocation, settingAppInfo;
    private TextView textViewCurrentLanguage, textViewCurrentLocation, textViewAppVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeViews();
        setupClickListeners();
        loadCurrentSettings();
    }

    private void initializeViews() {
        // Switches
        switchDarkMode = findViewById(R.id.switch_dark_mode);
        switchNotificationsNewQuests = findViewById(R.id.switch_notifications_new_quests);
        switchNotificationsComments = findViewById(R.id.switch_notifications_comments);

        // Clickable LinearLayouts
        settingLanguage = findViewById(R.id.setting_language);
        settingLocation = findViewById(R.id.setting_location);
        settingAppInfo = findViewById(R.id.setting_app_info);

        // TextViews for values
        textViewCurrentLanguage = findViewById(R.id.text_view_current_language);
        textViewCurrentLocation = findViewById(R.id.text_view_current_location);
        textViewAppVersion = findViewById(R.id.text_view_app_version);
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
            // TODO: Open a dialog or new screen to select the language
            Toast.makeText(this, "Language selection clicked", Toast.LENGTH_SHORT).show();
        });

        // Location Setting
        settingLocation.setOnClickListener(v -> {
            // TODO: Open a dialog or new screen to select location
            Toast.makeText(this, "Location selection clicked", Toast.LENGTH_SHORT).show();
        });

        // App Info
        settingAppInfo.setOnClickListener(v -> {
            Toast.makeText(this, "Showing app info...", Toast.LENGTH_SHORT).show();
            // You can also open a dedicated "About" dialog here.
        });
    }

    private void loadCurrentSettings() {
        // Load Dark Mode state
        int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        switchDarkMode.setChecked(currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES);

        // TODO: Load saved language and display it
        textViewCurrentLanguage.setText("English");

        // TODO: Load saved location and display it
        textViewCurrentLocation.setText("United Kingdom, London");

        // Load App Version
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            textViewAppVersion.setText("Version " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            textViewAppVersion.setText("Version not found");
        }

        // TODO: Load saved notification preferences
        switchNotificationsNewQuests.setChecked(true);
        switchNotificationsComments.setChecked(true);
    }
}

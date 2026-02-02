package com.uni_project.questmaster.ui.home.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.uni_project.questmaster.R;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private MaterialSwitch switchDarkMode, switchNotificationsNewQuests, switchNotificationsComments;
    private LinearLayout settingLanguage, settingLocation, settingAppInfo, settingClearSearchHistory;
    private TextView textViewCurrentLanguage, textViewCurrentLocation, textViewAppVersion;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        initializeViews(view);
        setupClickListeners();
        loadCurrentSettings();
    }

    private void initializeViews(View view) {
        // Switches
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        switchNotificationsNewQuests = view.findViewById(R.id.switch_notifications_new_quests);
        switchNotificationsComments = view.findViewById(R.id.switch_notifications_comments);

        // LinearLayouts
        settingLanguage = view.findViewById(R.id.setting_language);
        settingLocation = view.findViewById(R.id.setting_location);
        settingAppInfo = view.findViewById(R.id.setting_app_info);
        settingClearSearchHistory = view.findViewById(R.id.setting_clear_search_history);

        // TextViews
        textViewCurrentLanguage = view.findViewById(R.id.text_view_current_language);
        textViewCurrentLocation = view.findViewById(R.id.text_view_current_location);
        textViewAppVersion = view.findViewById(R.id.text_view_app_version);
    }

    private void setupClickListeners() {
        // Dark Mode
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        switchNotificationsNewQuests.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveNotificationPreference("new_quests", isChecked);
        });

        switchNotificationsComments.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveNotificationPreference("comments", isChecked);
        });

        // Language Setting
        settingLanguage.setOnClickListener(v -> {
            showLanguageSelectionDialog();
        });

        // Location Setting
        settingLocation.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_settingsFragment_to_locationSelectionFragment);
        });

        // App Info
        settingAppInfo.setOnClickListener(v -> {
            showAppInfoDialog();
        });

        // Clear Search History
        settingClearSearchHistory.setOnClickListener(v -> {
            showClearSearchHistoryDialog();
        });
    }

    private void loadCurrentSettings() {
        // Dark Mode state
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode",
                (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES);
        switchDarkMode.setChecked(isDarkMode);

        // Load saved language
        String language = sharedPreferences.getString("My_Lang", "en");
        Locale locale = new Locale(language);
        textViewCurrentLanguage.setText(locale.getDisplayLanguage());


        // Load saved location
        String location = sharedPreferences.getString("My_Location", "United Kingdom");
        textViewCurrentLocation.setText(location);

        // Load App Version
        try {
            PackageInfo pInfo = requireActivity().getPackageManager().getPackageInfo(requireActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            textViewAppVersion.setText("Version " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            textViewAppVersion.setText("Version not found");
        }

        // Load saved notification
        switchNotificationsNewQuests.setChecked(sharedPreferences.getBoolean("new_quests", true));
        switchNotificationsComments.setChecked(sharedPreferences.getBoolean("comments", true));
    }

    private void showLanguageSelectionDialog() {
        final String[] languages = {"English", "Italiano", "Française", "Deutsch", "Español"};
        final String[] languageCodes = {"en", "it", "fr", "de", "es"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Language")
                .setItems(languages, (dialog, which) -> {
                    setLocale(languageCodes[which]);
                });
        builder.create().show();
    }

    private void showAppInfoDialog() {
        String versionName = "";
        try {
            PackageInfo pInfo = requireActivity().getPackageManager().getPackageInfo(requireActivity().getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.app_info)
                .setMessage("Version " + versionName)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void showClearSearchHistoryDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.clear_search_history)
                .setMessage(R.string.clear_search_history_dialog_message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    clearSearchHistory();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void clearSearchHistory() {
        SharedPreferences searchHistoryPrefs = requireActivity().getSharedPreferences("SearchHistory", Context.MODE_PRIVATE);
        searchHistoryPrefs.edit().clear().apply();
        Toast.makeText(requireContext(), getString(R.string.history_cleared), Toast.LENGTH_SHORT).show();
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Save locale to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("My_Lang", languageCode);
        editor.apply();
        requireActivity().recreate();
    }

    private void saveNotificationPreference(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}

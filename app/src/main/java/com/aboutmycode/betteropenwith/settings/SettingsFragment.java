package com.aboutmycode.betteropenwith.settings;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.aboutmycode.betteropenwith.R;
import com.aboutmycode.betteropenwith.common.YesNoDialogFragment;

import java.util.prefs.Preferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        toggleColumnsPreference(getPreferenceScreen().getSharedPreferences());
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (key.equals("layout")) {
            toggleColumnsPreference(preferences);
        }

        if ("pref_lang".equals(key)) {
            Activity activity = getActivity();

            if (activity == null) {
                return;
            }

            YesNoDialogFragment dialog = YesNoDialogFragment.newInstance(activity.getString(R.string.restartTitle), activity.getString(R.string.localeRestart), false);
            dialog.show(getFragmentManager(), "Dialog");
        }
    }

    private void toggleColumnsPreference(SharedPreferences preferences) {
        Resources resources = getResources();
        Preference gridColumnsPreference = getPreferenceScreen().findPreference("gridColumns");

        if (preferences.getString("layout", resources.getString(R.string.listValue)).equals(resources.getString(R.string.listValue))) {
            gridColumnsPreference.setEnabled(false);
        } else {
            gridColumnsPreference.setEnabled(true);
        }
    }
}
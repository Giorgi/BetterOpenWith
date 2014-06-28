package com.aboutmycode.betteropenwith.settings;



import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceFragment;

import com.aboutmycode.betteropenwith.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class SettingsFragment extends PreferenceFragment {
    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}

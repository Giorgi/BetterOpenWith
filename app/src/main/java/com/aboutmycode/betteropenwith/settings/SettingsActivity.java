package com.aboutmycode.betteropenwith.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.aboutmycode.betteropenwith.common.YesNoListener;
import com.aboutmycode.betteropenwith.common.baseActivities.LocaleAwareActivity;

import java.util.Locale;

public class SettingsActivity extends LocaleAwareActivity implements YesNoListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public void yesClicked() {

    }
}
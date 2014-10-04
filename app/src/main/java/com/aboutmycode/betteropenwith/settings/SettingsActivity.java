package com.aboutmycode.betteropenwith.settings;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.aboutmycode.betteropenwith.common.YesNoListener;

import java.util.Locale;

public class SettingsActivity extends Activity implements YesNoListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSelectedLocale();

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    private void setSelectedLocale() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String language = preferences.getString("pref_lang", "null");

        if ("null".equalsIgnoreCase(language)) {
            language = Locale.getDefault().getLanguage();
        }

        Locale locale = new Locale(language);

        Resources resources = getBaseContext().getResources();
        Configuration config = resources.getConfiguration();
        config.locale = locale;

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    @Override
    public void yesClicked() {

    }
}
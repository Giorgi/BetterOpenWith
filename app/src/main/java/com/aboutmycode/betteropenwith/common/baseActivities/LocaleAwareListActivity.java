package com.aboutmycode.betteropenwith.common.baseActivities;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.util.Locale;

public abstract class LocaleAwareListActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSelectedLocale();
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
}
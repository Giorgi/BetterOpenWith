package com.aboutmycode.betteropenwith.settings;

import android.app.Activity;
import android.os.Bundle;

import com.aboutmycode.betteropenwith.common.YesNoListener;

public class SettingsActivity extends Activity implements YesNoListener {
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
package com.aboutmycode.betteropenwith;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.aboutmycode.betteropenwith.R;

public class BrowserDetailsActivity extends HandlerDetailsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browser_details, menu);
        return true;
    }
}

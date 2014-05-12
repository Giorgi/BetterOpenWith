package com.aboutmycode.openwith.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class FileHandlerActivity extends ListActivity {

    private ResolveInfoAdapter adapter;
    private Intent original = new Intent();
    private Timer autoStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.complete_action_with));

        Intent launchIntent = getIntent();

        original = makeMyIntent();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(launchIntent.getData(), launchIntent.getType());

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> resInfo = packageManager.queryIntentActivities(intent, 0);

        if (resInfo.size() < 1) {
            Toast.makeText(this, "No application found to open the selected file", Toast.LENGTH_LONG).show();
            finish();
        }

        if (resInfo.size() == 1) {
            startIntentFromResolveInfo(resInfo.get(0));
            finish();
        }

        Collections.sort(resInfo, new ResolveInfo.DisplayNameComparator(packageManager));

        List<ResolveInfoDisplay> list = new ArrayList<ResolveInfoDisplay>();

        for (ResolveInfo item : resInfo) {
            if (item.activityInfo.packageName.equals(getPackageName())) {
                continue;
            }

            ResolveInfoDisplay resolveInfoDisplay = new ResolveInfoDisplay();
            resolveInfoDisplay.setDisplayLabel(item.loadLabel(packageManager));
            resolveInfoDisplay.setDisplayIcon(item.loadIcon(packageManager));
            resolveInfoDisplay.setResolveInfo(item);

            list.add(resolveInfoDisplay);
        }

        adapter = new ResolveInfoAdapter(this, android.R.id.text1, list);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView listView, View v, int position, long id) {
        startSelectedItem(listView, position);
    }

    private void startSelectedItem(ListView listView, int position) {
        ResolveInfoDisplay item = (ResolveInfoDisplay) listView.getItemAtPosition(position);

        ResolveInfo resolveInfo = item.getResolveInfo();
        startIntentFromResolveInfo(resolveInfo);
    }

    private void startIntentFromResolveInfo(ResolveInfo resolveInfo) {
        autoStart.cancel();

        ActivityInfo ai = resolveInfo.activityInfo;

        Intent intent = new Intent(original);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.setComponent(new ComponentName(ai.applicationInfo.packageName, ai.name));

        startActivity(intent);
        finish();
    }

    private Intent makeMyIntent() {
        Intent intent = new Intent(getIntent());
        intent.setComponent(null);
        intent.setFlags(intent.getFlags() & ~Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        return intent;
    }

    @Override
    protected void onStart() {
        super.onStart();

        int timeout = PreferenceManager.getDefaultSharedPreferences(this).getInt("timeout", 4);

        autoStart = new Timer("AutoStart");
        autoStart.schedule(new TimerTask() {
            @Override
            public void run() {
                startSelectedItem(getListView(), 0);
            }
        }, timeout * 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            autoStart.cancel();
        }
    }
}

package com.aboutmycode.openwith.app;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class FileHandlerActivity extends ListActivity {
    private Timer autoStart;

    private Button pauseButton;
    private TextView secondsTextView;

    private int elapsed;
    private int timeout;
    private boolean paused;

    private ResolveInfoAdapter adapter;
    private Intent original = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            elapsed = savedInstanceState.getInt("elapsed", 0);
            paused = savedInstanceState.getBoolean("paused", false);
        }

        setContentView(R.layout.file_handler);
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

        ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setItemChecked(0, true);

        secondsTextView = (TextView) findViewById(R.id.secondsTextView);
        pauseButton = (Button) findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoStart.cancel();
                paused = true;
                secondsTextView.setText("Paused");
            }
        });
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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("elapsed", elapsed);
        outState.putBoolean("paused", paused);
        super.onSaveInstanceState(outState);

        if (isFinishing() || isChangingConfigurations() && autoStart != null) {
            autoStart.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        timeout = PreferenceManager.getDefaultSharedPreferences(this).getInt("timeout", 4);

        if (paused){
            secondsTextView.setText("Paused");
        }else {
            secondsTextView.setText(String.format("Launching in %s seconds", timeout - elapsed));
        }

        if (autoStart == null && !paused) {
            autoStart = new Timer("AutoStart");
            autoStart.schedule(new TimerTask() {
                @Override
                public void run() {
                    elapsed++;

                    if (elapsed == timeout) {
                        startSelectedItem(getListView(), 0);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                secondsTextView.setText(String.format("Launching in %s seconds", timeout - elapsed));
                            }
                        });
                    }
                }
            }, 1000, 1000);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isFinishing() && autoStart != null) {
            autoStart.cancel();
        }
    }
}

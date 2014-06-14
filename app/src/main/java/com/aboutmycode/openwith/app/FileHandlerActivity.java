package com.aboutmycode.openwith.app;

import android.app.ActivityManager;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aboutmycode.openwith.app.common.adapter.CommonAdapter;
import com.aboutmycode.openwith.app.common.adapter.IBindView;
import com.aboutmycode.openwith.app.database.CupboardSQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class FileHandlerActivity extends ListActivity {
    private Timer autoStart;

    private Button pauseButton;
    private TextView secondsTextView;

    private int elapsed;
    private int timeout;
    private boolean paused;

    private CommonAdapter<ResolveInfoDisplay> adapter;
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

        int id = -1;

        PackageManager packageManager = getPackageManager();
        try {
            ActivityInfo activityInfo = packageManager.getActivityInfo(getComponentName(), PackageManager.GET_META_DATA | PackageManager.GET_ACTIVITIES);
            id = activityInfo.metaData.getInt("id", -1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        HandleItem item = getHandleItem(id);

        List<ResolveInfo> resInfo = packageManager.queryIntentActivities(intent, 0);

        //If only one app is found it is us so there is no other app.
        if (resInfo.size() == 1) {
            Toast.makeText(this, "No application found to open the selected file", Toast.LENGTH_LONG).show();
            finish();
        }

        //If there are two apps start the other one.
        if (resInfo.size() == 2) {
            ResolveInfo resolveInfo = resInfo.get(0);

            if (resolveInfo.activityInfo.packageName.equals(getPackageName())) {
                startIntentFromResolveInfo(resInfo.get(1));
            } else {
                startIntentFromResolveInfo(resolveInfo);
            }
            finish();
        }

        Collections.sort(resInfo, new ResolveInfo.DisplayNameComparator(packageManager));

        List<ResolveInfoDisplay> list = new ArrayList<ResolveInfoDisplay>();

        int checked = -1;
        int index = -1;

        for (ResolveInfo info : resInfo) {
            if (info.activityInfo.packageName.equals(getPackageName())) {
                continue;
            }

            index++;

            if (info.activityInfo.packageName.equals(item.getPackageName()) && info.activityInfo.name.equals(item.getClassName())) {
                if (item.isSkipList()) {
                    startIntentFromResolveInfo(info);
                    return;
                } else {
                    checked = index;
                }
            }

            ResolveInfoDisplay resolveInfoDisplay = new ResolveInfoDisplay();
            resolveInfoDisplay.setDisplayLabel(info.loadLabel(packageManager));
            resolveInfoDisplay.setDisplayIcon(info.loadIcon(packageManager));
            resolveInfoDisplay.setResolveInfo(info);

            list.add(resolveInfoDisplay);
        }

        adapter = new CommonAdapter<ResolveInfoDisplay>(this, list, R.layout.resolve_list_item, new ResolveInfoDisplayFileHandlerViewBinder());
        setListAdapter(adapter);

        ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        if (checked >= 0) {
            listView.setItemChecked(checked, true);
        } else {
            listView.setItemChecked(0, true);
        }

        secondsTextView = (TextView) findViewById(R.id.secondsTextView);
        pauseButton = (Button) findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paused) {
                    configureTimer();
                    pauseButton.setText(getString(R.string.pause));
                } else {
                    autoStart.cancel();
                    pauseButton.setText(getString(R.string.resume));
                }

                paused = !paused;
                showTimerStatus();
            }
        });

        final long finalId = id;

        findViewById(R.id.settingsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainScreen = new Intent(FileHandlerActivity.this, HandlerDetailsActivity.class);
                mainScreen.putExtra("id", finalId);
                startActivity(mainScreen);
            }
        });
    }

    private HandleItem getHandleItem(int id) {
        CupboardSQLiteOpenHelper dbHelper = new CupboardSQLiteOpenHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        HandleItem item = cupboard().withDatabase(database).get(HandleItem.class, id);

        database.close();
        dbHelper.close();
        return item;
    }

    @Override
    protected void onListItemClick(ListView listView, View v, int position, long id) {
        ResolveInfoDisplay item = adapter.getItem(position);
        ResolveInfo resolveInfo = item.getResolveInfo();

        startIntentFromResolveInfo(resolveInfo);
    }

    private void startIntentFromResolveInfo(ResolveInfo resolveInfo) {
        if (autoStart != null) {
            autoStart.cancel();
        }

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

        showTimerStatus();

        if (autoStart == null && !paused) {
            configureTimer();
        }
    }

    private void showTimerStatus() {
        if (paused) {
            secondsTextView.setText(getString(R.string.paused));
            pauseButton.setText(getString(R.string.resume));
        } else {
            secondsTextView.setText(String.format(getString(R.string.launching_in), timeout - elapsed));
            pauseButton.setText(getString(R.string.pause));
        }
    }

    private void configureTimer() {
        autoStart = new Timer("AutoStart");
        autoStart.schedule(new TimerTask() {
            @Override
            public void run() {
                elapsed++;

                if (elapsed == timeout) {
                    int checkedItemPosition = getListView().getCheckedItemPosition();
                    if (checkedItemPosition >= 0) {
                        ResolveInfoDisplay item = adapter.getItem(checkedItemPosition);
                        startIntentFromResolveInfo(item.getResolveInfo());
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            secondsTextView.setText(String.format(getString(R.string.launching_in), timeout - elapsed));
                        }
                    });
                }
            }
        }, 1000, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (isFinishing() && autoStart != null) {
            autoStart.cancel();
        }
    }
}

class ResolveInfoDisplayFileHandlerViewBinder implements IBindView<ResolveInfoDisplay> {

    @Override
    public View bind(View row, ResolveInfoDisplay item, Context context) {
        Object tag = row.getTag();
        if (tag == null) {
            final ViewHolder holder = new ViewHolder(row);
            row.setTag(holder);

            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

            ViewGroup.LayoutParams lp = holder.icon.getLayoutParams();
            lp.width = lp.height = am.getLauncherLargeIconSize();

            tag = holder;
        }

        ViewHolder holder = (ViewHolder) tag;

        holder.text.setText(item.getDisplayLabel());
        holder.icon.setImageDrawable(item.getDisplayIcon());

        return row;
    }
}

class ViewHolder {
    public TextView text;
    public ImageView icon;

    public ViewHolder(View view) {
        text = (TextView) view.findViewById(android.R.id.text1);
        icon = (ImageView) view.findViewById(R.id.targetIcon);
    }
}
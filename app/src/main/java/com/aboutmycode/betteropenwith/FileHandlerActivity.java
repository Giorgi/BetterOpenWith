package com.aboutmycode.betteropenwith;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aboutmycode.betteropenwith.common.adapter.CommonAdapter;
import com.aboutmycode.betteropenwith.common.adapter.IBindView;
import com.aboutmycode.betteropenwith.database.CupboardSQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class FileHandlerActivity extends Activity implements AdapterView.OnItemClickListener {
    private Timer autoStart;

    private Button pauseButton;
    private TextView secondsTextView;

    private int elapsed;
    private int timeout;
    private boolean paused;

    private CommonAdapter<ResolveInfoDisplay> adapter;
    private Intent original = new Intent();
    private AbsListView adapterView;
    private ItemBase item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSelectedLocale();

        if (savedInstanceState != null) {
            elapsed = savedInstanceState.getInt("elapsed", 0);
            paused = savedInstanceState.getBoolean("paused", false);
        }
        Resources resources = getResources();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isLight;

        if (preferences.getString("theme", resources.getString(R.string.lightValue)).equals(resources.getString(R.string.lightValue))) {
            isLight = true;
            setTheme(android.R.style.Theme_Holo_Light_Dialog);
        } else {
            isLight = false;
            setTheme(android.R.style.Theme_Holo_Dialog);
        }

        setContentView(R.layout.file_handler);
        setTitle(getString(R.string.complete_action_with));

        View list = findViewById(R.id.listInclude);
        View grid = findViewById(R.id.gridView);

        if (preferences.getString("layout", resources.getString(R.string.listValue)).equals(resources.getString(R.string.listValue))) {
            grid.setVisibility(View.GONE);
            adapterView = (AbsListView) findViewById(android.R.id.list);
        } else {
            list.setVisibility(View.GONE);
            adapterView = (AbsListView) grid;
            ((GridView) grid).setNumColumns(preferences.getInt("gridColumns", resources.getInteger(R.integer.default_columns)));
        }

        Intent launchIntent = getIntent();

        original = makeMyIntent();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(launchIntent.getData(), launchIntent.getType());

        item = getCurrentItem(launchIntent);

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> resInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

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

        List<ResolveInfoDisplay> data = new ArrayList<ResolveInfoDisplay>();

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

            //if preferred app isn't set select the last used app
            if (TextUtils.isEmpty(item.getPackageName()) &&
                    info.activityInfo.packageName.equals(item.getLastPackageName()) && info.activityInfo.name.equals(item.getLastClassName())) {
                checked = index;
            }

            ResolveInfoDisplay resolveInfoDisplay = new ResolveInfoDisplay();
            resolveInfoDisplay.setDisplayLabel(info.loadLabel(packageManager));
            resolveInfoDisplay.setDisplayIcon(info.loadIcon(packageManager));
            resolveInfoDisplay.setResolveInfo(info);

            data.add(resolveInfoDisplay);
        }

        adapter = new CommonAdapter<ResolveInfoDisplay>(this, data, R.layout.resolve_list_item, new ResolveInfoDisplayFileHandlerViewBinder(this));
        adapterView.setAdapter(adapter);

        adapterView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        if (checked >= 0) {
            adapterView.setItemChecked(checked, true);
            adapterView.setSelection(checked);
        } else {
            adapterView.setItemChecked(0, true);
        }

        adapterView.setOnItemClickListener(this);

        secondsTextView = (TextView) findViewById(R.id.secondsTextView);
        pauseButton = (Button) findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTimer();
            }
        });

        ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsButton);

        settingsButton.setImageResource(isLight ? R.drawable.ic_action_settings_dark : R.drawable.ic_action_settings);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseTimer();
                showTimerStatus();

                launchDetailsActivity();
            }
        });
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

    private void launchDetailsActivity() {
        Intent detailsScreenIntent = getDetailsScreenIntent(this.item);
        detailsScreenIntent.putExtra("id", getItemId());
        startActivity(detailsScreenIntent);
    }

    private void toggleTimer() {
        if (paused) {
            configureTimer();
            pauseButton.setText(getString(R.string.pause));
            paused = false;
        } else {
            pauseTimer();
        }

        showTimerStatus();
    }

    private void pauseTimer() {
        autoStart.cancel();
        pauseButton.setText(getString(R.string.resume));
        paused = true;
    }

    protected Intent getDetailsScreenIntent(ItemBase item) {
        Intent mainScreen = new Intent(this, HandlerDetailsActivity.class);
        return mainScreen;
    }

    private HandleItem getHandleItem(long id) {
        CupboardSQLiteOpenHelper dbHelper = new CupboardSQLiteOpenHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        HandleItem item = cupboard().withDatabase(database).get(HandleItem.class, id);

        database.close();
        dbHelper.close();
        return item;
    }

    protected ItemBase getCurrentItem(Intent intent) {
        long id = getItemId();

        return getHandleItem(id);
    }

    private long getItemId() {
        PackageManager packageManager = getPackageManager();
        try {
            ActivityInfo activityInfo = packageManager.getActivityInfo(getComponentName(), PackageManager.GET_META_DATA | PackageManager.GET_ACTIVITIES);
            return activityInfo.metaData.getInt("id", -1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        ResolveInfo resolveInfo = adapter.getItem(position).getResolveInfo();

        updateLastApp(item, resolveInfo.activityInfo);
        startIntentFromResolveInfo(resolveInfo);
    }

    protected void updateLastApp(ItemBase item, ActivityInfo activityInfo) {
        item.setLastClassName(activityInfo.name);
        item.setLastPackageName(activityInfo.applicationInfo.packageName);

        CupboardSQLiteOpenHelper dbHelper = new CupboardSQLiteOpenHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        cupboard().withDatabase(database).put(item);

        database.close();
        dbHelper.close();
    }

    private void startIntentFromResolveInfo(ResolveInfo resolveInfo) {
        if (autoStart != null) {
            autoStart.cancel();
        }

        ActivityInfo ai = resolveInfo.activityInfo;

        Intent intent = new Intent(original);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.setComponent(new ComponentName(ai.applicationInfo.packageName, ai.name));

        try {
            startActivity(intent);
        } catch (SecurityException e) {
            intent.setComponent(null);
            intent.setPackage(ai.packageName);
            startActivity(intent);
        }
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

        if (item.isUseGlobalTimeout()) {
            timeout = PreferenceManager.getDefaultSharedPreferences(this).getInt("timeout", getResources().getInteger(R.integer.default_timeout));
        } else {
            timeout = item.getCustomTimeout();
        }

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
                    int checkedItemPosition = adapterView.getCheckedItemPosition();
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
    private boolean hideText;
    private boolean smallText;

    ResolveInfoDisplayFileHandlerViewBinder(Context context) {
        Context applicationContext = context.getApplicationContext();

        Resources resources = applicationContext.getResources();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        hideText = preferences.getBoolean("iconOnly", false);
        smallText = !preferences.getString("size", resources.getString(R.string.mediumValue)).equals(resources.getString(R.string.mediumValue));
    }

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

        if (hideText) {
            holder.text.setVisibility(View.GONE);
        }

        if (smallText) {
            holder.text.setTextAppearance(context, android.R.style.TextAppearance_Small);
        } else {
            holder.text.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        }


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
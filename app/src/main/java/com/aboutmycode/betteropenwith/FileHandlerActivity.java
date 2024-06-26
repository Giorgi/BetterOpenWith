package com.aboutmycode.betteropenwith;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aboutmycode.betteropenwith.common.adapter.CommonAdapter;
import com.aboutmycode.betteropenwith.common.adapter.IBindView;
import com.aboutmycode.betteropenwith.common.baseActivities.LocaleAwareActivity;
import com.aboutmycode.betteropenwith.database.CupboardSQLiteOpenHelper;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;
import com.todddavies.components.progressbar.ProgressWheel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;


public class FileHandlerActivity extends LocaleAwareActivity implements AdapterView.OnItemClickListener {
    private Timer autoStart;

    private ImageButton pauseButton;

    private int elapsed;
    private int timeout;
    private boolean paused;

    private CommonAdapter<ResolveInfoDisplay> adapter;
    private Intent original = new Intent();
    private AbsListView adapterView;
    private ItemBase item;
    private boolean isLight;
    private ProgressWheel timerCountDown;
    private TickerView tickerView;
    private boolean disableTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            elapsed = savedInstanceState.getInt("elapsed", 0);
            paused = savedInstanceState.getBoolean("paused", false);
        }

        Resources resources = getResources();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        int lightTheme = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                android.R.style.Theme_Material_Light_Dialog : android.R.style.Theme_Holo_Light_Dialog;

        int darkTheme = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                android.R.style.Theme_Material_Dialog : android.R.style.Theme_Holo_Dialog;

        isLight = preferences.getString("theme", resources.getString(R.string.lightValue)).equals(resources.getString(R.string.lightValue));

        setTheme(isLight ? lightTheme : darkTheme);

        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        if (preferences.getString("position", resources.getString(R.string.bottomValue)).equals(resources.getString(R.string.bottomValue))) {
            wlp.gravity = Gravity.BOTTOM;
        }
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        setContentView(R.layout.file_handler);
        setTitle(getString(R.string.complete_action_with));

        TypedArray array = getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorBackground,});

        window.setBackgroundDrawable(null);
        window.getDecorView().setBackgroundColor(array.getColor(0, 0xFF00FF));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        array.recycle();

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

        if (prepareDataAndLaunch()) {
            return;
        }

        disableTimer = preferences.getBoolean("disableTimer", false);

        if (disableTimer) {
            findViewById(R.id.timerFooter).setVisibility(View.GONE);
        } else {
            timerCountDown = (ProgressWheel) findViewById(R.id.timerCountDown);
            tickerView = (TickerView) findViewById(R.id.tickerView);
            tickerView.setCharacterList(TickerUtils.getDefaultNumberList());

            pauseButton = (ImageButton) findViewById(R.id.pauseButton);
            pauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleTimer();
                }
            });

            ImageButton settingsButton = (ImageButton) findViewById(R.id.settingsButton);

            settingsButton.setImageResource(isLight ? R.drawable.ic_settings_grey600_36dp : R.drawable.ic_settings_white_36dp);
            pauseButton.setImageResource(isLight ? R.drawable.ic_pause_circle_outline_grey600_36dp : R.drawable.ic_pause_circle_outline_white_36dp);

            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pauseTimer();
                    showTimerStatus();

                    launchDetailsActivity();
                }
            });
        }

        int slideAnimation = R.anim.slide_bottom_up;
        String animation = preferences.getString("animation", resources.getString(R.string.fromBottomValue));

        if (animation.equals(resources.getString(R.string.fromLeftValue))) {
            slideAnimation = R.anim.slide_left_right;
        }

        if (animation.equals(resources.getString(R.string.fromRightValue))) {
            slideAnimation = R.anim.slide_right_left;
        }

        if (animation.equals(resources.getString(R.string.noneValue))) {
            slideAnimation = 0;
        }

        overridePendingTransition(slideAnimation, 0);
    }

    private boolean prepareDataAndLaunch() {
        PackageManager packageManager = getPackageManager();

        Intent launchIntent = getIntent();

        original = makeMyIntent();

        Intent intent = new Intent(launchIntent.getAction());
        intent.setDataAndType(launchIntent.getData(), launchIntent.getType());

        item = getCurrentItem(launchIntent);
        List<ResolveInfo> resInfo = getMatchingActivities(packageManager, intent);

        List<ResolveInfoDisplay> data = new ArrayList<>();

        int checked = -1;
        int index = -1;

        for (Iterator<ResolveInfo> iter = resInfo.listIterator(); iter.hasNext(); ) {
            ResolveInfo info = iter.next();

            if (info.activityInfo.packageName.equals(getPackageName())) {
                iter.remove();
                continue;
            }

            if (item.getHiddenApps().contains(new HiddenApp(info.activityInfo.packageName))) {
                iter.remove();
                continue;
            }

            index++;

            if (info.activityInfo.packageName.equals(item.getPackageName()) && info.activityInfo.name.equals(item.getClassName())) {
                if (item.isSkipList()) {
                    startIntentFromResolveInfo(info);
                    return true;
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

        if (resInfo.size() == 1) {
            startIntentFromResolveInfo(resInfo.get(0));
            return true;
        }

        configureAdapter(data, checked);
        return false;
    }

    private void configureAdapter(List<ResolveInfoDisplay> data, int checked) {
        adapter = new CommonAdapter<>(this, data, R.layout.resolve_list_item, new ResolveInfoDisplayFileHandlerViewBinder(this));
        adapterView.setAdapter(adapter);

        adapterView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        if (checked >= 0) {
            adapterView.setItemChecked(checked, true);
            adapterView.setSelection(checked);
        } else {
            adapterView.setItemChecked(0, true);
        }

        adapterView.setOnItemClickListener(this);
    }

    private List<ResolveInfo> getMatchingActivities(PackageManager packageManager, Intent intent) {
        List<ResolveInfo> resInfo = Utils.resolveIntent(packageManager, intent);

        //If only one app is found and it is us there is no other app.
        if (resInfo.size() == 1 && resInfo.get(0).activityInfo.packageName.equals(getPackageName())) {
            resInfo = Utils.resolveIntent(packageManager, getIntentWithRawSchemeUri(intent));

            if (resInfo.size() == 0 ||
                    (resInfo.size() == 1 && resInfo.get(0).activityInfo.packageName.equals(getPackageName()))) {
                Toast.makeText(this, R.string.cannot_open_file, Toast.LENGTH_LONG).show();
                finish();
            }
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
        return resInfo;
    }

    Intent getIntentWithRawSchemeUri(Intent intent) {
        return new Intent(intent).setData(getRawSchemeUri(intent.getData()));
    }

    Uri getRawSchemeUri(Uri uri) {
        return new Uri.Builder().scheme(uri.getScheme()).build();
    }

    private void launchDetailsActivity() {
        Intent detailsScreenIntent = getDetailsScreenIntent(this.item);
        detailsScreenIntent.putExtra("id", getItemId());
        startActivity(detailsScreenIntent);
    }

    private void toggleTimer() {
        if (paused) {
            configureTimer();
            pauseButton.setImageResource(isLight ? R.drawable.ic_pause_circle_outline_grey600_36dp : R.drawable.ic_pause_circle_outline_white_36dp);
            paused = false;
        } else {
            pauseTimer();
        }

        showTimerStatus();
    }

    private void pauseTimer() {
        autoStart.cancel();
        pauseButton.setImageResource(isLight ? R.drawable.ic_play_circle_outline_grey600_36dp : R.drawable.ic_play_circle_outline_white_36dp);
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

        List<HiddenApp> hiddenApps = cupboard().withDatabase(database)
                .query(HiddenApp.class).withSelection("itemId=?", String.valueOf(item.getId()))
                .query().list();

        item.setHiddenApps(new ArrayList<>(hiddenApps));

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

        if (!disableTimer) {
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
    }

    private void showTimerStatus() {
        updateCountDownTimer();

        if (paused) {
            pauseButton.setImageResource(isLight ? R.drawable.ic_play_circle_outline_grey600_36dp : R.drawable.ic_play_circle_outline_white_36dp);
        } else {
            pauseButton.setImageResource(isLight ? R.drawable.ic_pause_circle_outline_grey600_36dp : R.drawable.ic_pause_circle_outline_white_36dp);
        }
    }

    private void updateCountDownTimer() {
        timerCountDown.setProgress((int) ((timeout - elapsed) * 360.0 / timeout));
        tickerView.setText(String.valueOf((timeout - elapsed)));
    }

    private void configureTimer() {
        autoStart = new Timer("AutoStart");
        autoStart.schedule(new TimerTask() {
            @Override
            public void run() {
                elapsed++;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateCountDownTimer();
                    }
                });

                if (elapsed == timeout) {
                    int checkedItemPosition = adapterView.getCheckedItemPosition();
                    if (checkedItemPosition >= 0) {
                        ResolveInfoDisplay item = adapter.getItem(checkedItemPosition);
                        startIntentFromResolveInfo(item.getResolveInfo());
                    }
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

package com.aboutmycode.betteropenwith;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.aboutmycode.betteropenwith.common.YesNoDialogFragment;
import com.aboutmycode.betteropenwith.common.YesNoListener;
import com.aboutmycode.betteropenwith.common.adapter.CommonAdapter;
import com.aboutmycode.betteropenwith.common.adapter.IBindView;
import com.aboutmycode.betteropenwith.database.CupboardCursorLoader;
import com.aboutmycode.betteropenwith.database.CupboardSQLiteOpenHelper;
import com.aboutmycode.betteropenwith.database.HandleItemLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HandlerDetailsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<List<HandleItem>>, YesNoListener {

    private CommonAdapter<ResolveInfoDisplay> adapter;
    private CheckBox skipListCheckBox;
    private Switch masterSwitch;
    private HandleItem item;
    private HandleItemLoader loader;
    private ViewFlipper flipper;
    private boolean hideSwitch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.handler_details);
        flipper = (ViewFlipper) findViewById(R.id.view_flipper);

        loadData();

        ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    protected void loadData() {
        getLoaderManager().restartLoader(1, getIntent().getExtras(), this);
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        onListItemClick(listView, position, item, loader);
    }

    protected <T extends ItemBase> void onListItemClick(ListView listView, int position, T item, CupboardCursorLoader<T> loader) {
        ResolveInfoDisplay adapterItem = adapter.getItem(position);
        ActivityInfo activityInfo = adapterItem.getResolveInfo().activityInfo;

        if (activityInfo.name.equals(item.getClassName())) {
            //We clicked on already checked item
            listView.setItemChecked(position, false);
        }

        boolean defaultSelected = listView.getCheckedItemCount() > 0;
        skipListCheckBox.setEnabled(defaultSelected);

        if (defaultSelected) {
            item.setPackageName(activityInfo.applicationInfo.packageName);
            item.setClassName(activityInfo.name);
        } else {
            item.setPackageName("");
            item.setClassName("");
        }

        loader.update(item);
        setResult(RESULT_OK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.handler_details, menu);

        MenuItem menuItem = menu.findItem(R.id.toggleMenu);

        menuItem.setVisible(!hideSwitch);

        masterSwitch = (Switch) menuItem.getActionView().findViewById(R.id.toggleSwitch);
        if (item != null) {
            initializeMasterSwitch();
        }

        masterSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = masterSwitch.isChecked();
                if (checked) {
                    toggleHandlerState(checked);
                } else {
                    masterSwitch.setChecked(true);
                    FragmentManager fm = getFragmentManager();
                    String message = String.format(getString(R.string.confirm_disable), getTitle(), getString(R.string.app_name));
                    YesNoDialogFragment yesNoDialogFragment = YesNoDialogFragment.newInstance(message);
                    yesNoDialogFragment.show(fm, "YesNoDialogFragment");
                }
            }
        });
        return true;
    }

    private void toggleHandlerState(boolean enabled) {
        item.setEnabled(enabled);
        loader.update(item);
        setResult(RESULT_OK);

        PackageManager packageManager = getPackageManager();
        int state = enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        ComponentName component = new ComponentName(this, item.getAppComponentName());
        packageManager.setComponentEnabledSetting(component, state, PackageManager.DONT_KILL_APP);

        if (enabled) {
            flipper.setDisplayedChild(0);
        } else {
            flipper.setDisplayedChild(1);
        }
    }

    private void initializeMasterSwitch() {
        boolean enabled = item.isEnabled();
        masterSwitch.setChecked(enabled);

        if (hideSwitch) {
            flipper.setDisplayedChild(2);
        } else if (enabled) {
            flipper.setDisplayedChild(0);
        } else {
            flipper.setDisplayedChild(1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent upIntent = new Intent(this, HandlerListActivity.class);
            upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            upIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(upIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<HandleItem>> onCreateLoader(int loaderId, Bundle bundle) {
        loader = new HandleItemLoader(this, new CupboardSQLiteOpenHelper(this), "_id=?", bundle.getLong("id"));
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<HandleItem>> listLoader, List<HandleItem> handleItems) {
        if (loader == null) {
            loader = (HandleItemLoader) listLoader;
        }

        item = handleItems.get(0);

        setAppLaunchDetails(item);

        //region ActionBar and title
        Resources resources = getResources();

        String title = resources.getString(resources.getIdentifier(item.getNameResource(), "string", R.class.getPackage().getName()));
        setTitle(title);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(resources.getDrawable(resources.getIdentifier(item.getLightIconResource(), "drawable", R.class.getPackage().getName())));
        //endregion

        TextView disabledTextView = (TextView) findViewById(R.id.disabledTextView);
        disabledTextView.setText(String.format(getString(R.string.not_handled), title, getString(R.string.app_name)));

        TextView noAppsTextView = (TextView) findViewById(R.id.noAppsTextView);
        noAppsTextView.setText(String.format(getString(R.string.no_app), title));

        loadApps(item);
    }

    @Override
    public void onLoaderReset(Loader<List<HandleItem>> listLoader) {
    }

    protected void setAppLaunchDetails(ItemBase item) {
        Resources resources = getResources();

        skipListCheckBox = (CheckBox) findViewById(R.id.skipListCheckBox);
        skipListCheckBox.setEnabled(!TextUtils.isEmpty(item.getPackageName()));
        skipListCheckBox.setChecked(item.isSkipList());
        skipListCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skipChanged(skipListCheckBox.isChecked());
            }
        });

        setTimeoutText(resources, item);
    }

    protected void loadApps(ItemBase item) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String intentData = item.getIntentData();

        if (TextUtils.isEmpty(intentData)) {
            intent.setType(item.getIntentType());
        } else {
            intent.setDataAndType(Uri.parse(intentData), item.getIntentType());
        }

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> resInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (resInfo.size() == 1) {
            hideSwitch = true;
            invalidateOptionsMenu();
            flipper.setDisplayedChild(2);
            return;
        }

        if (masterSwitch != null) {
            initializeMasterSwitch();
        }

        Collections.sort(resInfo, new ResolveInfo.DisplayNameComparator(packageManager));

        List<ResolveInfoDisplay> list = new ArrayList<ResolveInfoDisplay>();

        int checked = -1;
        boolean subtract = false;

        for (int i = 0, resInfoSize = resInfo.size(); i < resInfoSize; i++) {
            ResolveInfo info = resInfo.get(i);
            if (info.activityInfo.packageName.equals(getPackageName())) {
                if (checked < 0) {
                    subtract = true;
                }
                continue;
            }

            if (info.activityInfo.packageName.equals(item.getPackageName()) && info.activityInfo.name.equals(item.getClassName())) {
                checked = i;
            }

            ResolveInfoDisplay resolveInfoDisplay = new ResolveInfoDisplay();
            resolveInfoDisplay.setDisplayLabel(info.loadLabel(packageManager));
            resolveInfoDisplay.setDisplayIcon(info.loadIcon(packageManager));
            resolveInfoDisplay.setResolveInfo(info);

            list.add(resolveInfoDisplay);
        }

        adapter = new CommonAdapter<ResolveInfoDisplay>(this, list, R.layout.resolve_info_checkable, new ResolveInfoDetailsActivityViewBinder());
        setListAdapter(adapter);

        if (checked >= 0) {
            checked -= subtract ? 1 : 0;
            getListView().setItemChecked(checked, true);
        }
    }

    private void setTimeoutText(Resources resources, ItemBase item) {
        TextView timeoutTextView = (TextView) findViewById(R.id.timeoutTextView);

        int timeout = PreferenceManager.getDefaultSharedPreferences(this).getInt("timeout", resources.getInteger(R.integer.default_timeout));

        if (item.isUseGlobalTimeout()) {
            timeoutTextView.setText(String.format(getString(R.string.countdown_time), timeout));
        } else {
            timeoutTextView.setText(String.format(getString(R.string.countdown_time), item.getCustomTimeout()));
        }
    }

    public void editTimeoutClicked(View view) {
        showTimeoutDialog(item);
    }

    protected void showTimeoutDialog(ItemBase item) {
        FragmentManager fm = getFragmentManager();
        TimeoutDialogFragment editTimeoutDialog = TimeoutDialogFragment.newInstance(item.isUseGlobalTimeout(), item.getCustomTimeout());
        editTimeoutDialog.show(fm, "TimeoutDialogFragment");
    }

    protected void skipChanged(boolean skipList) {
        updateSkipList(skipList, item, loader);
    }

    protected <T extends ItemBase> void updateSkipList(boolean skipList, T item, CupboardCursorLoader<T> loader) {
        item.setSkipList(skipListCheckBox.isChecked());
        loader.update(item);
        setResult(RESULT_OK);
    }

    protected void timeoutChanged(boolean useGlobal, int timeout) {
        updateTimeout(useGlobal, timeout, item, loader);
    }

    protected <T extends ItemBase> void updateTimeout(boolean useGlobal, int timeout, T item, CupboardCursorLoader<T> loader) {
        item.setUseGlobalTimeout(useGlobal);
        if (!useGlobal) {
            item.setCustomTimeout(timeout);
        }

        loader.update(item);
        setResult(RESULT_OK);

        setTimeoutText(getResources(), item);
    }

    @Override
    public void yesClicked() {
        toggleHandlerState(false);
        masterSwitch.setChecked(false);
    }
}

class ResolveInfoDetailsActivityViewBinder implements IBindView<ResolveInfoDisplay> {
    @Override
    public View bind(View row, ResolveInfoDisplay item, Context context) {
        ImageView image = (ImageView) row.findViewById(R.id.image);
        CheckedTextView checkedTextView = (CheckedTextView) row.findViewById(R.id.resolveCheckBox);

        image.setImageDrawable(item.getDisplayIcon());
        checkedTextView.setText(item.getDisplayLabel());

        return row;
    }
}
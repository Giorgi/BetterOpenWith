package com.aboutmycode.betteropenwith;

import android.app.ActionBar;
import android.app.AlertDialog;
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

import com.aboutmycode.betteropenwith.common.adapter.CommonAdapter;
import com.aboutmycode.betteropenwith.common.adapter.IBindView;
import com.aboutmycode.betteropenwith.database.CupboardCursorLoader;
import com.aboutmycode.betteropenwith.database.CupboardSQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HandlerDetailsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<List<HandleItem>> {

    private CommonAdapter<ResolveInfoDisplay> adapter;
    private CheckBox skipListCheckBox;
    private Switch masterSwitch;
    private HandleItem item;
    private CupboardCursorLoader loader;
    private ViewFlipper flipper;
    private boolean hideSwitch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.handler_details);
        flipper = (ViewFlipper) findViewById(R.id.view_flipper);

        getLoaderManager().initLoader(1, getIntent().getExtras(), this);

        ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
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
                item.setEnabled(checked);
                loader.update(item);

                PackageManager packageManager = getPackageManager();
                int state = checked ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                ComponentName component = new ComponentName(HandlerDetailsActivity.this, item.getAppComponentName());
                packageManager.setComponentEnabledSetting(component, state, PackageManager.DONT_KILL_APP);

                if (checked) {
                    flipper.setDisplayedChild(0);
                } else {
                    flipper.setDisplayedChild(1);
                }
            }
        });
        return true;
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
        loader = new CupboardCursorLoader(this, new CupboardSQLiteOpenHelper(this), "_id=?", bundle.getLong("id"));
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<HandleItem>> listLoader, List<HandleItem> handleItems) {
        if (loader == null) {
            loader = (CupboardCursorLoader) listLoader;
        }

        item = handleItems.get(0);

        //region skip list checkbox and timeout
        skipListCheckBox = (CheckBox) findViewById(R.id.skipListCheckBox);
        skipListCheckBox.setEnabled(!TextUtils.isEmpty(item.getPackageName()));
        skipListCheckBox.setChecked(item.isSkipList());
        skipListCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.setSkipList(skipListCheckBox.isChecked());
                loader.update(item);
            }
        });

        Resources resources = getResources();
        setTimeoutText(resources);

        //endregion

        //region ActionBar and title

        String title = resources.getString(resources.getIdentifier(item.getNameResource(), "string", R.class.getPackage().getName()));
        setTitle(title);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(resources.getDrawable(resources.getIdentifier(item.getLightIconResource(), "drawable", R.class.getPackage().getName())));
        //endregion

        TextView disabledTextView = (TextView) findViewById(R.id.disabledTextView);
        disabledTextView.setText(String.format("%s will not be handled with %s", title, getString(R.string.app_name)));

        TextView noAppsTextView = (TextView) findViewById(R.id.noAppsTextView);
        noAppsTextView.setText(String.format(getString(R.string.no_app), title));

        //region application list
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String intentData = item.getIntentData();

        if (TextUtils.isEmpty(intentData)) {
            intent.setType(item.getIntentType());
        } else {
            intent.setDataAndType(Uri.parse(intentData), item.getIntentType());
        }

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> resInfo = packageManager.queryIntentActivities(intent, 0);

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
        //endregion
    }

    private void setTimeoutText(Resources resources) {
        TextView timeoutTextView = (TextView) findViewById(R.id.timeoutTextView);

        int timeout = PreferenceManager.getDefaultSharedPreferences(this).getInt("timeout", resources.getInteger(R.integer.default_timeout));

        if (item.isUseGlobalTimeout()) {
            timeoutTextView.setText(String.format(getString(R.string.countdown_time), timeout));
        } else {
            timeoutTextView.setText(String.format(getString(R.string.countdown_time), item.getCustomTimeout()));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<HandleItem>> listLoader) {
    }

    public void editTimeoutClicked(View view) {
        FragmentManager fm = getFragmentManager();
        TimeoutDialogFragment editNameDialog = TimeoutDialogFragment.newInstance(item.isUseGlobalTimeout(), item.getCustomTimeout());
        editNameDialog.show(fm, "TimeoutDialogFragment");
    }

    public void timeoutChanged(boolean useGlobal, int timeout) {
        item.setUseGlobalTimeout(useGlobal);
        if (!useGlobal) {
            item.setCustomTimeout(timeout);
        }

        loader.update(item);

        setTimeoutText(getResources());
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
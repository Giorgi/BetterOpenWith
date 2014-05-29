package com.aboutmycode.openwith.app;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.aboutmycode.openwith.app.common.adapter.CommonAdapter;
import com.aboutmycode.openwith.app.common.adapter.IBindView;
import com.aboutmycode.openwith.app.database.CupboardCursorLoader;
import com.aboutmycode.openwith.app.database.CupboardSQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HandlerDetailsActivity extends ListActivity implements LoaderManager.LoaderCallbacks<List<HandleItem>> {

    private CommonAdapter<ResolveInfoDisplay> adapter;
    private CheckBox skipListCheckBox;
    private Switch masterSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.handler_details);

        getLoaderManager().initLoader(1, getIntent().getExtras(), this);

        ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(R.id.resolveCheckBox);
        if (checkedTextView.isChecked()) {
            listView.setItemChecked(position, false);
        }
        skipListCheckBox.setEnabled(listView.getCheckedItemCount() > 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.handler_details, menu);

        masterSwitch = (Switch) menu.findItem(R.id.toggleMenu).getActionView().findViewById(R.id.toggleSwitch);
        masterSwitch.setChecked(true);

        masterSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HandlerDetailsActivity.this, "toggled", Toast.LENGTH_LONG).show();
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<HandleItem>> onCreateLoader(int loaderId, Bundle bundle) {
        return new CupboardCursorLoader(this, new CupboardSQLiteOpenHelper(this), "_id=?", bundle.getLong("id"));
    }

    @Override
    public void onLoadFinished(Loader<List<HandleItem>> listLoader, List<HandleItem> handleItems) {
        HandleItem item = handleItems.get(0);

        //region skip list checkbox
        skipListCheckBox = (CheckBox) findViewById(R.id.skipListCheckBox);
        skipListCheckBox.setEnabled(item.getPackageName() != null);
        skipListCheckBox.setChecked(item.isSkipList());
        //endregion

        //region ActionBar and title
        Resources resources = getResources();

        setTitle(resources.getString(resources.getIdentifier(item.getNameResource(), "string", R.class.getPackage().getName())));

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(resources.getDrawable(resources.getIdentifier(item.getLightIconResource(), "drawable", R.class.getPackage().getName())));
        //endregion

        //region application list
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(item.getIntentData()), item.getIntentType());

        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> resInfo = packageManager.queryIntentActivities(intent, 0);

        Collections.sort(resInfo, new ResolveInfo.DisplayNameComparator(packageManager));

        List<ResolveInfoDisplay> list = new ArrayList<ResolveInfoDisplay>();

        for (ResolveInfo info : resInfo) {
            if (info.activityInfo.packageName.equals(getPackageName())) {
                continue;
            }

            ResolveInfoDisplay resolveInfoDisplay = new ResolveInfoDisplay();
            resolveInfoDisplay.setDisplayLabel(info.loadLabel(packageManager));
            resolveInfoDisplay.setDisplayIcon(info.loadIcon(packageManager));
            resolveInfoDisplay.setResolveInfo(info);

            list.add(resolveInfoDisplay);
        }

        adapter = new CommonAdapter<ResolveInfoDisplay>(this, list, R.layout.resolve_info_checkable, new ResolveInfoDetailsActivityViewBinder());
        setListAdapter(adapter);
        //endregion
    }

    @Override
    public void onLoaderReset(Loader<List<HandleItem>> listLoader) {

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
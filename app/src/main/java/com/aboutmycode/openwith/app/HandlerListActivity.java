package com.aboutmycode.openwith.app;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aboutmycode.openwith.app.common.adapter.CommonAdapter;
import com.aboutmycode.openwith.app.common.adapter.IBindView;
import com.aboutmycode.openwith.app.database.CupboardCursorLoader;
import com.aboutmycode.openwith.app.database.CupboardSQLiteOpenHelper;
import com.aboutmycode.openwith.app.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class HandlerListActivity extends ListActivity implements LoaderManager.LoaderCallbacks<List<HandleItem>> {
    private CommonAdapter<HandleItem> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        adapter = new CommonAdapter<HandleItem>(this, new ArrayList<HandleItem>(), R.layout.handler_types, new HandleItemViewBinder());
        getListView().setAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        Intent intent = new Intent(this, HandlerDetailsActivity.class);
        HandleItem item = adapter.getItem(position);
        intent.putExtra("id", item.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<HandleItem>> onCreateLoader(int loaderId, Bundle bundle) {
        return new CupboardCursorLoader(this, new CupboardSQLiteOpenHelper(this));
    }

    @Override
    public void onLoadFinished(Loader<List<HandleItem>> objectLoader, List<HandleItem> items) {
        Collections.sort(items, new Comparator<HandleItem>() {
            @Override
            public int compare(HandleItem item, HandleItem item2) {
                return item.getName().compareTo(item2.getName());
            }
        });
        adapter.setData(items);
    }

    @Override
    public void onLoaderReset(Loader<List<HandleItem>> objectLoader) {
        adapter.setData(null);
    }
}

class HandleItemViewBinder implements IBindView<HandleItem> {
    @Override
    public View bind(View row, HandleItem item, Context context) {
        TextView textView = (TextView) row.findViewById(R.id.label);
        TextView selectedAppTextView = (TextView) row.findViewById(R.id.selectedAppLabel);
        ImageView imageView = (ImageView) row.findViewById(R.id.targetIcon);

        Resources resources = context.getResources();

        textView.setText(item.getName());
        Drawable drawable = resources.getDrawable(resources.getIdentifier(item.getDarkIconResource(), "drawable", R.class.getPackage().getName()));
        //textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        imageView.setImageDrawable(drawable);

        Drawable selectedAppIcon = item.getSelectedAppIcon();
        if (selectedAppIcon != null) {
            selectedAppTextView.setText(item.getSelectedAppLabel());

            selectedAppIcon.setBounds(0, 0, 32, 32);
            selectedAppTextView.setCompoundDrawables(selectedAppIcon, null, null, null);
        }

        return row;
    }
}
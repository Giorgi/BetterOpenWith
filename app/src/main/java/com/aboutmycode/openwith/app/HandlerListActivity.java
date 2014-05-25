package com.aboutmycode.openwith.app;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
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
import com.aboutmycode.openwith.app.settings.SettingsActivity;

import java.util.ArrayList;


public class HandlerListActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        ArrayList<HandleItem> items = new ArrayList<HandleItem>();
        HandleItem pdf = new HandleItem();
        pdf.setName("PDF Files");
        pdf.setIcon(R.drawable.ic_adobe_acrobat);

        items.add(pdf);

        getListView().setAdapter(new CommonAdapter<HandleItem>(this, items, R.layout.handler_types, new HandleItemViewBinder()));
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        Intent intent = new Intent(this, HandlerDetailsActivity.class);
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
}

class HandleItemViewBinder implements IBindView<HandleItem> {

    @Override
    public View bind(View row, HandleItem item, Context context) {
        TextView textView = (TextView) row.findViewById(R.id.label);
        ImageView imageView = (ImageView) row.findViewById(R.id.icon);

        textView.setText(item.getName());
        imageView.setImageDrawable(context.getResources().getDrawable(item.getIcon()));

        return row;
    }
}


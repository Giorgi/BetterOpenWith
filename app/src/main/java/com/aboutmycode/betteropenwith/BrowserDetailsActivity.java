package com.aboutmycode.betteropenwith;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboutmycode.betteropenwith.R;

import java.util.ArrayList;

public class BrowserDetailsActivity extends HandlerDetailsActivity implements ActionBar.OnNavigationListener {

    private ActionBar actionBar;
    private ArrayList<SpinnerNavItem> navSpinner;
    private SiteNavigationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        navSpinner = new ArrayList<SpinnerNavItem>();
        navSpinner.add(new SpinnerNavItem("All sites", 0));
        navSpinner.add(new SpinnerNavItem("Youtube", R.drawable.youtube));
        navSpinner.add(new SpinnerNavItem("Twitter", R.drawable.twitter));
        navSpinner.add(new SpinnerNavItem("Facebook", R.drawable.facebook));
        navSpinner.add(new SpinnerNavItem("Google+", R.drawable.googleplusred));
        navSpinner.add(new SpinnerNavItem("Reddit", R.drawable.reddit));
        navSpinner.add(new SpinnerNavItem("Wikipedia", R.drawable.wikipedia));

        adapter = new SiteNavigationAdapter(getApplicationContext(), navSpinner);

        actionBar.setListNavigationCallbacks(adapter, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.browser_details, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        return false;
    }
}

class SiteNavigationAdapter extends BaseAdapter {

    private ImageView imgIcon;
    private TextView txtTitle;
    private ArrayList<SpinnerNavItem> spinnerNavItem;
    private Context context;

    public SiteNavigationAdapter(Context context, ArrayList<SpinnerNavItem> spinnerNavItem) {
        this.spinnerNavItem = spinnerNavItem;
        this.context = context;
    }

    @Override
    public int getCount() {
        return spinnerNavItem.size();
    }

    @Override
    public Object getItem(int index) {
        return spinnerNavItem.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.site_navigation_item, null);
        }

        imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
        txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);

        imgIcon.setImageResource(spinnerNavItem.get(position).getIcon());
        imgIcon.setVisibility(View.GONE);
        txtTitle.setText(spinnerNavItem.get(position).getTitle());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.site_navigation_item, null);
        }

        imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
        txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);

        int icon = spinnerNavItem.get(position).getIcon();
        txtTitle.setText(spinnerNavItem.get(position).getTitle());

        if (icon != 0) {
            imgIcon.setVisibility(View.VISIBLE);
            imgIcon.setImageResource(icon);
        } else {
            imgIcon.setVisibility(View.GONE);
        }

        return convertView;
    }
}
package com.aboutmycode.betteropenwith;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BrowserDetailsActivity extends HandlerDetailsActivity implements ActionBar.OnNavigationListener {

    private ActionBar actionBar;
    private ArrayList<SpinnerItem> navSpinner;
    private SiteNavigationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        navSpinner = new ArrayList<SpinnerItem>();
        navSpinner.add(new SpinnerItem("All sites", 0, 0, "http://aboutmycode.com"));
        navSpinner.add(new SpinnerItem("Youtube", R.drawable.youtube, 0, "http://youtube.com/"));
        navSpinner.add(new SpinnerItem("Twitter", R.drawable.twitter, 0, "http://twitter.com/"));
        navSpinner.add(new SpinnerItem("Facebook", R.drawable.facebook, 0, "http://facebook.com/"));
        navSpinner.add(new SpinnerItem("Play Store", R.drawable.play_store, 0, "https://play.google.com/store/apps/details?id=com.aboutmycode.betteropenwith"));
        navSpinner.add(new SpinnerItem("Google+", R.drawable.google_plus_red, 0, "https://plus.google.com/communities/110383670951588070492"));
        navSpinner.add(new SpinnerItem("Reddit", R.drawable.reddit, 0, "http://www.reddit.com/r/android"));
        navSpinner.add(new SpinnerItem("Wikipedia", R.drawable.wikipedia, 0, "http://en.wikipedia.org/wiki/Android"));

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
    private ArrayList<SpinnerItem> spinnerItem;
    private Context context;

    public SiteNavigationAdapter(Context context, ArrayList<SpinnerItem> spinnerItem) {
        this.spinnerItem = spinnerItem;
        this.context = context;
    }

    @Override
    public int getCount() {
        return spinnerItem.size();
    }

    @Override
    public Object getItem(int index) {
        return spinnerItem.get(index);
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

        imgIcon.setImageResource(spinnerItem.get(position).getIcon());
        imgIcon.setVisibility(View.GONE);
        txtTitle.setText(spinnerItem.get(position).getTitle());
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

        int icon = spinnerItem.get(position).getIcon();
        txtTitle.setText(spinnerItem.get(position).getTitle());

        if (icon != 0) {
            imgIcon.setVisibility(View.VISIBLE);
            imgIcon.setImageResource(icon);
        } else {
            imgIcon.setVisibility(View.GONE);
        }

        return convertView;
    }
}
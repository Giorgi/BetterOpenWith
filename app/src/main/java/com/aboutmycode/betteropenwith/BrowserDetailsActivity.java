package com.aboutmycode.betteropenwith;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aboutmycode.betteropenwith.database.CupboardCursorLoader;
import com.aboutmycode.betteropenwith.database.CupboardSQLiteOpenHelper;
import com.aboutmycode.betteropenwith.database.HandleItemLoader;

import java.util.ArrayList;
import java.util.List;

public class BrowserDetailsActivity extends HandlerDetailsActivity implements ActionBar.OnNavigationListener {
    private ActionBar actionBar;
    private ArrayList<SpinnerSite> navSpinner;
    private SiteNavigationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        navSpinner = new ArrayList<SpinnerSite>();
        navSpinner.add(new SpinnerSite("All sites", 0, 0, "http://aboutmycode.com"));
        navSpinner.add(new SpinnerSite("Youtube", R.drawable.youtube, 1, "http://youtube.com/"));
        navSpinner.add(new SpinnerSite("Twitter", R.drawable.twitter, 0, "http://twitter.com/"));
        navSpinner.add(new SpinnerSite("Facebook", R.drawable.facebook, 0, "http://facebook.com/"));
        navSpinner.add(new SpinnerSite("Play Store", R.drawable.play_store, 0, "https://play.google.com/store/apps/details?id=com.aboutmycode.betteropenwith"));
        navSpinner.add(new SpinnerSite("Google+", R.drawable.google_plus_red, 0, "https://plus.google.com/communities/110383670951588070492"));
        navSpinner.add(new SpinnerSite("Reddit", R.drawable.reddit, 0, "http://www.reddit.com/r/android"));
        navSpinner.add(new SpinnerSite("Wikipedia", R.drawable.wikipedia, 0, "http://en.wikipedia.org/wiki/Android"));

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
        if (itemId == 0) {
            return true;
        }

        Bundle extras = new Bundle();
        extras.putLong("id", itemId);
        getLoaderManager().initLoader(2, extras, new SiteLoaderCallbacks(this));

        return true;
    }
}

class SiteLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Site>> {
    Context context;

    SiteLoaderCallbacks(Context context) {
        this.context = context;
    }

    @Override
    public Loader<List<Site>> onCreateLoader(int id, Bundle bundle) {
        return new CupboardCursorLoader<Site>(context, new CupboardSQLiteOpenHelper(context), Site.class, "_id=?", bundle.getLong("id"));
    }

    @Override
    public void onLoadFinished(Loader<List<Site>> listLoader, List<Site> sites) {
        Site site = sites.get(0);

        HandlerDetailsActivity loaderContext = (HandlerDetailsActivity) context;

        loaderContext.setAppLaunchDetails(site);
        loaderContext.loadApps(site);
    }

    @Override
    public void onLoaderReset(Loader<List<Site>> listLoader) {

    }
}

class SiteNavigationAdapter extends BaseAdapter {

    private ImageView imgIcon;
    private TextView txtTitle;
    private ArrayList<SpinnerSite> spinnerSite;
    private Context context;

    public SiteNavigationAdapter(Context context, ArrayList<SpinnerSite> spinnerSite) {
        this.spinnerSite = spinnerSite;
        this.context = context;
    }

    @Override
    public int getCount() {
        return spinnerSite.size();
    }

    @Override
    public Object getItem(int index) {
        return spinnerSite.get(index);
    }

    @Override
    public long getItemId(int position) {
        return spinnerSite.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.site_navigation_item, null);
        }

        imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
        txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);

        imgIcon.setImageResource(spinnerSite.get(position).getIcon());
        imgIcon.setVisibility(View.GONE);
        txtTitle.setText(spinnerSite.get(position).getTitle());
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

        int icon = spinnerSite.get(position).getIcon();
        txtTitle.setText(spinnerSite.get(position).getTitle());

        if (icon != 0) {
            imgIcon.setVisibility(View.VISIBLE);
            imgIcon.setImageResource(icon);
        } else {
            imgIcon.setVisibility(View.GONE);
        }

        return convertView;
    }
}
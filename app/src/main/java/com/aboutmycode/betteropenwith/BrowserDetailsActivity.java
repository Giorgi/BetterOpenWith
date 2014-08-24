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
import android.widget.ListView;
import android.widget.TextView;

import com.aboutmycode.betteropenwith.database.CupboardCursorLoader;
import com.aboutmycode.betteropenwith.database.CupboardSQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class BrowserDetailsActivity extends HandlerDetailsActivity implements ActionBar.OnNavigationListener {
    private ActionBar actionBar;
    private ArrayList<SpinnerSite> navSpinner;
    private SiteNavigationAdapter adapter;
    private Site site;
    private CupboardCursorLoader<Site> loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getActionBar();

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        navSpinner = new ArrayList<SpinnerSite>();
        navSpinner.add(new SpinnerSite("All sites", 0, 0, "http://aboutmycode.com"));
        navSpinner.add(new SpinnerSite("Youtube", R.drawable.youtube, 1, "http://youtube.com/"));
        navSpinner.add(new SpinnerSite("Twitter", R.drawable.twitter, 2, "http://twitter.com/"));
        navSpinner.add(new SpinnerSite("Play Store", R.drawable.play_store, 4, "https://play.google.com/store/apps/details?id=com.aboutmycode.betteropenwith"));
        navSpinner.add(new SpinnerSite("Google+", R.drawable.google_plus_red, 5, "https://plus.google.com/communities/110383670951588070492"));
        navSpinner.add(new SpinnerSite("Reddit", R.drawable.reddit, 6, "http://www.reddit.com/r/android"));
        navSpinner.add(new SpinnerSite("Wikipedia", R.drawable.wikipedia, 7, "http://en.wikipedia.org/wiki/Android"));

        adapter = new SiteNavigationAdapter(getApplicationContext(), navSpinner);

        actionBar.setListNavigationCallbacks(adapter, this);
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        if (site == null) {
            super.onListItemClick(listView, view, position, id);
        } else {
            onListItemClick(listView, position, site, loader);
        }
    }

    @Override
    protected void skipChanged(boolean skipList) {
        updateSkipList(skipList, site, loader);
    }

    @Override
    protected void timeoutChanged(boolean useGlobal, int timeout) {
        if (site == null) {
            super.timeoutChanged(useGlobal, timeout);
        } else {
            updateTimeout(useGlobal, timeout, site, loader);
        }
    }

    @Override
    public void editTimeoutClicked(View view) {
        if (site == null) {
            super.editTimeoutClicked(view);
        } else {
            showTimeoutDialog(site);
        }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (itemId == 0) {
            site = null;
            loadData();
            return true;
        }

        Bundle extras = new Bundle();
        extras.putLong("id", itemId);
        getLoaderManager().restartLoader(2, extras, new SiteLoaderCallbacks());

        return true;
    }

    class SiteLoaderCallbacks implements LoaderManager.LoaderCallbacks<List<Site>> {
        @Override
        public Loader<List<Site>> onCreateLoader(int id, Bundle bundle) {
            loader = new CupboardCursorLoader<Site>(BrowserDetailsActivity.this, new CupboardSQLiteOpenHelper(BrowserDetailsActivity.this),
                    Site.class, "_id=?", bundle.getLong("id"));
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<List<Site>> listLoader, List<Site> sites) {
            if (loader == null) {
                loader = (CupboardCursorLoader<Site>) listLoader;
            }
            Site site = sites.get(0);

            BrowserDetailsActivity browserDetailsActivity = BrowserDetailsActivity.this;
            browserDetailsActivity.site = site;
            browserDetailsActivity.setAppLaunchDetails(site);
            browserDetailsActivity.loadApps(site);
        }

        @Override
        public void onLoaderReset(Loader<List<Site>> listLoader) {

        }
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
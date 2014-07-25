package com.aboutmycode.betteropenwith;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.aboutmycode.betteropenwith.common.adapter.CommonAdapter;
import com.aboutmycode.betteropenwith.common.adapter.IBindView;
import com.aboutmycode.betteropenwith.database.CupboardCursorLoader;
import com.aboutmycode.betteropenwith.database.CupboardSQLiteOpenHelper;
import com.aboutmycode.betteropenwith.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.gmariotti.changelibs.library.view.ChangeLogListView;

public class HandlerListActivity extends ListActivity implements LoaderManager.LoaderCallbacks<List<HandleItem>> {
    private CommonAdapter<HandleItem> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        getLoaderManager().initLoader(0, null, this);

        adapter = new CommonAdapter<HandleItem>(this, new ArrayList<HandleItem>(), R.layout.handler_types, new HandleItemViewBinder());
        getListView().setAdapter(adapter);

        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            int lastVersion = preferences.getInt("version", -1);
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            if (packageInfo.versionCode > lastVersion) {
                ChangelogDialogFragment dialogStandardFragment = new ChangelogDialogFragment();
                FragmentManager fm = getFragmentManager();
                Fragment prev = fm.findFragmentByTag("ChangelogDialogFragment");
                if (prev != null) {
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.remove(prev);
                }

                dialogStandardFragment.show(fm, "ChangelogDialogFragment");
            }

            preferences.edit().putInt("version", packageInfo.versionCode).commit();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        Intent intent = new Intent(this, HandlerDetailsActivity.class);
        HandleItem item = adapter.getItem(position);
        intent.putExtra("id", item.getId());
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem actionItem = menu.findItem(R.id.action_share);
        ShareActionProvider actionProvider = (ShareActionProvider) actionItem.getActionProvider();
        actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        actionProvider.setShareIntent(createShareIntent());
        
        return true;
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_text));
        shareIntent.putExtra(Intent.EXTRA_TEXT, String.format("https://play.google.com/store/apps/details?id=%s", getPackageName()));
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_feedback) {
            SendFeedbackEmail();
            return true;
        }

        if (id == R.id.action_rate) {
            Uri marketUri = Uri.parse(String.format("market://details?id=%s", getPackageName()));
            startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
            return true;
        }

        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_all_apps) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://search?q=pub:Giorgi Dalakishvili"));

            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void SendFeedbackEmail() {
        String address = "android@aboutmycode.com";
        String subject = "Feedback for Better Open With";
        Uri mailto = Uri.fromParts("mailto", address, null);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, mailto);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_EMAIL, address);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);

            startActivity(intent);
        }
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
        imageView.setImageDrawable(drawable);

        if (!item.isEnabled()) {
            selectedAppTextView.setText(context.getString(R.string.turned_off));
            selectedAppTextView.setCompoundDrawables(null, null, null, null);
            return row;
        }

        Drawable selectedAppIcon = item.getSelectedAppIcon();
        if (selectedAppIcon == null) {
            selectedAppTextView.setText(context.getString(R.string.no_preferred_app));
            selectedAppTextView.setCompoundDrawables(null, null, null, null);
        } else {
            int timeout = PreferenceManager.getDefaultSharedPreferences(context).getInt("timeout", resources.getInteger(R.integer.default_timeout));

            if (!item.isUseGlobalTimeout()) {
                timeout = item.getCustomTimeout();
            }

            selectedAppTextView.setText(String.format(context.getString(R.string.app_seconds), item.getSelectedAppLabel(), timeout));

            selectedAppIcon.setBounds(0, 0, 32, 32);
            selectedAppTextView.setCompoundDrawables(selectedAppIcon, null, null, null);
        }

        return row;
    }
}